package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import com.google.inject.Guice;
import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import org.limadelrey.vertx4.reactive.rest.api.R.Result;
import org.limadelrey.vertx4.reactive.rest.api.api.model.Book;
import org.limadelrey.vertx4.reactive.rest.api.api.model.BookGetAllResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.model.BookGetByIdResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.repository.BookRepository;
import org.limadelrey.vertx4.reactive.rest.api.api.service.BookService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.guice.MainModule;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;
@Singleton
public class BookHandler {

    private static final String ID_PARAMETER = "id";
    private static final String PAGE_PARAMETER = "page";
    private static final String LIMIT_PARAMETER = "limit";

    private final BookService bookService= GuiceUtil.getGuice().getInstance(BookService.class);


    public BookHandler() {

    }

    /**
     * Read all books
     * It should return 200 OK in case of success
     * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
     *
     * @param rc Routing context
     * @return BookGetAllResponse
     */
    public Future<BookGetAllResponse> readAll(RoutingContext rc) {
        final String page = rc.queryParams().get(PAGE_PARAMETER);
        final String limit = rc.queryParams().get(LIMIT_PARAMETER);

        return bookService.readAll(page, limit)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<BookGetAllResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

    /**
     * Read one book
     * It should return 200 OK in case of success
     * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
     *
     * @param rc Routing context
     * @return BookGetByIdResponse
     */
    public Future<BookGetByIdResponse> readOne(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);

        return bookService.readOne(Integer.parseInt(id))
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc,  new Result<BookGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

    /**
     * Create one book
     * It should return 201 Created in case of success
     * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
     *
     * @param rc Routing context
     * @return BookGetByIdResponse
     */
    public Future<BookGetByIdResponse> create(RoutingContext rc) {
        final Book book = rc.getBodyAsJson().mapTo(Book.class);

        return bookService.create(book)
                .onSuccess(success -> ResponseUtils.buildCreatedResponse(rc, new Result<BookGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

    /**
     * Update one book
     * It should return 200 OK in case of success
     * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
     *
     * @param rc Routing context
     * @return BookGetByIdResponse
     */
    public Future<BookGetByIdResponse> update(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);
        final Book book = rc.getBodyAsJson().mapTo(Book.class);

        return bookService.update(Integer.parseInt(id), book)
                .onSuccess(success -> ResponseUtils.buildOkResponse(rc, new Result<BookGetByIdResponse>().ok(success)))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

    /**
     * Delete one book
     * It should return 204 No Content in case of success
     * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
     *
     * @param rc Routing context
     * @return BookGetByIdResponse
     */
    public Future<Void> delete(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);


        return bookService.delete(Integer.parseInt(id))
                .onSuccess(success -> ResponseUtils.buildNoContentResponse(rc))
                .onFailure(throwable -> ResponseUtils.buildErrorResponse(rc, throwable));
    }

}
