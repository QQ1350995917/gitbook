## 服务端分析
### 注册中心长相com.netflix.eureka.registry.AbstractInstanceRegistry
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
