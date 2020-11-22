package com.ta.gateway;

import io.vertx.core.*;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.HttpEndpoint;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 *  Base class to create Verticles and manage Service Discovery
 *
 *  Reference @author Eric Zhao,
 *  Source : https://www.sczyh30.com/vertx-blueprint-microservice/api-gateway.html
 *                  https://vertx.io/docs/
 *  Code pattern idea source: https://github.com/sczyh30/vertx-blueprint-microservice/tree/master
 */

public class BaseMicroServiceVerticle extends AbstractVerticle {

    protected ServiceDiscovery discovery;

    // API record is registered to unpublish at stop event
    protected Set<Record> registeredAPIRecords = new ConcurrentHashSet<>();

    private boolean debugMode;



    @Override
    public void start() throws Exception {

        // initiation of service discovery instance and setting configuration to connect to zookeeper backend to manage service discovery of different services
        discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().
                setBackendConfiguration(new io.vertx.core.json.JsonObject().put("connection", "localhost:2181")));
    }

    // Method to publish REST API services
    protected Future<Void> publishHttpEndpoint(String name, String host, int port) {
        Record record = HttpEndpoint.createRecord(name, host, port, "/",
                new JsonObject().put("api.name", config().getString("api.name", ""))
        );
        return publishService(record);
    }

    // Method to publish Gateway API service
    // Currently SSL is not considered but recommended for production use
    protected Future<Void> publishApiGatewayService(String host, int port) {
        Record record = HttpEndpoint.createRecord("api-gateway", false, host, port, "/", null)
                .setType("api-gateway");
        return publishService(record);
    }


    /**
     * Publish a service with record.
     */
    private Future<Void> publishService(Record record) {

        // Intentionally set to true to write log message into service log
        this.debugMode = true;

        if (discovery == null) {
            try {
                start();
            } catch (Exception e) {
                throw new IllegalStateException("Cannot create discovery service");
            }
        }

        Future<Void> future = Future.future();
        // publish the service
        discovery.publish(record, ar -> {
            if (ar.succeeded()) {
                registeredAPIRecords.add(record);
                if (debugMode)System.out.println("Service <" + ar.result().getName() + "> published");
                future.complete();
            } else {
                if (debugMode)System.out.println("Service publication failed : "  + ar.cause());
                future.fail(ar.cause());
            }
        });
        return future;
    }


    @Override
    public void stop(Future<Void> future) throws Exception {

        List<Future> futures = new ArrayList<>();
        registeredAPIRecords.forEach(record -> {
            Future<Void> cleanupFuture = Future.future();
            futures.add(cleanupFuture);
            discovery.unpublish(record.getRegistration(), cleanupFuture.completer());
        });

        if (futures.isEmpty()) {
            discovery.close();
            future.complete();
        } else {
            CompositeFuture.all(futures)
                    .setHandler(ar -> {
                        discovery.close();
                        if (ar.failed()) {
                            future.fail(ar.cause());
                        } else {
                            future.complete();
                        }
                    });
        }
    }

}
