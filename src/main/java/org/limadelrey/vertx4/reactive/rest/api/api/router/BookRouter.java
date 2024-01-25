package org.limadelrey.vertx4.reactive.rest.api.api.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookValidationHandler;

public class BookRouter {

    private final Vertx vertx;
    private final BookHandler bookHandler;
    private final BookValidationHandler bookValidationHandler;

    public BookRouter(Vertx vertx,
                      BookHandler bookHandler,
                      BookValidationHandler bookValidationHandler) {
        this.vertx = vertx;
        this.bookHandler = bookHandler;
        this.bookValidationHandler = bookValidationHandler;
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

        bookRouter.route("/books*").handler(BodyHandler.create()).handler(LoggerHandler.create(LoggerFormat.DEFAULT));
        bookRouter.get("/books").handler(bookValidationHandler.readAll()).handler(bookHandler::readAll);
        bookRouter.get("/books/:id").handler(bookValidationHandler.readOne()).handler(bookHandler::readOne);
        bookRouter.post("/books").handler(bookValidationHandler.create()).handler(bookHandler::create);
        bookRouter.put("/books/:id").handler(bookValidationHandler.update()).handler(bookHandler::update);
        bookRouter.delete("/books/:id").handler(bookValidationHandler.delete()).handler(bookHandler::delete);

        return bookRouter;
    }

}
