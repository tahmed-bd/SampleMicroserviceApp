package com.ta.sm;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ProductInfoServerTest {

    private Vertx vertx;
    private WebClient client;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        client = WebClient.create(vertx);

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", 8000)
                );
        vertx.deployVerticle(ProductInfoServer.class.getName(),options,
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    /*
     * Test case : To test product info service creation
     * On success: This test publish service
     * */
    @Test
    public void testProductInfoServerTest(TestContext context) {

        final Async async = context.async();

        client.get(8000, "localhost", "/")
                .send(ar -> {
                    if (ar.succeeded()) {

                        HttpResponse<Buffer> response = ar.result();
                        System.out.println("ProductionInforServer- Response success "  + response.statusCode());
                        context.assertTrue(response.bodyAsString().toString().contains("Hello"));
                        async.complete();
                    }
                    else {
                        System.out.println("ProductionInforServer- Response failed:  " + ar.cause().getMessage());
                    }
                });
    }

    /*
     * Test case : To test product info service API
     * On success: This test will return 200 status code
     * */
    @Test
    public void testAPIScore(TestContext context) {

        final Async async = context.async();
        System.out.println("Testing API score..");
        client.get(8000, "localhost", "/api/product/values")
                .send(ar -> {
                    if (ar.succeeded()) {

                        HttpResponse<Buffer> response = ar.result();
                        System.out.println("ProductionInforServer- Received status code " + response.statusCode());
                        async.complete();
                    }
                    else {
                        System.out.println("ProductionInforServer- Response failed:  " + ar.cause().getMessage());
                    }
                });


    }
}
