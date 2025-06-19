package org.limadelrey.vertx4.reactive.rest.api.api.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.*;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;

public class BookRouter {
    private static final Logger LOGGER = LogManager.getLogger(BookRouter.class);
    private final Vertx vertx=Vertx.currentContext().owner();
    private final BookHandler bookHandler= GuiceUtil.getGuice().getInstance(BookHandler.class);

    private final BookValidationHandler bookValidationHandler=GuiceUtil.getGuice().getInstance(BookValidationHandler.class);
    private final AgentValidationHandler agentValidationHandler=GuiceUtil.getGuice().getInstance(AgentValidationHandler.class);

    private final RolesValidationHandler rolesValidationHandler=GuiceUtil.getGuice().getInstance(RolesValidationHandler.class);

    private final AagentInfosHandler agentHandler= GuiceUtil.getGuice().getInstance(AagentInfosHandler.class);
    private final RolesHandler rolesHandler= GuiceUtil.getGuice().getInstance(RolesHandler.class);
    private final JwtAuthHandler jwtAuthHandler= GuiceUtil.getGuice().getInstance(JwtAuthHandler.class);

    public BookRouter() {

    }

    /**
     * Set books API routes
     *
     * @param router Router
     */
    public void setRouter(Router router) {
        router.mountSubRouter("/api/v1", buildApiRouter());
    }

    /**
     * Build books API
     * All routes are composed by an error handler, a validation handler and the actual business logic handler
     */
    private Router buildApiRouter() {
        final Router router = Router.router(vertx);

        router.route("/rest-*")
                .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
                .handler(BodyHandler.create().setBodyLimit(40000).setDeleteUploadedFilesOnEnd(true).setHandleFileUploads(true));





        router.get("/rest-books").handler(bookValidationHandler.readAll()).handler(bookHandler::readAll);
        router.get("/rest-books/:id").handler(bookValidationHandler.readOne()).handler(bookHandler::readOne);

        router.get("/rest-agents").handler(agentValidationHandler.readAll()).handler(agentHandler::readAll);
        router.get("/rest-agents/:id").handler(agentValidationHandler.readOne()).handler(agentHandler::readOne);
        router.delete("/rest-agents/:id").handler(agentValidationHandler.delete()).handler(agentHandler::delete);

        router.put("/rest-agents/:id").handler(agentValidationHandler.update()).handler(agentHandler::update);
        router.post("/rest-agents").handler(agentValidationHandler.create()).handler(agentHandler::create);


        router.get("/rest-roles").handler(rolesValidationHandler.readAll()).handler(rolesHandler::readAll);
        router.get("/rest-roles/:id").handler(rolesValidationHandler.readOne()).handler(rolesHandler::readOne);
        router.delete("/rest-roles/:id").handler(rolesValidationHandler.delete()).handler(rolesHandler::delete);
        router.put("/rest-roles/:id").handler(rolesValidationHandler.update()).handler(rolesHandler::update);
        router.post("/rest-roles").handler(rolesValidationHandler.create()).handler(rolesHandler::create);





        router.post("/rest-agent2agent").handler(agentHandler::chatToAgent);


        router.get("/rest-chat-history").handler(bookValidationHandler.readAll()).handler(bookHandler::readAll);


      /*  router.get("/rest-chat-history/:pingId").handler(TimeoutHandler.create(99999999)).handler(ctx -> {
            System.out.println(" chat-history pingId:" + ctx.pathParam("pingId"));
            ctx.response()
                    .putHeader("Content-Type", "text/event-stream")
                    .putHeader("Cache-Control", "no-cache")
                    .setChunked(true);  // 启用分块传输编码
            SseMap.sseClients.put(ctx.pathParam("pingId"), ctx.response());
        });*/
        return router;
    }

}
