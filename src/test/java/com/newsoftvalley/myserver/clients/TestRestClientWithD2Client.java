package com.newsoftvalley.myserver.clients;

import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.client.RestClient;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;
import java.io.FileInputStream;
import java.util.Properties;
import nam.e.spa.ce.Abc;
import nam.e.spa.ce.AbcRequestBuilders;
import nam.e.spa.ce.Xyz;
import nam.e.spa.ce.XyzKey;
import nam.e.spa.ce.XyzRequestBuilders;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class TestRestClientWithD2Client {

  private final static XyzRequestBuilders ADD_REQUEST_BUILDERS = new XyzRequestBuilders();
  private final static AbcRequestBuilders REVERSE_REQUEST_BUILDERS = new AbcRequestBuilders();

  private RestClient _restClient;

  private Object[][] _testDataAdd = new Object[][] {
      {"d2://add", 1, 1},
      {"d2://add", 2, 2},
      {"d2://add", 3, 3},
      {"d2://add", 4, 4},
      {"d2://add", 5, 5},
      {"d2://add", 6, 6},
      {"d2://add", 7, 7},
      {"d2://add", 8, 8},
      {"d2://add", 9, 9},
  };
  private Object[][] _testDataReverse = new Object[][] {
      {"d2://reverse", 1l},
      {"d2://reverse", 12l},
      {"d2://reverse", 123l},
      {"d2://reverse", 1234l},
      {"d2://reverse", 12345l},
      {"d2://reverse", 123456l},
      {"d2://reverse", 1234567l},
      {"d2://reverse", 12345678l},
      {"d2://reverse", 123456789l},
  };

  @BeforeMethod
  public void before() throws IOException, InterruptedException {
    String path = new File(new File(".").getAbsolutePath()).getCanonicalPath() + "/config/D2Client.properties";
    Properties properties = new Properties();
    properties.load(new FileInputStream(path));
    _restClient = new RestClient(D2ClientFactory.createInstance(properties), "d2://");
  }

  @AfterMethod
  public void after() {
    D2ClientFactory.shutDown();
  }

  @Test
  public void testAdd() throws URISyntaxException, RemoteInvocationException, InterruptedException {
    for (Object[] testData : _testDataAdd) {
      GetRequest<Xyz> getRequestAdd = ADD_REQUEST_BUILDERS.get()
          .id(new ComplexResourceKey<>(new XyzKey().setA((Integer) testData[1]).setB((Integer) testData[2]), new EmptyRecord()))
          .build();
      Xyz result = _restClient.sendRequest(getRequestAdd).getResponseEntity();
      System.out.println(testData[1] + " + " + testData[2] + " = " + result.getResult());
    }
  }

  @Test
  public void testReverse() throws URISyntaxException, RemoteInvocationException, InterruptedException {
    for (Object[] testData : _testDataReverse) {
      GetRequest<Abc> getRequestReverse = REVERSE_REQUEST_BUILDERS.get()
          .id((String) testData[1])
          .build();
      Abc entity = _restClient.sendRequest(getRequestReverse).getResponseEntity();
      System.out.println(testData[1] + " -> " + entity.getEntity());
    }
  }
}
