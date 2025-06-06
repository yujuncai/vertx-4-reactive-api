package org.limadelrey.vertx4.reactive.rest.api.api.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.AagentInfosHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookValidationHandler;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;

public class BookRouter {
    private static final Logger LOGGER = LogManager.getLogger(BookRouter.class);
    private final Vertx vertx=Vertx.currentContext().owner();
    private final BookHandler bookHandler= GuiceUtil.getGuice().getInstance(BookHandler.class);

    private final BookValidationHandler bookValidationHandler=GuiceUtil.getGuice().getInstance(BookValidationHandler.class);

    private final AagentInfosHandler agentHandler= GuiceUtil.getGuice().getInstance(AagentInfosHandler.class);


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

        router.route("/books*")
                .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
                .handler(BodyHandler.create().setBodyLimit(40000).setDeleteUploadedFilesOnEnd(true).setHandleFileUploads(true));
        router.route("/agent*")
                .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
                .handler(BodyHandler.create().setBodyLimit(4000).setDeleteUploadedFilesOnEnd(true).setHandleFileUploads(true));


        router.get("/books").handler(bookValidationHandler.readAll()).handler(bookHandler::readAll);
        router.get("/books/:id").handler(bookValidationHandler.readOne()).handler(bookHandler::readOne);
        router.post("/books").handler(bookValidationHandler.create()).handler(bookHandler::create);
        router.put("/books/:id").handler(bookValidationHandler.update()).handler(bookHandler::update);
        router.delete("/books/:id").handler(bookValidationHandler.delete()).handler(bookHandler::delete);



        router.post("/agent2agent").handler(agentHandler::chatToAgent);

        return router;
    }

}
