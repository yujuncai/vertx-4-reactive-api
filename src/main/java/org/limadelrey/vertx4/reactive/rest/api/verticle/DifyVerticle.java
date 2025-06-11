package org.limadelrey.vertx4.reactive.rest.api.verticle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.jackson.DatabindCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.EventBusHandler;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;

public class DifyVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(DifyVerticle.class);

    private final EventBusHandler eventBusHandler= GuiceUtil.getGuice().getInstance(EventBusHandler.class);



    @Override
    public void start(Promise<Void> promise) {

        ObjectMapper mapper = DatabindCodec.mapper();
        mapper.registerModule(new JavaTimeModule());
        vertx.eventBus().consumer("chat_to_dify").handler(message ->{
            eventBusHandler.handlerStart(message);
                });


        //模拟方都为 0
        vertx.eventBus().consumer("chat_to_0").handler(eventBusHandler::handler0);
        //业务方都为 1
        vertx.eventBus().consumer("chat_to_1").handler(eventBusHandler::handler1);


        promise.complete();
        LOGGER.info("Verticle started!");
    }






}
