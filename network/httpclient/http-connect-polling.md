## [HTTP Keep Alive](../basic/http-keep-alive.md)

## HttpClient如何生成持久连接

HttpClien中使用了连接池来管理持有连接，同一条TCP链路上，连接是可以复用的。HttpClient通过连接池的方式进行连接持久化。

其实“池”技术是一种通用的设计，其设计思想并不复杂：

- 当有连接第一次使用的时候建立连接
- 结束时对应连接不关闭，归还到池中
- 下次同个目的的连接可从池中获取一个可用连接
- 定期清理过期连接

所有的连接池都是这个思路，不过我们看HttpClient源码主要关注两点：

- 连接池的具体设计方案，以供以后自定义连接池参考
- 如何与HTTP协议对应上，即理论抽象转为代码的实现

### HttpClient连接池的实现

HttpClient关于持久连接的处理在下面的代码中可以集中体现，下面从MainClientExec摘取了和连接池相关的部分，去掉了其他部分：

```java

public class MainClientExec implements ClientExecChain {

 @Override
 public CloseableHttpResponse execute(
  final HttpRoute route,
  final HttpRequestWrapper request,
  final HttpClientContext context,
  final HttpExecutionAware execAware) throws IOException, HttpException {
　　　　　//从连接管理器HttpClientConnectionManager中获取一个连接请求ConnectionRequest
 final ConnectionRequest connRequest = connManager.requestConnection(route, userToken);final HttpClientConnection managedConn;
 final int timeout = config.getConnectionRequestTimeout(); //从连接请求ConnectionRequest中获取一个被管理的连接HttpClientConnection
 managedConn = connRequest.get(timeout > 0 ? timeout : 0, TimeUnit.MILLISECONDS);
　　　　 //将连接管理器HttpClientConnectionManager与被管理的连接HttpClientConnection交给一个ConnectionHolder持有
 final ConnectionHolder connHolder = new ConnectionHolder(this.log, this.connManager, managedConn);
 try {
  HttpResponse response;
  if (!managedConn.isOpen()) {　　　　　　　　　　//如果当前被管理的连接不是出于打开状态，需要重新建立连接
  establishRoute(proxyAuthState, managedConn, route, request, context);
  }
　　　　　　　//通过连接HttpClientConnection发送请求
  response = requestExecutor.execute(request, managedConn, context);
　　　　　　　//通过连接重用策略判断是否连接可重用  
  if (reuseStrategy.keepAlive(response, context)) {
  //获得连接有效期
  final long duration = keepAliveStrategy.getKeepAliveDuration(response, context);
  //设置连接有效期
  connHolder.setValidFor(duration, TimeUnit.MILLISECONDS);　　　　　　　　　 //将当前连接标记为可重用状态
  connHolder.markReusable();
  } else {
  connHolder.markNonReusable();
  }
 }
 final HttpEntity entity = response.getEntity();
 if (entity == null || !entity.isStreaming()) {
  //将当前连接释放到池中，供下次调用
  connHolder.releaseConnection();
  return new HttpResponseProxy(response, null);
 } else {
  return new HttpResponseProxy(response, connHolder);
 }
}
```

这里看到了在Http请求过程中对连接的处理是和协议规范是一致的，这里要展开讲一下具体实现。

PoolingHttpClientConnectionManager是HttpClient默认的连接管理器，首先通过requestConnection()获得一个连接的请求，注意这里不是连接。

```java

public ConnectionRequest requestConnection(
  final HttpRoute route,
  final Object state) {final Future<CPoolEntry> future = this.pool.lease(route, state, null);
 return new ConnectionRequest() {
  @Override
  public boolean cancel() {
  return future.cancel(true);
  }
  @Override
  public HttpClientConnection get(
   final long timeout,
   final TimeUnit tunit) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException {
  final HttpClientConnection conn = leaseConnection(future, timeout, tunit);
  if (conn.isOpen()) {
   final HttpHost host;
   if (route.getProxyHost() != null) {
   host = route.getProxyHost();
   } else {
   host = route.getTargetHost();
   }
   final SocketConfig socketConfig = resolveSocketConfig(host);
   conn.setSocketTimeout(socketConfig.getSoTimeout());
  }
  return conn;
  }
 };
 }
```
可以看到返回的ConnectionRequest对象实际上是一个持有了Future<CPoolEntry>，CPoolEntry是被连接池管理的真正连接实例。

从上面的代码我们应该关注的是：

Future<CPoolEntry> future = this.pool.lease(route, state, null)
　　如何从连接池CPool中获得一个异步的连接，Future<CPoolEntry>
HttpClientConnection conn = leaseConnection(future, timeout, tunit)
　　如何通过异步连接Future<CPoolEntry>获得一个真正的连接HttpClientConnection

### Future<CPoolEntry>

看一下CPool是如何释放一个Future<CPoolEntry>的，AbstractConnPool核心代码如下：

```java
private E getPoolEntryBlocking(
  final T route, final Object state,
  final long timeout, final TimeUnit tunit,
  final Future<E> future) throws IOException, InterruptedException, TimeoutException {
　　　　　//首先对当前连接池加锁，当前锁是可重入锁ReentrantLockthis.lock.lock();
 try {　　　　　　　 //获得一个当前HttpRoute对应的连接池，对于HttpClient的连接池而言，总池有个大小，每个route对应的连接也是个池，所以是“池中池”
  final RouteSpecificPool<T, C, E> pool = getPool(route);
  E entry;
  for (;;) {
  Asserts.check(!this.isShutDown, "Connection pool shut down");　　　　　　　　　　//死循环获得连接
  for (;;) {　　　　　　　　　　　　//从route对应的池中拿连接，可能是null，也可能是有效连接
   entry = pool.getFree(state);　　　　　　　　　　　　//如果拿到null，就退出循环
   if (entry == null) {
   break;
   }　　　　　　　　　　　　//如果拿到过期连接或者已关闭连接，就释放资源，继续循环获取
   if (entry.isExpired(System.currentTimeMillis())) {
   entry.close();
   }
   if (entry.isClosed()) {
   this.available.remove(entry);
   pool.free(entry, false);
   } else {　　　　　　　　　　　　　　//如果拿到有效连接就退出循环
   break;
   }
  }　　　　　　　　　　//拿到有效连接就退出
  if (entry != null) {
   this.available.remove(entry);
   this.leased.add(entry);
   onReuse(entry);
   return entry;
  }
　　　　　　　　　 //到这里证明没有拿到有效连接，需要自己生成一个  
  final int maxPerRoute = getMax(route);
  //每个route对应的连接最大数量是可配置的，如果超过了，就需要通过LRU清理掉一些连接
  final int excess = Math.max(0, pool.getAllocatedCount() + 1 - maxPerRoute);
  if (excess > 0) {
   for (int i = 0; i < excess; i++) {
   final E lastUsed = pool.getLastUsed();
   if (lastUsed == null) {
    break;
   }
   lastUsed.close();
   this.available.remove(lastUsed);
   pool.remove(lastUsed);
   }
  }
　　　　　　　　　 //当前route池中的连接数，没有达到上线
  if (pool.getAllocatedCount() < maxPerRoute) {
   final int totalUsed = this.leased.size();
   final int freeCapacity = Math.max(this.maxTotal - totalUsed, 0);　　　　　　　　　　　　//判断连接池是否超过上线，如果超过了，需要通过LRU清理掉一些连接
   if (freeCapacity > 0) {
   final int totalAvailable = this.available.size();　　　　　　　　　　　　　　 //如果空闲连接数已经大于剩余可用空间，则需要清理下空闲连接
   if (totalAvailable > freeCapacity - 1) {
    if (!this.available.isEmpty()) {
    final E lastUsed = this.available.removeLast();
    lastUsed.close();
    final RouteSpecificPool<T, C, E> otherpool = getPool(lastUsed.getRoute());
    otherpool.remove(lastUsed);
    }
   }　　　　　　　　　　　　　　//根据route建立一个连接
   final C conn = this.connFactory.create(route);　　　　　　　　　　　　　　//将这个连接放入route对应的“小池”中
   entry = pool.add(conn);　　　　　　　　　　　　　　//将这个连接放入“大池”中
   this.leased.add(entry);
   return entry;
   }
  }
　　　　　　　　　//到这里证明没有从获得route池中获得有效连接，并且想要自己建立连接时当前route连接池已经到达最大值，即已经有连接在使用，但是对当前线程不可用
  boolean success = false;
  try {
   if (future.isCancelled()) {
   throw new InterruptedException("Operation interrupted");
   }　　　　　　　　　　　　//将future放入route池中等待
   pool.queue(future);　　　　　　　　　　　　//将future放入大连接池中等待
   this.pending.add(future);　　　　　　　　　　　　//如果等待到了信号量的通知,success为true
   if (deadline != null) {
   success = this.condition.awaitUntil(deadline);
   } else {
   this.condition.await();
   success = true;
   }
   if (future.isCancelled()) {
   throw new InterruptedException("Operation interrupted");
   }
  } finally {
   //从等待队列中移除
   pool.unqueue(future);
   this.pending.remove(future);
  }
  //如果没有等到信号量通知并且当前时间已经超时，则退出循环
  if (!success && (deadline != null && deadline.getTime() <= System.currentTimeMillis())) {
   break;
  }
  }　　　　　　　//最终也没有等到信号量通知，没有拿到可用连接，则抛异常
  throw new TimeoutException("Timeout waiting for connection");
 } finally {　　　　　　　//释放对大连接池的锁
  this.lock.unlock();
 }
 }

```

上面的代码逻辑有几个重要点：

- 连接池有个最大连接数，每个route对应一个小连接池，也有个最大连接数
- 不论是大连接池还是小连接池，当超过数量的时候，都要通过LRU释放一些连接
- 如果拿到了可用连接，则返回给上层使用
- 如果没有拿到可用连接，HttpClient会判断当前route连接池是否已经超过了最大数量，没有到上限就会新建一个连接，并放入池中
- 如果到达了上限，就排队等待，等到了信号量，就重新获得一次，等待不到就抛超时异常
- 通过线程池获取连接要通过ReetrantLock加锁，保证线程安全

到这里为止，程序已经拿到了一个可用的CPoolEntry实例，或者抛异常终止了程序。

### HttpClientConnection

```java
protected HttpClientConnection leaseConnection(
  final Future<CPoolEntry> future,
  final long timeout,
  final TimeUnit tunit) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException {
 final CPoolEntry entry;
 try {　　　　　　　//从异步操作Future<CPoolEntry>中获得CPoolEntry
  entry = future.get(timeout, tunit);
  if (entry == null || future.isCancelled()) {
  throw new InterruptedException();
  }
  Asserts.check(entry.getConnection() != null, "Pool entry with no connection");
  if (this.log.isDebugEnabled()) {
  this.log.debug("Connection leased: " + format(entry) + formatStats(entry.getRoute()));
  }　　　　　　　//获得一个CPoolEntry的代理对象，对其操作都是使用同一个底层的HttpClientConnection
  return CPoolProxy.newProxy(entry);
 } catch (final TimeoutException ex) {
  throw new ConnectionPoolTimeoutException("Timeout waiting for connection from pool");
 }
 }

```

### HttpClient如何复用持久连接？

在上一章中，我们看到了HttpClient通过连接池来获得连接，当需要使用连接的时候从池中获得。

对应着第三章的问题：

- 当有连接第一次使用的时候建立连接
- 结束时对应连接不关闭，归还到池中
- 下次同个目的的连接可从池中获取一个可用连接
- 定期清理过期连接

我们在第四章中看到了HttpClient是如何处理1、3的问题的，那么第2个问题是怎么处理的呢？

即HttpClient如何判断一个连接在使用完毕后是要关闭，还是要放入池中供他人复用？再看一下MainClientExec的代码

```java
//发送Http连接  response = requestExecutor.execute(request, managedConn, context);
  //根据重用策略判断当前连接是否要复用
  if (reuseStrategy.keepAlive(response, context)) {
   //需要复用的连接，获取连接超时时间，以response中的timeout为准
   final long duration = keepAliveStrategy.getKeepAliveDuration(response, context);
   if (this.log.isDebugEnabled()) {
   final String s;　　　　　　　　　　　　　　 //timeout的是毫秒数，如果没有设置则为-1，即没有超时时间
   if (duration > 0) {
    s = "for " + duration + " " + TimeUnit.MILLISECONDS;
   } else {
    s = "indefinitely";
   }
   this.log.debug("Connection can be kept alive " + s);
   }　　　　　　　　　　　　//设置超时时间，当请求结束时连接管理器会根据超时时间决定是关闭还是放回到池中
   connHolder.setValidFor(duration, TimeUnit.MILLISECONDS);
   //将连接标记为可重用　　　　　　　　　　　　connHolder.markReusable();
  } else {　　　　　　　　　　　　//将连接标记为不可重用
   connHolder.markNonReusable();
  }

```

可以看到，当使用连接发生过请求之后，有连接重试策略来决定该连接是否要重用，如果要重用就会在结束后交给HttpClientConnectionManager放入池中。

那么连接复用策略的逻辑是怎么样的呢？

```java
public class DefaultClientConnectionReuseStrategy extends DefaultConnectionReuseStrategy {

 public static final DefaultClientConnectionReuseStrategy INSTANCE = new DefaultClientConnectionReuseStrategy();

 @Override
 public boolean keepAlive(final HttpResponse response, final HttpContext context) {
　　　　　//从上下文中拿到request
  final HttpRequest request = (HttpRequest) context.getAttribute(HttpCoreContext.HTTP_REQUEST);
  if (request != null) {　　　　　　　//获得Connection的Header
   final Header[] connHeaders = request.getHeaders(HttpHeaders.CONNECTION);
   if (connHeaders.length != 0) {
    final TokenIterator ti = new BasicTokenIterator(new BasicHeaderIterator(connHeaders, null));
    while (ti.hasNext()) {
     final String token = ti.nextToken();　　　　　　　　　　　　//如果包含Connection:Close首部，则代表请求不打算保持连接，会忽略response的意愿，该头部这是HTTP/1.1的规范
     if (HTTP.CONN_CLOSE.equalsIgnoreCase(token)) {
      return false;
     }
    }
   }
  }　　　　 //使用父类的的复用策略
  return super.keepAlive(response, context);
 }
}
```
看一下父类的复用策略

```java

if (canResponseHaveBody(request, response)) {
    final Header[] clhs = response.getHeaders(HTTP.CONTENT_LEN);
    //如果reponse的Content-Length没有正确设置，则不复用连接　　　　　　　　　 //因为对于持久化连接，两次传输之间不需要重新建立连接，则需要根据Content-Length确认内容属于哪次请求，以正确处理“粘包”现象    //所以，没有正确设置Content-Length的response连接不能复用
    if (clhs.length == 1) {
     final Header clh = clhs[0];
     try {
      final int contentLen = Integer.parseInt(clh.getValue());
      if (contentLen < 0) {
       return false;
      }
     } catch (final NumberFormatException ex) {
      return false;
     }
    } else {
     return false;
    }
   }
  if (headerIterator.hasNext()) {
   try {
    final TokenIterator ti = new BasicTokenIterator(headerIterator);
    boolean keepalive = false;
    while (ti.hasNext()) {
     final String token = ti.nextToken();　　　　　　　　　　　　//如果response有Connection:Close首部，则明确表示要关闭，则不复用
     if (HTTP.CONN_CLOSE.equalsIgnoreCase(token)) {
      return false;　　　　　　　　　　　　//如果response有Connection:Keep-Alive首部，则明确表示要持久化，则复用
     } else if (HTTP.CONN_KEEP_ALIVE.equalsIgnoreCase(token)) {
      keepalive = true;
     }
    }
    if (keepalive) {
     return true;
    }
   } catch (final ParseException px) {
    return false;
   }
  }
　　　　　//如果response中没有相关的Connection首部说明，则高于HTTP/1.0版本的都复用连接 
  return !ver.lessEquals(HttpVersion.HTTP_1_0);
```

总结一下：

- 如果request首部中包含Connection:Close，不复用
- 如果response中Content-Length长度设置不正确，不复用
- 如果response首部包含Connection:Close，不复用
- 如果reponse首部包含Connection:Keep-Alive，复用
- 都没命中的情况下，如果HTTP版本高于1.0则复用

从代码中可以看到，其实现策略与我们第二、三章协议层的约束是一致的。

### HttpClient如何清理过期连接

在HttpClient4.4版本之前，在从连接池中获取重用连接的时候会检查下是否过期，过期则清理。

之后的版本则不同，会有一个单独的线程来扫描连接池中的连接，发现有离最近一次使用超过设置的时间后，就会清理。默认的超时时间是2秒钟。

```java
public CloseableHttpClient build() {   //如果指定了要清理过期连接与空闲连接，才会启动清理线程，默认是不启动的
   if (evictExpiredConnections || evictIdleConnections) {　　　　　　　　　　//创造一个连接池的清理线程
    final IdleConnectionEvictor connectionEvictor = new IdleConnectionEvictor(cm,
      maxIdleTime > 0 ? maxIdleTime : 10, maxIdleTimeUnit != null ? maxIdleTimeUnit : TimeUnit.SECONDS,
      maxIdleTime, maxIdleTimeUnit);
    closeablesCopy.add(new Closeable() {
     @Override
     public void close() throws IOException {
      connectionEvictor.shutdown();
      try {
       connectionEvictor.awaitTermination(1L, TimeUnit.SECONDS);
      } catch (final InterruptedException interrupted) {
       Thread.currentThread().interrupt();
      }
     }

    });　　　　　　　　　　//执行该清理线程
    connectionEvictor.start();
}

```

可以看到在HttpClientBuilder进行build的时候,如果指定了开启清理功能，会创建一个连接池清理线程并运行它。

```java
public IdleConnectionEvictor(
   final HttpClientConnectionManager connectionManager,
   final ThreadFactory threadFactory,
   final long sleepTime, final TimeUnit sleepTimeUnit,
   final long maxIdleTime, final TimeUnit maxIdleTimeUnit) {
  this.connectionManager = Args.notNull(connectionManager, "Connection manager");
  this.threadFactory = threadFactory != null ? threadFactory : new DefaultThreadFactory();
  this.sleepTimeMs = sleepTimeUnit != null ? sleepTimeUnit.toMillis(sleepTime) : sleepTime;
  this.maxIdleTimeMs = maxIdleTimeUnit != null ? maxIdleTimeUnit.toMillis(maxIdleTime) : maxIdleTime;
  this.thread = this.threadFactory.newThread(new Runnable() {
   @Override
   public void run() {
    try {　　　　　　　　　　　　//死循环，线程一直执行
     while (!Thread.currentThread().isInterrupted()) {　　　　　　　　　　　　　　//休息若干秒后执行，默认10秒
      Thread.sleep(sleepTimeMs);　　　　　　　　　　　　　　 //清理过期连接
      connectionManager.closeExpiredConnections();　　　　　　　　　　　　　　 //如果指定了最大空闲时间，则清理空闲连接
      if (maxIdleTimeMs > 0) {
       connectionManager.closeIdleConnections(maxIdleTimeMs, TimeUnit.MILLISECONDS);
      }
     }
    } catch (final Exception ex) {
     exception = ex;
    }

   }
  });
 }
```

总结一下：

- 只有在HttpClientBuilder手动设置后，才会开启清理过期与空闲连接
- 手动设置后，会启动一个线程死循环执行，每次执行sleep一定时间，调用HttpClientConnectionManager的清理方法清理过期与空闲连接。

### 本文总结
- HTTP协议通过持久连接的方式，减轻了早期设计中的过多连接问题
- 持久连接有两种方式：HTTP/1.0+的Keep-Avlive与HTTP/1.1的默认持久连接
- HttpClient通过连接池来管理持久连接，连接池分为两个，一个是总连接池，一个是每个route对应的连接池
- HttpClient通过异步的Future<CPoolEntry>来获取一个池化的连接
- 默认连接重用策略与HTTP协议约束一致，根据response先判断Connection:Close则关闭，在判断Connection:Keep-Alive则开启，最后版本大于1.0则开启
- 只有在HttpClientBuilder中手动开启了清理过期与空闲连接的开关后，才会清理连接池中的连接
- HttpClient4.4之后的版本通过一个死循环线程清理过期与空闲连接，该线程每次执行都sleep一会，以达到定期执行的效果 

## 参考资料
https://www.jb51.net/article/141015.htm
