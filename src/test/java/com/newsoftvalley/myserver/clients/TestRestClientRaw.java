package com.newsoftvalley.myserver.clients;

import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.restli.client.CreateIdRequest;
import com.linkedin.restli.client.DeleteRequest;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.client.RestClient;
import com.linkedin.restli.client.UpdateRequest;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;
import nam.e.spa.ce.Xyz;
import nam.e.spa.ce.XyzKey;
import nam.e.spa.ce.Abc;
import nam.e.spa.ce.AbcRequestBuilders;
import nam.e.spa.ce.XyzRequestBuilders;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

public class TestRestClientRaw {

  private final static AbcRequestBuilders ABC_REQUEST_BUILDERS = new AbcRequestBuilders();
  private final static XyzRequestBuilders XYZ_REQUEST_BUILDERS = new XyzRequestBuilders();

  final HttpClientFactory http = new HttpClientFactory();
  final Client r2Client = new TransportClientAdapter(
      http.getClient(Collections.<String, String>emptyMap()));

  private final RestClient _restClient = new RestClient(r2Client, "http://localhost:7077/myserver-backend/");
  //private final RestClient _restClient = new RestClient(r2Client, "73.231.82.5:7070/myserver-backend/");

  @Test
  public void testXyz() throws RemoteInvocationException {
    // 1. Get
    GetRequest<Xyz> getRequestOne = XYZ_REQUEST_BUILDERS.get()
        .id(new ComplexResourceKey<>(new XyzKey().setA(5).setB(16), new EmptyRecord()))
        .build();
    Xyz entityOne = _restClient.sendRequest(getRequestOne).getResponseEntity();
    Assert.assertEquals((int)entityOne.getResult(), 21);

    GetRequest<Xyz> getRequestTwo = XYZ_REQUEST_BUILDERS.get()
        .id(new ComplexResourceKey<>(new XyzKey().setA(16).setB(5), new EmptyRecord()))
        .build();
    Xyz entityTwo = _restClient.sendRequest(getRequestTwo).getResponseEntity();
    Assert.assertEquals((int)entityTwo.getResult(), 21);

    // 2. Create
    CreateIdRequest<ComplexResourceKey<XyzKey, EmptyRecord>, Xyz> createIdRequest =
        XYZ_REQUEST_BUILDERS.create()
            .input(new Xyz().setA(5).setB(16).setResult(8))
            .build();
    _restClient.sendRequest(createIdRequest).getResponse();

    GetRequest<Xyz> getRequestThree = XYZ_REQUEST_BUILDERS.get()
        .id(new ComplexResourceKey<>(new XyzKey().setA(5).setB(16), new EmptyRecord()))
        .build();
    Xyz entityThree = _restClient.sendRequest(getRequestThree).getResponseEntity();
    Assert.assertEquals((int)entityThree.getResult(), 8);

    GetRequest<Xyz> getRequestFour = XYZ_REQUEST_BUILDERS.get()
        .id(new ComplexResourceKey<>(new XyzKey().setA(16).setB(5), new EmptyRecord()))
        .build();
    Xyz entityFour = _restClient.sendRequest(getRequestFour).getResponseEntity();
    Assert.assertEquals((int)entityFour.getResult(), 8);

    // 3. Update
    UpdateRequest<Xyz> updateRequest = XYZ_REQUEST_BUILDERS.update()
        .id(new ComplexResourceKey<>(new XyzKey().setA(16).setB(5), new EmptyRecord()))
        .input(new Xyz().setResult(27))
        .build();
    _restClient.sendRequest(updateRequest).getResponse();

    GetRequest<Xyz> getRequestFive = XYZ_REQUEST_BUILDERS.get()
        .id(new ComplexResourceKey<>(new XyzKey().setA(5).setB(16), new EmptyRecord()))
        .build();
    Xyz entityFive = _restClient.sendRequest(getRequestFive).getResponseEntity();
    Assert.assertEquals((int)entityFive.getResult(), 27);

    GetRequest<Xyz> getRequestSix = XYZ_REQUEST_BUILDERS.get()
        .id(new ComplexResourceKey<>(new XyzKey().setA(16).setB(5), new EmptyRecord()))
        .build();
    Xyz entitySix = _restClient.sendRequest(getRequestSix).getResponseEntity();
    Assert.assertEquals((int)entitySix.getResult(), 27);

    // 4. Delete
    DeleteRequest<Xyz> deleteRequest = XYZ_REQUEST_BUILDERS.delete()
        .id(new ComplexResourceKey<>(new XyzKey().setA(16).setB(5), new EmptyRecord()))
        .build();
    _restClient.sendRequest(deleteRequest);

    GetRequest<Xyz> getRequestSeven = XYZ_REQUEST_BUILDERS.get()
        .id(new ComplexResourceKey<>(new XyzKey().setA(5).setB(16), new EmptyRecord()))
        .build();
    Xyz entitySeven = _restClient.sendRequest(getRequestSeven).getResponseEntity();
    Assert.assertEquals((int)entitySeven.getResult(), 21);

    GetRequest<Xyz> getRequestEight = XYZ_REQUEST_BUILDERS.get()
        .id(new ComplexResourceKey<>(new XyzKey().setA(16).setB(5), new EmptyRecord()))
        .build();
    Xyz entityEight = _restClient.sendRequest(getRequestEight).getResponseEntity();
    Assert.assertEquals((int)entityEight.getResult(), 21);
  }

  @Test
  public void testAbc() {
    GetRequest<Abc> getRequestReverse = ABC_REQUEST_BUILDERS.get()
        .id("123")
        .build();
    try {
      Abc abc = _restClient.sendRequest(getRequestReverse).getResponseEntity();
      Assert.assertEquals(abc.getEntity(), "321");
    } catch (RemoteInvocationException e) {
      System.out.println("exception found in reverse");
      Assert.fail();
    }
  }
}
