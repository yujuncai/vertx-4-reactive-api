package org.limadelrey.vertx4.reactive.rest.api.api.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookValidationHandler;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;

public class BookRouter {

    private final Vertx vertx=Vertx.currentContext().owner();
    private final BookHandler bookHandler= GuiceUtil.getGuice().getInstance(BookHandler.class);

    private final BookValidationHandler bookValidationHandler=GuiceUtil.getGuice().getInstance(BookValidationHandler.class);

    public BookRouter() {

    }

    /**
     * Set books API routes
     *
     * @param router Router
     */
    public void setRouter(Router router) {
        router.mountSubRouter("/api/v1", buildBookRouter());
    }

    /**
     * Build books API
     * All routes are composed by an error handler, a validation handler and the actual business logic handler
     */
    private Router buildBookRouter() {
        final Router bookRouter = Router.router(vertx);

        bookRouter.route("/books*")
                .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
                .handler(BodyHandler.create().setBodyLimit(40000000).setDeleteUploadedFilesOnEnd(true).setHandleFileUploads(true));
        bookRouter.get("/books").handler(bookValidationHandler.readAll()).handler(bookHandler::readAll);
        bookRouter.get("/books/:id").handler(bookValidationHandler.readOne()).handler(bookHandler::readOne);
        bookRouter.post("/books").handler(bookValidationHandler.create()).handler(bookHandler::create);
        bookRouter.put("/books/:id").handler(bookValidationHandler.update()).handler(bookHandler::update);
        bookRouter.delete("/books/:id").handler(bookValidationHandler.delete()).handler(bookHandler::delete);

        bookRouter.get("/*").handler(rc -> {
            rc.response().setStatusCode(404).end("Custom 404 message");
        });
        return bookRouter;
    }

}
