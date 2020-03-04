## 实现分析

### 客户端
#### com.netflix.discovery.EurekaClient
#### jersey
#### com.netflix.discovery.shared.transport.EurekaHttpClient
根据Eureka的应用介绍，其应该为一个CS结构的应用，那么客户端和服务端则存在通讯，使用抓包工具抓取数据为

![](images/eureka-client-request-01.png)
![](images/eureka-client-request-02.png)
![](images/eureka-client-request-03.png)


### com.netflix.discovery.shared.transport.EurekaHttpClient
```java
package com.netflix.discovery.shared.transport;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;

/**
 * Low level Eureka HTTP client API.
 *
 * @author Tomasz Bak
 */
public interface EurekaHttpClient {

    EurekaHttpResponse<Void> register(InstanceInfo info);

    EurekaHttpResponse<Void> cancel(String appName, String id);

    EurekaHttpResponse<InstanceInfo> sendHeartBeat(String appName, String id, InstanceInfo info, InstanceStatus overriddenStatus);

    EurekaHttpResponse<Void> statusUpdate(String appName, String id, InstanceStatus newStatus, InstanceInfo info);

    EurekaHttpResponse<Void> deleteStatusOverride(String appName, String id, InstanceInfo info);

    EurekaHttpResponse<Applications> getApplications(String... regions);

    EurekaHttpResponse<Applications> getDelta(String... regions);

    EurekaHttpResponse<Applications> getVip(String vipAddress, String... regions);

    EurekaHttpResponse<Applications> getSecureVip(String secureVipAddress, String... regions);

    EurekaHttpResponse<Application> getApplication(String appName);

    EurekaHttpResponse<InstanceInfo> getInstance(String appName, String id);

    EurekaHttpResponse<InstanceInfo> getInstance(String id);

    void shutdown();
}
```
其实现类如下图

![](images/eureka-client-class.png)

运行时主要使用到了com.netflix.discovery.shared.transport.jersey.AbstractJerseyEurekaHttpClient

### com.netflix.discovery.shared.transport.jersey.AbstractJerseyEurekaHttpClient

#### register

```java
package com.netflix.discovery.shared.transport.jersey;
/**
 * @author Tomasz Bak
 */
public abstract class AbstractJerseyEurekaHttpClient implements EurekaHttpClient {
     @Override
     public EurekaHttpResponse<Void> register(InstanceInfo info) {
         String urlPath = "apps/" + info.getAppName();
         ClientResponse response = null;
         try {
             Builder resourceBuilder = jerseyClient.resource(serviceUrl).path(urlPath).getRequestBuilder();
             addExtraHeaders(resourceBuilder);
             response = resourceBuilder
                     .header("Accept-Encoding", "gzip")
                     .type(MediaType.APPLICATION_JSON_TYPE)
                     .accept(MediaType.APPLICATION_JSON)
                     .post(ClientResponse.class, info);
             return anEurekaHttpResponse(response.getStatus()).headers(headersOf(response)).build();
         } finally {
             if (logger.isDebugEnabled()) {
                 logger.debug("Jersey HTTP POST {}/{} with instance {}; statusCode={}", serviceUrl, urlPath, info.getId(),
                         response == null ? "N/A" : response.getStatus());
             }
             if (response != null) {
                 response.close();
             }
         }
     } 
}
```
#### cancel
```java
package com.netflix.discovery.shared.transport.jersey;
/**
 * @author Tomasz Bak
 */
public abstract class AbstractJerseyEurekaHttpClient implements EurekaHttpClient {
      @Override
      public EurekaHttpResponse<Void> cancel(String appName, String id) {
          String urlPath = "apps/" + appName + '/' + id;
          ClientResponse response = null;
          try {
              Builder resourceBuilder = jerseyClient.resource(serviceUrl).path(urlPath).getRequestBuilder();
              addExtraHeaders(resourceBuilder);
              response = resourceBuilder.delete(ClientResponse.class);
              return anEurekaHttpResponse(response.getStatus()).headers(headersOf(response)).build();
          } finally {
              if (logger.isDebugEnabled()) {
                  logger.debug("Jersey HTTP DELETE {}/{}; statusCode={}", serviceUrl, urlPath, response == null ? "N/A" : response.getStatus());
              }
              if (response != null) {
                  response.close();
              }
          }
      }
}
```

#### sendHeartBeat
```java
package com.netflix.discovery.shared.transport.jersey;
/**
 * @author Tomasz Bak
 */
public abstract class AbstractJerseyEurekaHttpClient implements EurekaHttpClient {
      @Override
      public EurekaHttpResponse<InstanceInfo> sendHeartBeat(String appName, String id, InstanceInfo info, InstanceStatus overriddenStatus) {
          String urlPath = "apps/" + appName + '/' + id;
          ClientResponse response = null;
          try {
              WebResource webResource = jerseyClient.resource(serviceUrl)
                      .path(urlPath)
                      .queryParam("status", info.getStatus().toString())
                      .queryParam("lastDirtyTimestamp", info.getLastDirtyTimestamp().toString());
              if (overriddenStatus != null) {
                  webResource = webResource.queryParam("overriddenstatus", overriddenStatus.name());
              }
              Builder requestBuilder = webResource.getRequestBuilder();
              addExtraHeaders(requestBuilder);
              response = requestBuilder.put(ClientResponse.class);
              EurekaHttpResponseBuilder<InstanceInfo> eurekaResponseBuilder = anEurekaHttpResponse(response.getStatus(), InstanceInfo.class).headers(headersOf(response));
              if (response.hasEntity()) {
                  eurekaResponseBuilder.entity(response.getEntity(InstanceInfo.class));
              }
              return eurekaResponseBuilder.build();
          } finally {
              if (logger.isDebugEnabled()) {
                  logger.debug("Jersey HTTP PUT {}/{}; statusCode={}", serviceUrl, urlPath, response == null ? "N/A" : response.getStatus());
              }
              if (response != null) {
                  response.close();
              }
          }
      }
}
```
#### statusUpdate
```java
package com.netflix.discovery.shared.transport.jersey;
/**
 * @author Tomasz Bak
 */
public abstract class AbstractJerseyEurekaHttpClient implements EurekaHttpClient {
      @Override
      public EurekaHttpResponse<Void> statusUpdate(String appName, String id, InstanceStatus newStatus, InstanceInfo info) {
          String urlPath = "apps/" + appName + '/' + id + "/status";
          ClientResponse response = null;
          try {
              Builder requestBuilder = jerseyClient.resource(serviceUrl)
                      .path(urlPath)
                      .queryParam("value", newStatus.name())
                      .queryParam("lastDirtyTimestamp", info.getLastDirtyTimestamp().toString())
                      .getRequestBuilder();
              addExtraHeaders(requestBuilder);
              response = requestBuilder.put(ClientResponse.class);
              return anEurekaHttpResponse(response.getStatus()).headers(headersOf(response)).build();
          } finally {
              if (logger.isDebugEnabled()) {
                  logger.debug("Jersey HTTP PUT {}/{}; statusCode={}", serviceUrl, urlPath, response == null ? "N/A" : response.getStatus());
              }
              if (response != null) {
                  response.close();
              }
          }
      }
}
```
#### deleteStatusOverride
```java
package com.netflix.discovery.shared.transport.jersey;
/**
 * @author Tomasz Bak
 */
public abstract class AbstractJerseyEurekaHttpClient implements EurekaHttpClient {
      @Override
      public EurekaHttpResponse<Void> deleteStatusOverride(String appName, String id, InstanceInfo info) {
          String urlPath = "apps/" + appName + '/' + id + "/status";
          ClientResponse response = null;
          try {
              Builder requestBuilder = jerseyClient.resource(serviceUrl)
                      .path(urlPath)
                      .queryParam("lastDirtyTimestamp", info.getLastDirtyTimestamp().toString())
                      .getRequestBuilder();
              addExtraHeaders(requestBuilder);
              response = requestBuilder.delete(ClientResponse.class);
              return anEurekaHttpResponse(response.getStatus()).headers(headersOf(response)).build();
          } finally {
              if (logger.isDebugEnabled()) {
                  logger.debug("Jersey HTTP DELETE {}/{}; statusCode={}", serviceUrl, urlPath, response == null ? "N/A" : response.getStatus());
              }
              if (response != null) {
                  response.close();
              }
          }
      }
}
```
#### getApplications getDelta getVip getSecureVip
```java
package com.netflix.discovery.shared.transport.jersey;
/**
 * @author Tomasz Bak
 */
public abstract class AbstractJerseyEurekaHttpClient implements EurekaHttpClient {
      @Override
      public EurekaHttpResponse<Applications> getApplications(String... regions) {
          return getApplicationsInternal("apps/", regions);
      }
  
      @Override
      public EurekaHttpResponse<Applications> getDelta(String... regions) {
          return getApplicationsInternal("apps/delta", regions);
      }
  
      @Override
      public EurekaHttpResponse<Applications> getVip(String vipAddress, String... regions) {
          return getApplicationsInternal("vips/" + vipAddress, regions);
      }
  
      @Override
      public EurekaHttpResponse<Applications> getSecureVip(String secureVipAddress, String... regions) {
          return getApplicationsInternal("svips/" + secureVipAddress, regions);
      }
      
      private EurekaHttpResponse<Applications> getApplicationsInternal(String urlPath, String[] regions) {
        ClientResponse response = null;
        String regionsParamValue = null;
        try {
            WebResource webResource = jerseyClient.resource(serviceUrl).path(urlPath);
            if (regions != null && regions.length > 0) {
                regionsParamValue = StringUtil.join(regions);
                webResource = webResource.queryParam("regions", regionsParamValue);
            }
            Builder requestBuilder = webResource.getRequestBuilder();
            addExtraHeaders(requestBuilder);
            response = requestBuilder.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

            Applications applications = null;
            if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity()) {
                applications = response.getEntity(Applications.class);
            }
            return anEurekaHttpResponse(response.getStatus(), Applications.class)
                    .headers(headersOf(response))
                    .entity(applications)
                    .build();
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("Jersey HTTP GET {}/{}?{}; statusCode={}",
                        serviceUrl, urlPath,
                        regionsParamValue == null ? "" : "regions=" + regionsParamValue,
                        response == null ? "N/A" : response.getStatus()
                );
            }
            if (response != null) {
                response.close();
            }
        }
    }      
}
```
#### getApplication

```java
package com.netflix.discovery.shared.transport.jersey;
/**
 * @author Tomasz Bak
 */
public abstract class AbstractJerseyEurekaHttpClient implements EurekaHttpClient {
      @Override
      public EurekaHttpResponse<Application> getApplication(String appName) {
          String urlPath = "apps/" + appName;
          ClientResponse response = null;
          try {
              Builder requestBuilder = jerseyClient.resource(serviceUrl).path(urlPath).getRequestBuilder();
              addExtraHeaders(requestBuilder);
              response = requestBuilder.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
  
              Application application = null;
              if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity()) {
                  application = response.getEntity(Application.class);
              }
              return anEurekaHttpResponse(response.getStatus(), Application.class)
                      .headers(headersOf(response))
                      .entity(application)
                      .build();
          } finally {
              if (logger.isDebugEnabled()) {
                  logger.debug("Jersey HTTP GET {}/{}; statusCode={}", serviceUrl, urlPath, response == null ? "N/A" : response.getStatus());
              }
              if (response != null) {
                  response.close();
              }
          }
      }
}
```

####  getInstance
```java
package com.netflix.discovery.shared.transport.jersey;
/**
 * @author Tomasz Bak
 */
public abstract class AbstractJerseyEurekaHttpClient implements EurekaHttpClient {
      @Override
      public EurekaHttpResponse<InstanceInfo> getInstance(String id) {
          return getInstanceInternal("instances/" + id);
      }
  
      @Override
      public EurekaHttpResponse<InstanceInfo> getInstance(String appName, String id) {
          return getInstanceInternal("apps/" + appName + '/' + id);
      }
      
      private EurekaHttpResponse<InstanceInfo> getInstanceInternal(String urlPath) {
        ClientResponse response = null;
        try {
            Builder requestBuilder = jerseyClient.resource(serviceUrl).path(urlPath).getRequestBuilder();
            addExtraHeaders(requestBuilder);
            response = requestBuilder.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

            InstanceInfo infoFromPeer = null;
            if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity()) {
                infoFromPeer = response.getEntity(InstanceInfo.class);
            }
            return anEurekaHttpResponse(response.getStatus(), InstanceInfo.class)
                    .headers(headersOf(response))
                    .entity(infoFromPeer)
                    .build();
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("Jersey HTTP GET {}/{}; statusCode={}", serviceUrl, urlPath, response == null ? "N/A" : response.getStatus());
            }
            if (response != null) {
                response.close();
            }
        }
    }      
}
```

执行顺序为
- getApplications
- register
- getDelta
- sendHeartBeat
- getDelta
- sendHeartBeat
- ...

在AbstractJerseyEurekaHttpClient中以上方法返回的数据为压缩后的数据在entity中存储。在上层用可以看到返回数据结构如下图

![](images/eureka-client-response-01.png)
![](images/eureka-client-response-01-01.png)

Applications接口定义在com.netflix.discovery.shared.Applications中。

org.apache.http.impl.client.AbstractHttpClient
GET ttp://192.168.202.222:51000/eureka/apps/




com.netflix.discovery.shared.transport.EurekaHttpClient

### 服务端
#### com.netflix.discovery.EurekaClient
#### jersey 
#### com.netflix.discovery.shared.transport.EurekaHttpClient
##### 注册中心长相 com.netflix.eureka.registry.AbstractInstanceRegistry
```java
public abstract class AbstractInstanceRegistry implements InstanceRegistry {
    private final ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>> registry
            = new ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>>();  
}
```

外层key为appName,内层的key为instanceId。InstanceInfo对象中存储的是注册的服务实例。

### com.netflix.eureka.resources.ApplicationsResource
```java
@Path("/{version}/apps")
@Produces({"application/xml", "application/json"})
public class ApplicationsResource {
    /**
     * Gets information about a particular {@link com.netflix.discovery.shared.Application}.
     *
     * @param version
     *            the version of the request.
     * @param appId
     *            the unique application identifier (which is the name) of the
     *            application.
     * @return information about a particular application.
     */
    @Path("{appId}")
    public ApplicationResource getApplicationResource(
            @PathParam("version") String version,
            @PathParam("appId") String appId) {
        CurrentRequestVersion.set(Version.toEnum(version));
        return new ApplicationResource(appId, serverConfig, registry);
    }
}
```

```java
@Path("/{version}/apps")
@Produces({"application/xml", "application/json"})
public class ApplicationsResource {
    /**
     * Get information about all {@link com.netflix.discovery.shared.Applications}.
     *
     * @param version the version of the request.
     * @param acceptHeader the accept header to indicate whether to serve JSON or XML data.
     * @param acceptEncoding the accept header to indicate whether to serve compressed or uncompressed data.
     * @param eurekaAccept an eureka accept extension, see {@link com.netflix.appinfo.EurekaAccept}
     * @param uriInfo the {@link java.net.URI} information of the request made.
     * @param regionsStr A comma separated list of remote regions from which the instances will also be returned.
     *                   The applications returned from the remote region can be limited to the applications
     *                   returned by {@link EurekaServerConfig#getRemoteRegionAppWhitelist(String)}
     *
     * @return a response containing information about all {@link com.netflix.discovery.shared.Applications}
     *         from the {@link AbstractInstanceRegistry}.
     */
    @GET
    public Response getContainers(@PathParam("version") String version,
                                  @HeaderParam(HEADER_ACCEPT) String acceptHeader,
                                  @HeaderParam(HEADER_ACCEPT_ENCODING) String acceptEncoding,
                                  @HeaderParam(EurekaAccept.HTTP_X_EUREKA_ACCEPT) String eurekaAccept,
                                  @Context UriInfo uriInfo,
                                  @Nullable @QueryParam("regions") String regionsStr) {

        boolean isRemoteRegionRequested = null != regionsStr && !regionsStr.isEmpty();
        String[] regions = null;
        if (!isRemoteRegionRequested) {
            EurekaMonitors.GET_ALL.increment();
        } else {
            regions = regionsStr.toLowerCase().split(",");
            Arrays.sort(regions); // So we don't have different caches for same regions queried in different order.
            EurekaMonitors.GET_ALL_WITH_REMOTE_REGIONS.increment();
        }

        // Check if the server allows the access to the registry. The server can
        // restrict access if it is not
        // ready to serve traffic depending on various reasons.
        if (!registry.shouldAllowAccess(isRemoteRegionRequested)) {
            return Response.status(Status.FORBIDDEN).build();
        }
        CurrentRequestVersion.set(Version.toEnum(version));
        KeyType keyType = Key.KeyType.JSON;
        String returnMediaType = MediaType.APPLICATION_JSON;
        if (acceptHeader == null || !acceptHeader.contains(HEADER_JSON_VALUE)) {
            keyType = Key.KeyType.XML;
            returnMediaType = MediaType.APPLICATION_XML;
        }

        Key cacheKey = new Key(Key.EntityType.Application,
                ResponseCacheImpl.ALL_APPS,
                keyType, CurrentRequestVersion.get(), EurekaAccept.fromString(eurekaAccept), regions
        );

        Response response;
        if (acceptEncoding != null && acceptEncoding.contains(HEADER_GZIP_VALUE)) {
            response = Response.ok(responseCache.getGZIP(cacheKey))
                    .header(HEADER_CONTENT_ENCODING, HEADER_GZIP_VALUE)
                    .header(HEADER_CONTENT_TYPE, returnMediaType)
                    .build();
        } else {
            response = Response.ok(responseCache.get(cacheKey))
                    .build();
        }
        return response;
    }

}
```
```java
@Path("/{version}/apps")
@Produces({"application/xml", "application/json"})
public class ApplicationsResource {
    /**
     * Get information about all delta changes in {@link com.netflix.discovery.shared.Applications}.
     *
     * <p>
     * The delta changes represent the registry information change for a period
     * as configured by
     * {@link EurekaServerConfig#getRetentionTimeInMSInDeltaQueue()}. The
     * changes that can happen in a registry include
     * <em>Registrations,Cancels,Status Changes and Expirations</em>. Normally
     * the changes to the registry are infrequent and hence getting just the
     * delta will be much more efficient than getting the complete registry.
     * </p>
     *
     * <p>
     * Since the delta information is cached over a period of time, the requests
     * may return the same data multiple times within the window configured by
     * {@link EurekaServerConfig#getRetentionTimeInMSInDeltaQueue()}.The clients
     * are expected to handle this duplicate information.
     * <p>
     *
     * @param version the version of the request.
     * @param acceptHeader the accept header to indicate whether to serve  JSON or XML data.
     * @param acceptEncoding the accept header to indicate whether to serve compressed or uncompressed data.
     * @param eurekaAccept an eureka accept extension, see {@link com.netflix.appinfo.EurekaAccept}
     * @param uriInfo  the {@link java.net.URI} information of the request made.
     * @return response containing the delta information of the
     *         {@link AbstractInstanceRegistry}.
     */
    @Path("delta")
    @GET
    public Response getContainerDifferential(
            @PathParam("version") String version,
            @HeaderParam(HEADER_ACCEPT) String acceptHeader,
            @HeaderParam(HEADER_ACCEPT_ENCODING) String acceptEncoding,
            @HeaderParam(EurekaAccept.HTTP_X_EUREKA_ACCEPT) String eurekaAccept,
            @Context UriInfo uriInfo, @Nullable @QueryParam("regions") String regionsStr) {

        boolean isRemoteRegionRequested = null != regionsStr && !regionsStr.isEmpty();

        // If the delta flag is disabled in discovery or if the lease expiration
        // has been disabled, redirect clients to get all instances
        if ((serverConfig.shouldDisableDelta()) || (!registry.shouldAllowAccess(isRemoteRegionRequested))) {
            return Response.status(Status.FORBIDDEN).build();
        }

        String[] regions = null;
        if (!isRemoteRegionRequested) {
            EurekaMonitors.GET_ALL_DELTA.increment();
        } else {
            regions = regionsStr.toLowerCase().split(",");
            Arrays.sort(regions); // So we don't have different caches for same regions queried in different order.
            EurekaMonitors.GET_ALL_DELTA_WITH_REMOTE_REGIONS.increment();
        }

        CurrentRequestVersion.set(Version.toEnum(version));
        KeyType keyType = Key.KeyType.JSON;
        String returnMediaType = MediaType.APPLICATION_JSON;
        if (acceptHeader == null || !acceptHeader.contains(HEADER_JSON_VALUE)) {
            keyType = Key.KeyType.XML;
            returnMediaType = MediaType.APPLICATION_XML;
        }

        Key cacheKey = new Key(Key.EntityType.Application,
                ResponseCacheImpl.ALL_APPS_DELTA,
                keyType, CurrentRequestVersion.get(), EurekaAccept.fromString(eurekaAccept), regions
        );

        if (acceptEncoding != null
                && acceptEncoding.contains(HEADER_GZIP_VALUE)) {
            return Response.ok(responseCache.getGZIP(cacheKey))
                    .header(HEADER_CONTENT_ENCODING, HEADER_GZIP_VALUE)
                    .header(HEADER_CONTENT_TYPE, returnMediaType)
                    .build();
        } else {
            return Response.ok(responseCache.get(cacheKey))
                    .build();
        }
    }

}
```

### com.netflix.eureka.resources.ApplicationResource
```java
@Produces({"application/xml", "application/json"})
public class ApplicationResource {
      /**
       * Gets information about a particular {@link com.netflix.discovery.shared.Application}.
       *
       * @param version
       *            the version of the request.
       * @param acceptHeader
       *            the accept header of the request to indicate whether to serve
       *            JSON or XML data.
       * @return the response containing information about a particular
       *         application.
       */
      @GET
      public Response getApplication(@PathParam("version") String version,
                                     @HeaderParam("Accept") final String acceptHeader,
                                     @HeaderParam(EurekaAccept.HTTP_X_EUREKA_ACCEPT) String eurekaAccept) {
          if (!registry.shouldAllowAccess(false)) {
              return Response.status(Status.FORBIDDEN).build();
          }
  
          EurekaMonitors.GET_APPLICATION.increment();
  
          CurrentRequestVersion.set(Version.toEnum(version));
          KeyType keyType = Key.KeyType.JSON;
          if (acceptHeader == null || !acceptHeader.contains("json")) {
              keyType = Key.KeyType.XML;
          }
  
          Key cacheKey = new Key(
                  Key.EntityType.Application,
                  appName,
                  keyType,
                  CurrentRequestVersion.get(),
                  EurekaAccept.fromString(eurekaAccept)
          );
  
          String payLoad = responseCache.get(cacheKey);
  
          if (payLoad != null) {
              logger.debug("Found: {}", appName);
              return Response.ok(payLoad).build();
          } else {
              logger.debug("Not Found: {}", appName);
              return Response.status(Status.NOT_FOUND).build();
          }
      }
}
```

### com.netflix.eureka.resources.ApplicationResource
```java
@Produces({"application/xml", "application/json"})
public class ApplicationResource {
      /**
       * Gets information about a particular instance of an application.
       *
       * @param id
       *            the unique identifier of the instance.
       * @return information about a particular instance.
       */
      @Path("{id}")
      public InstanceResource getInstanceInfo(@PathParam("id") String id) {
          return new InstanceResource(this, id, serverConfig, registry);
      }
}
```

### com.netflix.eureka.resources.ApplicationResource
```java
@Produces({"application/xml", "application/json"})
public class ApplicationResource {
      /**
       * Registers information about a particular instance for an
       * {@link com.netflix.discovery.shared.Application}.
       *
       * @param info
       *            {@link InstanceInfo} information of the instance.
       * @param isReplication
       *            a header parameter containing information whether this is
       *            replicated from other nodes.
       */
      @POST
      @Consumes({"application/json", "application/xml"})
      public Response addInstance(InstanceInfo info,
                                  @HeaderParam(PeerEurekaNode.HEADER_REPLICATION) String isReplication) {
          logger.debug("Registering instance {} (replication={})", info.getId(), isReplication);
          // validate that the instanceinfo contains all the necessary required fields
          if (isBlank(info.getId())) {
              return Response.status(400).entity("Missing instanceId").build();
          } else if (isBlank(info.getHostName())) {
              return Response.status(400).entity("Missing hostname").build();
          } else if (isBlank(info.getIPAddr())) {
              return Response.status(400).entity("Missing ip address").build();
          } else if (isBlank(info.getAppName())) {
              return Response.status(400).entity("Missing appName").build();
          } else if (!appName.equals(info.getAppName())) {
              return Response.status(400).entity("Mismatched appName, expecting " + appName + " but was " + info.getAppName()).build();
          } else if (info.getDataCenterInfo() == null) {
              return Response.status(400).entity("Missing dataCenterInfo").build();
          } else if (info.getDataCenterInfo().getName() == null) {
              return Response.status(400).entity("Missing dataCenterInfo Name").build();
          }
  
          // handle cases where clients may be registering with bad DataCenterInfo with missing data
          DataCenterInfo dataCenterInfo = info.getDataCenterInfo();
          if (dataCenterInfo instanceof UniqueIdentifier) {
              String dataCenterInfoId = ((UniqueIdentifier) dataCenterInfo).getId();
              if (isBlank(dataCenterInfoId)) {
                  boolean experimental = "true".equalsIgnoreCase(serverConfig.getExperimental("registration.validation.dataCenterInfoId"));
                  if (experimental) {
                      String entity = "DataCenterInfo of type " + dataCenterInfo.getClass() + " must contain a valid id";
                      return Response.status(400).entity(entity).build();
                  } else if (dataCenterInfo instanceof AmazonInfo) {
                      AmazonInfo amazonInfo = (AmazonInfo) dataCenterInfo;
                      String effectiveId = amazonInfo.get(AmazonInfo.MetaDataKey.instanceId);
                      if (effectiveId == null) {
                          amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.instanceId.getName(), info.getId());
                      }
                  } else {
                      logger.warn("Registering DataCenterInfo of type {} without an appropriate id", dataCenterInfo.getClass());
                  }
              }
          }
  
          registry.register(info, "true".equals(isReplication));
          return Response.status(204).build();  // 204 to be backwards compatible
      }
}
```
