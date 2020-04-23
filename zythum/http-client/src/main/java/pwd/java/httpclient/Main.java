package pwd.java.httpclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;

/**
 * pwd.java.lambda@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2020-03-24 16:51
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class Main {

  protected static volatile CloseableHttpClient httpClient = HttpClients.createDefault();

  public static void main(String[] args) {
    String host = "http://www.apache.com";
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(10000)
        .setConnectTimeout(5000)
        .build();
    HttpGet httpGet = new HttpGet(getRequestUrl(host, "", new HashMap()));
    httpGet.setConfig(requestConfig);
    httpGet.setHeader(HTTP.CONTENT_TYPE, ContentType.create(ContentType.APPLICATION_FORM_URLENCODED
        .getMimeType(), Consts.UTF_8).toString());
    System.out.println(System.currentTimeMillis());
    try {
      HttpResponse execute = httpClient.execute(httpGet);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      System.out.println(System.currentTimeMillis());

    }

//    new Thread(() -> {
//      try {
//        Thread.sleep(3000);
//        httpClient.close();
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }).start();


  }

  protected static String getRequestUrl(String host, String path, Map<String, String> paramsMap) {
    StringBuilder reqUrl = new StringBuilder(host).append(path);
    if (paramsMap != null && !paramsMap.isEmpty()) {
      StringBuilder params = new StringBuilder();
      for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
        params.append("&" + entry.getKey() + "=" + entry.getValue());
      }
      String paramConnector = "?";
      if (!host.contains(paramConnector) && !path.contains(paramConnector)) {
        reqUrl.append(paramConnector);
        reqUrl.append(params.toString().substring(1));
      } else {
        reqUrl.append(params.toString());
      }
    }

    return reqUrl.toString();
  }
}
