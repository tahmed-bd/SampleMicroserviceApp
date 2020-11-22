package com.ta.sm;

import com.ta.gateway.BaseMicroServiceVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  ProductInfo service class: Product information will be fetched through this service
 *
 *  Reference @author Eric Zhao,
 *  Theory source : https://www.sczyh30.com/vertx-blueprint-microservice/api-gateway.html
 *                  https://vertx.io/docs/
 *  Code pattern idea source: https://github.com/sczyh30/vertx-blueprint-microservice/tree/master
 */

public class ProductInfoServer extends BaseMicroServiceVerticle{

    private Map<Integer, ProductInfo> _workstations = new LinkedHashMap<>();
    //protected ServiceDiscovery discovery;
    private boolean debugMode;

    @Override
    public void start(Future<Void> fut) {

        // Intentionally set to true to write log message into service log
        this.debugMode = true;

        Router router = Router.router(vertx);

        // Binding root url "/" to our hello message .
        router.route("/").handler(routingContext -> {
            System.out.println("Productinfo server accessed ");
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Hello from Product Info services </h1>");
        });

        // Serve static resources from the /assets directory
        router.route("/sma/*").handler(StaticHandler.create("assets"));

        // API to get all scores
        router.get("/api/product/values").handler(this::getAllBooks);


        // Create the HTTP server
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration file
                        config().getInteger("api.port", 8000),
                        result -> {
                            if (result.succeeded()) {
                                if (debugMode) System.out.println("Product Info service started ");

                                // publishing service as HttpEndpoint - for REST APIs
                                publishHttpEndpoint(config().getString("api.name", ""), config().getString("api.host", "localhost"), config().getInteger("api.port",8000));
                                fut.complete();
                            } else {
                                if (debugMode) System.out.println("Product Info service not started ");
                                fut.fail(result.cause());
                            }
                        }
                );
    }

    // method to fetch already created dummy data
    private void getAllBooks(RoutingContext routingContext) {

        // Creation of sample data for this service
        createSampleData();

        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(_workstations.values()));
    }

        // Creation of sample data
        private void createSampleData() {
            _workstations = new LinkedHashMap<>();
            ProductInfo _pinfo1 = new ProductInfo("Book","Introduction to Computer Science");
            _workstations.put(_pinfo1.getId(), _pinfo1);
            ProductInfo _pinfo2 = new ProductInfo("Book", "Basic Calculus");
            _workstations.put(_pinfo2.getId(), _pinfo2);
        }

}

