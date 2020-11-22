package com.ta.gateway;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
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
public class APIGatewayTest {

    private Vertx vertx;
    private WebClient client;


    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        client = WebClient.create(vertx);
        vertx.deployVerticle(APIGateway.class.getName(),
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    /*
     * Test case : To test APIGateway service creation
     * On success: This test publish APIGateway service
     * On failure : if Apache Zookeeper is not started , it will print error message
     * */
    @Test
    public void testAPIServer(TestContext context) {

        final Async async = context.async();

        client.get(8007, "localhost", "/")
                .send(ar -> {
                    if (ar.succeeded()) {

                        HttpResponse<Buffer> response = ar.result();
                        System.out.println("APTGateway- Test Response Success : " + response.statusCode());

                        async.complete();
                    }
                    else {
                        System.out.println("APTGateway-  Response failed:  " + ar.cause().getMessage());
                    }
                });


    }
}
