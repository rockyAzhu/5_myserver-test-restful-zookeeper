package com.newsoftvalley.myserver.clients;

import com.linkedin.common.callback.Callback;
import com.linkedin.common.util.None;
import com.linkedin.d2.balancer.D2Client;
import com.linkedin.d2.balancer.D2ClientBuilder;
import java.util.Properties;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class D2ClientFactory {

  private static D2Client _d2Client;
  private static ScheduledExecutorService _executorService = Executors.newSingleThreadScheduledExecutor();
  private static final CountDownLatch _latch = new CountDownLatch(1);
  private static long _clientStartTimeout;
  private static long _clientShutdownTimeout;

  public static D2Client createInstance(Properties properties)
      throws InterruptedException {
    _d2Client = new D2ClientBuilder()
        .setZkHosts((String)properties.get("zkConnectString"))
        .setZkSessionTimeout(Long.parseLong((String)properties.get("zkSessionTimeout")), TimeUnit.MILLISECONDS)
        .setZkStartupTimeout(Long.parseLong((String)properties.get("zkStartupTimeout")), TimeUnit.MILLISECONDS)
        .setLbWaitTimeout(Long.parseLong((String)properties.get("zkLoadBalancerNotificationTimeout")), TimeUnit.MILLISECONDS)
        .setFlagFile((String) properties.get("zkFlagFile"))
        .setBasePath((String) properties.get("zkBasePath"))
        .setFsBasePath((String) properties.get("fsBasePath"))
        .build();
    _clientStartTimeout = Long.parseLong((String)properties.get("clientStartTimeout"));
    _clientShutdownTimeout = Long.parseLong((String)properties.get("clientShutdownTimeout"));
    start();
    return _d2Client;
  }

  private static void start()
      throws InterruptedException {
    System.out.println("Starting d2 client...");
    try {
      _executorService.submit(new Runnable() {
        @Override
        public void run () {
          _d2Client.start(new Callback<None>() {
            @Override
            public void onError (Throwable e) {
              System.err.println("Error starting d2Client. Aborting... ");
              e.printStackTrace();
              System.exit(1);
            }

            @Override
            public void onSuccess (None result)
            {
              System.out.println("D2 client started");
              _latch.countDown();
            }
          });
        }
      }).get(_clientStartTimeout, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      System.err.println("Cannot start d2 client. Timeout is set to " + _clientStartTimeout + " ms");
      e.printStackTrace();
    }

    _latch.await();
  }

  public static void shutDown() {
    System.out.println("Shutting down d2 client...");
    try {
      _executorService.submit(new Runnable() {
        @Override
        public void run () {
          _d2Client.shutdown(new Callback<None>() {
            @Override
            public void onError (Throwable e) {
              System.err.println("Error shutting down d2Client.");
              e.printStackTrace();
            }

            @Override
            public void onSuccess (None result) {
              System.out.println("D2 client stopped");
            }
          });
        }
      }).get(_clientShutdownTimeout, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      System.err.println("Cannot stop d2 client. Timeout is set to " + _clientShutdownTimeout + " ms");
      e.printStackTrace();
    } finally {
      _executorService.shutdown();
    }
  }
}
