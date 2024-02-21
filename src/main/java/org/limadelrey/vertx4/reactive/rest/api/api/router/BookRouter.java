package org.limadelrey.vertx4.reactive.rest.api.api.router;

import com.google.inject.Guice;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.BookValidationHandler;
import org.limadelrey.vertx4.reactive.rest.api.api.service.BookService;
import org.limadelrey.vertx4.reactive.rest.api.guice.MainModule;

public class BookRouter {

    private final Vertx vertx=Vertx.currentContext().owner();
    private final BookHandler bookHandler= Guice.createInjector(new MainModule()).getInstance(BookHandler.class);
    ;
    private final BookValidationHandler bookValidationHandler=Guice.createInjector(new MainModule()).getInstance(BookValidationHandler.class);;

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

        bookRouter.route("/books*").handler(BodyHandler.create()).handler(LoggerHandler.create(LoggerFormat.DEFAULT));
        bookRouter.get("/books").handler(bookValidationHandler.readAll()).handler(s -> bookHandler.readAll(s));
        bookRouter.get("/books/:id").handler(bookValidationHandler.readOne()).handler(bookHandler::readOne);
        bookRouter.post("/books").handler(bookValidationHandler.create()).handler(bookHandler::create);
        bookRouter.put("/books/:id").handler(bookValidationHandler.update()).handler(bookHandler::update);
        bookRouter.delete("/books/:id").handler(bookValidationHandler.delete()).handler(bookHandler::delete);

        return bookRouter;
    }

}
