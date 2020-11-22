package com.ta.gateway;

import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.servicediscovery.ServiceReference;

/**
 *   A verticle for global API gateway.
 *
 *  Reference @author Eric Zhao ,
 *  Theory source : https://www.sczyh30.com/vertx-blueprint-microservice/api-gateway.html
 *                  https://vertx.io/docs/
 *  Code pattern idea source: https://github.com/sczyh30/vertx-blueprint-microservice/tree/master
 *
 */

public class APIGateway extends BaseMicroServiceVerticle{

    private static final int DEFAULT_PORT = 8007;

    //private static final Logger logger = LoggerFactory.getLogger(APIGateway.class);

    private boolean debugMode;



    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();
        this.debugMode = true;

        // get HTTP host and port from configuration, or use default value
        String host = config().getString("api.host", "localhost");
        int port = config().getInteger("api.port", DEFAULT_PORT);

        Router router = Router.router(vertx);

        // body handler
        router.route().handler(BodyHandler.create());

        // api dispatcher
        router.route("/api/*").handler(this::dispatchAPIRequests);

        // static content handler
        router.route("/sma/*").handler(StaticHandler.create("assets"));


        // create http server for API Gateway
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port, host, ar -> {
                    if (ar.succeeded()) {

                        publishApiGatewayService(host, port);

                        future.complete();

                        if (debugMode) System.out.println("API Gateway is running on port " + port);
                        // publish log

                        if (debugMode) System.out.println("api_gateway_init_success:" + port);
                    } else {
                        future.fail(ar.cause());
                        if (debugMode) System.out.println("Can not add to service discovery");
                    }
                });
    }

    private void dispatchAPIRequests(RoutingContext context) {

        int initialOffset = 5; // length of `/api/`
        this.debugMode = true;
        String path = context.request().uri();

        /* TODO : run with circuit breaker in order to deal with failure */

        String prefix = (path.substring(initialOffset)
                .split("/"))[0];

        if (debugMode) System.out.println("prefix value : " + prefix);


        // Get a record by name
        discovery.getRecord(r -> r.getName().equals(prefix), ar -> {
            if (ar.succeeded()) {
                if (ar.result() != null) {

                    ServiceReference reference = discovery.getReference(ar.result());

                    /* TODO : implement authentication  */

                    /*  Fetch product information  */
                    if (prefix.equals("products")) {

                        if (debugMode) System.out.println("products found");


                        // WebClient client = reference.getAs(WebClient.class);
                        // Retrieve the service object
                        HttpClient client = reference.getAs(HttpClient.class);

                        /* TODO : implement authentication  & authorization here */

                        /* Get data from Service  here */

                        client.get("/api/product/values", response -> {


                            response.bodyHandler(buffer -> {

                                if (debugMode) System.out.println("Response " + buffer.length());
                                if (debugMode)
                                    System.out.println("Response " + buffer.getString(0, buffer.length()));

                                context.response().setStatusCode(200).putHeader("content-type", "text/html").end(buffer.getString(0, buffer.length()));

                            });

                            // Always release the service
                            reference.release();
                        });
                    } else {
                        if (debugMode) System.out.println("No matching found");
                        context.response().setStatusCode(422).putHeader("content-type", "text/html").setStatusMessage("Unprocessable entity").end();
                        // the lookup succeeded, but no matching service
                    }
                }
            else
            {
                if (debugMode) System.out.println("Result lookup failed");
                // lookup failed
                context.response().setStatusCode(422).putHeader("content-type","text/html").setStatusMessage("Unprocessable entity").end();
            }
        }
        else
        {
            if (debugMode) System.out.println("Record search failed");
            // lookup failed
            context.response().setStatusCode(422).putHeader("content-type","text/html").setStatusMessage("Unprocessable entity").end();
        }
    });




    }
}
