package com.newsoftvalley.myserver.clients;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestHttpClient {
  private final HttpClient _httpClient = HttpClientBuilder.create().build();

  private final static String HOST = "";
  private final static String CONTEXT_PATH = "";
  private final static String xyz = "";
  private final static String REVERSE = "xyz/";

  @Test
  public void testAbc() throws IOException {
    String url = "http://localhost:7077/myserver-backend/abc/123456";
    HttpGet httpGet = new HttpGet(url);
    HttpResponse response = _httpClient.execute(httpGet);
    httpGet.releaseConnection();
    System.out.println(getEntityString(response));
    //Assert.assertEquals(getEntityString(response), "654321");
  }

  @Test
  public void testXyz() throws IOException {
    // 1. Get: 5 + 16 = 21
    String getUrlOne = "http://localhost:7077/myserver-backend/xyz/a=5&b=16";
    HttpGet httpGetOne = new HttpGet(getUrlOne);
    HttpResponse responseGetOne = _httpClient.execute(httpGetOne);
    httpGetOne.releaseConnection();
    System.out.println(getEntityString(responseGetOne));
    String getUrlTwo  = "http://localhost:7077/myserver-backend/xyz/a=16&b=5";
    HttpGet httpGetTwo = new HttpGet(getUrlTwo);
    HttpResponse responseGetTwo = _httpClient.execute(httpGetTwo);
    httpGetTwo.releaseConnection();
    System.out.println(getEntityString(responseGetTwo));
    System.out.println();

    // 2. Post/Create
    // curl http://localhost:7077/myserver-backend/xyz --data '{"a": 5, "b": 16, "result": 8}'
    String postUrl = "http://localhost:7077/myserver-backend/xyz";
    HttpPost httpPost = new HttpPost(postUrl);
    String jsonPost = "{\"a\":5,\"b\":16, \"result\":8}";
    StringEntity entityPost = new StringEntity(jsonPost);
    httpPost.setEntity(entityPost);
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");
    HttpResponse responsePost = _httpClient.execute(httpPost);
    httpPost.releaseConnection();
    System.out.println("Post Response: " + responsePost.getStatusLine().getStatusCode());

    httpGetOne = new HttpGet(getUrlOne);
    responseGetOne = _httpClient.execute(httpGetOne);
    httpGetOne.releaseConnection();
    System.out.println(getEntityString(responseGetOne));
    httpGetTwo = new HttpGet(getUrlTwo);
    responseGetTwo = _httpClient.execute(httpGetTwo);
    httpGetTwo.releaseConnection();
    System.out.println(getEntityString(responseGetTwo));
    System.out.println();

    // 3. Put/Update
    String putUrl = "http://localhost:7077/myserver-backend/xyz/a=5&b=16";
    HttpPut httpPut = new HttpPut(putUrl);
    String jsonPut = "{\"a\":5,\"b\":16, \"result\":18}";
    HttpEntity entityPut = new StringEntity(jsonPut);
    httpPut.setEntity(entityPut);
    httpPut.setHeader("Accept", "application/json");
    httpPut.setHeader("Content-type", "application/json");
    HttpResponse responsePut = _httpClient.execute(httpPut);
    httpPut.releaseConnection();
    System.out.println("Put Response: " + responsePut.getStatusLine().getStatusCode());

    httpGetOne = new HttpGet(getUrlOne);
    responseGetOne = _httpClient.execute(httpGetOne);
    httpGetOne.releaseConnection();
    System.out.println(getEntityString(responseGetOne));
    httpGetTwo = new HttpGet(getUrlTwo);
    responseGetTwo = _httpClient.execute(httpGetTwo);
    httpGetTwo.releaseConnection();
    System.out.println(getEntityString(responseGetTwo));
    System.out.println();

    // 4. Delete
    String deleteUrl = "http://localhost:7077/myserver-backend/xyz/a=16&b=5";
    HttpDelete httpDelete = new HttpDelete(deleteUrl);
    HttpResponse responseDelete = _httpClient.execute(httpDelete);
    httpDelete.releaseConnection();
    System.out.println("Delete Response: " + responseDelete.getStatusLine().getStatusCode());

    httpGetOne = new HttpGet(getUrlOne);
    responseGetOne = _httpClient.execute(httpGetOne);
    httpGetOne.releaseConnection();
    System.out.println(getEntityString(responseGetOne));
    httpGetTwo = new HttpGet(getUrlTwo);
    responseGetTwo = _httpClient.execute(httpGetTwo);
    httpGetTwo.releaseConnection();
    System.out.println(getEntityString(responseGetTwo));
    System.out.println();
  }

  private String getEntityString(HttpResponse response)
      throws IOException {
    HttpEntity entity = response.getEntity();
    String responseString = EntityUtils.toString(entity, "UTF-8");
    return responseString;
  }
}
