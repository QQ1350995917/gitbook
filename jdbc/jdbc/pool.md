# JDBC POOL

池(Pool)技术在一定程度上可以明显优化服务器应用程序的性能，提高程序执行效率和降低系统资源开销。这里所说的池是一种广义上的池，比如数据库连接池、线程池、内存池、对象池等。其中，对象池可以看成保存对象的容器，在进程初始化时创建一定数量的对象。需要时直接从池中取出一个空闲对象，用完后并不直接释放掉对象，而是再放到对象池中以方便下一次对象请求可以直接复用。其他几种池的设计思想也是如此，池技术的优势是，可以消除对象创建所带来的延迟，从而提高系统的性能。

要了解Java连接池我们先要了解数据库连接池（connection pool）的原理，Java连接池正是数据库连接池在Java上的应用。——我们知道，对于共享资源，有一个很著名的设计模式：资源池（Resource Pool）。

该模式正是为了解决资源的频繁分配﹑释放所造成的问题。为解决上述问题，可以采用数据库连接池技术。数据库连接池的基本思想就是为数据库连接建立一个“缓冲池”。预先在缓冲池中放入一定数量的连接，当需要建立数据库连接时，只需从“缓冲池”中取出一个，使用完毕之后再放回去。我们可以通过设定连接池最大连接数来防止系统无尽的与数据库连接。更为重要的是我们可以通过连接池的管理机制监视数据库的连接的数量﹑使用情况，为系统开发﹑测试及性能调整提供依据。

所谓数据库连接池，可以看作 ：在用户和数据库之间创建一个”池”，这个池中有若干个连接对象，当用户想要连接数据库，就要先从连接池中获取连接对象，然后操作数据库。一旦连接池中的连接对象被拿光了，下一个想要操作数据库的用户必须等待，等待其他用户释放连接对象，把它放回连接池中，这时候等待的用户才能获取连接对象，从而操作数据库。

可以通过以上描述实现<a href="#1">完全自定义一个连接池</a>，该例子就实现了自己的一个连接池，但是这个连接池依然存在着很多问题，一个较为明显的问题就是：如果一个用户获取了一个连接对象，然后调用了close()方法把它关闭了，没有放回池中，这样池中的这个对象就回不来了，造成最大连接上限为8个的连接池实际上只有7个连接在工作。有两种方法解决该问题，
- 方法一：使用静态代理，写一个myConnection()类来继承connection的实现类，然后重写它的close()方法;
- 方法二：使用动态代理，使用jdbc动态代理类：java.lang.reflect.Proxy类.但是即使解决了以上问题还有其他的问题：1）当前多个并发用户同时获取连接时，可能拿到同一个连接对象；2）当前用户连接数超过了最大连接数时，不能直接抛出异常，应该有机制，控制用户等待时间等。

可以通过javax.sql.DataSource实现<a href="#2">扩展自定义一个连接池</a>，该例子扩展了DataSource，但是也要实现很多的功能，使用起来也不方便。

此时一些功能完善的第三方连接池就出现了。

- [DBCP（Database Connection Pool）连接池](http://commons.apache.org/dbcp/)：Tomcat的数据源使用的就是DBCP。DBCP是一个依赖Jakarta commons-pool对象池机制的数据库连接池，因此需要引入commons-dbcp.jar包和commons-pool.jar包。在具体项目应用中，发现此连接池的持续运行的稳定性还是可以，不过速度稍慢，在大并发量的压力下稳定性有所下降，此外不提供连接池监控

- [C3P0连接池](http://sourceforge.net/projects/c3p0/):C3P0是一个开源组织的产品，开源框架的内部的连接池一般都使用C3P0来实现，例如：Hibernate。需要引入c3p0.jar 包。在具体项目应用中，发现此连接池的持续运行的稳定性相当不错，在大并发量的压力下稳定性也有一定保证，此外不提供连接池监控。

- [Druid连接池](http://druid.io): 是阿里巴巴开发的号称为监控而生的数据库连接池，不止一个数据库连接池，在功能、性能、扩展性方面，都超过其他数据库连接池。包括三个部分：（1）基于Filter－Chain模式的插件体系；（2）DruidDataSource 高效可管理的数据库连接池;（3）SQLParser;具体配置可见[Druid高效架构](https://www.jianshu.com/p/7a26d9153455);[Druid配置以及介绍](https://www.cnblogs.com/niejunlei/p/5977895.html);

- [BoneCP连接池]():是一个开源的快速的 JDBC 连接池。BoneCP很小，只有四十几K（运行时需要log4j和Google Collections的支持，这二者加起来就不小了），而相比之下 C3P0 要六百多K。另外JDBC驱动的加载是在连接池之外的，这样在一些应用服务器的配置上就不够灵活。当然，体积小并不是 BoneCP 优秀的原因，BoneCP 到底有什么突出的地方呢，请看看[性能测试报告](http://jolbox.com/)
- [一些其他连接池](http://www.open-open.com/20.htm)
- 商业级连接池：weblogic和websphere

关键概念：
- 最小连接--应用启动后随即打开的连接数以及后续最小维持的连接数。
- 最大连接数--应用能够使用的最多的连接数
- 连接增长数--应用每次新打开的连接个数

举个例子说明连接池的运作：

假设设置了最小和最大的连接为10，20，那么应用一旦启动则首先打开10个数据库连接，但注意此时数据库连接池的正在使用数字为0--因为你并没有使用这些连接，而空闲的数量则是10。然后你开始登录，假设登录代码使用了一个连接进行查询，那么此时数据库连接池的正在使用数字为1、空闲数为9，这并不需要从数据库打开连接--因为连接池已经准备好了10个给你留着呢。登录结束了，当前连接池的连接数量是多少？当然是0，因为那个连接随着事务的结束已经返还给连接池了。然后同时有11个人在同一秒进行登录，会发生什么：连接池从数据库新申请（打开）了一个连接，连同另外的10个一并送出，这个瞬间连接池的使用数是11个，不过没关系正常情况下过一会儿又会变成0。如果同时有21个人登录呢？那第21个人就只能等前面的某个人登录完毕后释放连接给他。这时连接池开启了20个数据库连接--虽然很可能正在使用数量的已经降为0，那么20个连接会一直保持吗？当然不，连接池会在一定时间内关闭一定量的连接还给数据库，在这个例子里数字是20-10=10，因为只需要保持最小连接数就好了，而这个时间周期也是连接池里配置的。


## <a name="1">完全自定义一个连接池</a>

```
public class DBConnectionPool {

    //设置注册属性
    private String url = "jdbc:mysql://localhost:3306/vmaxtam";
    private String user = "root";
    private String password = "root";
    private static String driverClass = "com.mysql.jdbc.Driver";

    //设置连接池属性
    private int initSize = 5;
    private int maxSize = 8;

    //用LinkedList对象来保存connection对象
    public static LinkedList<Connection> connList = new LinkedList<Connection>();
    //声明一个临时变量来计算连接对象的数量
    private int currentsize = 0;

    //声明DBConnectionPool对象时自动注册驱动
    static {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //获取连接的方法
    private Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }

    //获取连接的方法
    private Connection getConnectionProxy() {

        try {
            //获取一个连接
            final Connection conn = DriverManager.getConnection(url, user, password);

            //把连接交给动态代理类转换为代理的连接对象
            Connection connection = (Connection) Proxy.newProxyInstance(
                DBConnectionPool.class.getClassLoader(),
                new Class[]{Connection.class},
                //编写一个方法处理器
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                        throws Throwable {
                        Object value = null;

                        //当遇到close方法，就会把对象放回连接池中，而不是关闭连接
                        if (method.getName().equals("close")) {
                            DBConnectionPool.connList.addLast(conn);
                        } else {
                            //其它方法不变
                            value = method.invoke(conn, args);
                        }
                        return value;
                    }
                }
            );
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //构造方法，初始化连接池，并往里面添加连接对象
    public DBConnectionPool() {
        for (int i = 0; i < initSize; i++) {
            Connection connection = this.getConnection();
            connList.add(connection);
            currentsize++;
        }
    }

    //获取连接池中的一个连接对象
    public Connection getConnFromPool() {
        //当连接池还没空
        if (connList.size() > 0) {
            Connection connection = connList.getFirst();
            connList.removeFirst();
            return connection;

        } else if (connList.size() == 0 && currentsize < 8) {
            //连接池被拿空，且连接数没有达到上限，创建新的连接
            currentsize++;
            connList.addLast(this.getConnection());

            Connection connection = connList.getFirst();
            connList.removeFirst();
            return connection;
        }

        throw new RuntimeException("连接数达到上限，请等待");
    }

    //把用完的连接放回连接池
    public void releaseConnection(Connection connection) {
        connList.addLast(connection);
    }


    public static void main(String[] args) {
        //获得连接池
        DBConnectionPool dbConnectionPool = new DBConnectionPool();

        /*从连接池中尝试获取9个连接
        for(int i = 0 ; i<9; i++){
            Connection conn = mypool.getConnFromPool();
            System.out.println(conn.toString());
        }*/

        //获取第五个连接后，释放一下，然后再获取
        for (int i = 0; i < 9; i++) {
            Connection conn = dbConnectionPool.getConnFromPool();
            if (i == 5) {
                dbConnectionPool.releaseConnection(conn);
            }
            System.out.println(conn.toString());
        }
    }

}
```

## <a name="2">扩展自定义一个连接池</a>
```
public class DBConnectionPool1 implements javax.sql.DataSource {
    // 链表 --- 实现 栈结构 、队列 结构
    private LinkedList<Connection> dataSources = new LinkedList<Connection>();

    public DBConnectionPool1() {
        // 一次性创建10个连接
        for (int i = 0; i < 10; i++) {
            try {
                Connection conn = getConnection();
                // 将连接加入连接池中
                dataSources.add(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        // 取出连接池中一个连接
        final Connection conn = dataSources.removeFirst(); // 删除第一个连接返回
        System.out.println("取出一个连接剩余 " + dataSources.size() + "个连接！");
        // 将目标Connection对象进行增强
        Connection connProxy = (Connection) Proxy.newProxyInstance(conn
                .getClass().getClassLoader(), conn.getClass().getInterfaces(),
            new InvocationHandler() {
                // 执行代理对象任何方法 都将执行 invoke
                @Override
                public Object invoke(Object proxy, Method method,
                    Object[] args) throws Throwable {
                    if (method.getName().equals("close")) {
                        // 需要加强的方法
                        // 不将连接真正关闭，将连接放回连接池
                        releaseConnection(conn);
                        return null;
                    } else {
                        // 不需要加强的方法
                        return method.invoke(conn, args); // 调用真实对象方法
                    }
                }
            });
        return connProxy;
    }

    // 将连接放回连接池
    public void releaseConnection(Connection conn) {
        dataSources.add(conn);
        System.out.println("将连接 放回到连接池中 数量:" + dataSources.size());
    }

    @Override
    public Connection getConnection(String username, String password)
        throws SQLException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
```

参考资料
- [几个主流的Java连接池整理](https://www.cnblogs.com/linjian/p/4831088.html)
- [JDBC之 连接池](https://www.cnblogs.com/vmax-tam/p/4158802.html)
- [JDBC 进阶——连接池](https://www.jianshu.com/p/ad0ff2961597)
