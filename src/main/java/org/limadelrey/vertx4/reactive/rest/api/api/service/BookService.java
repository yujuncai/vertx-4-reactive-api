package org.limadelrey.vertx4.reactive.rest.api.api.service;

import cn.hutool.core.lang.Assert;
import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import org.apache.logging.log4j.core.async.AsyncLoggerContextSelector;
import org.limadelrey.vertx4.reactive.rest.api.api.model.Book;
import org.limadelrey.vertx4.reactive.rest.api.api.model.BookGetAllResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.model.BookGetByIdResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.repository.BookRepository;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.DbUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.QueryUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;
@Singleton
public class BookService {

    private static final Logger LOGGER = LogManager.getLogger(BookService.class);

    private  PgPool dbClient ;
    private final BookRepository bookRepository= GuiceUtil.getGuice().getInstance(BookRepository.class);

    public BookService() {

        dbClient= DbUtils.getInstance();
}


    /**
     * Read all books using pagination
     *
     * @param p Page
     * @param l Limit
     * @return BookGetAllResponse
     */
    public Future<BookGetAllResponse> readAll(String p,
                                              String l) {

        return dbClient.withTransaction(
                connection -> {
                    final int page = QueryUtils.getPage(p);
                    final int limit = QueryUtils.getLimit(l);
                    final int offset = QueryUtils.getOffset(page, limit);
                    LOGGER.info("page {}  , limit {} ,  offset {}" , page, limit, offset);
                    return bookRepository.count(connection)
                            .flatMap(total ->
                                    bookRepository.selectAll(connection, limit, offset)
                                            .map(result -> {
                                                final List<BookGetByIdResponse> books = result.stream()
                                                        .map(BookGetByIdResponse::new)
                                                        .collect(Collectors.toList());

                                                return new BookGetAllResponse(total, limit, page, books);
                                            })
                            );
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read all books", success.getBooks())))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all books", throwable.getMessage())));
    }

    /**
     * Read one book
     *
     * @param id Book ID
     * @return BookGetByIdResponse
     */
    public Future<BookGetByIdResponse> readOne(int id) {

        return dbClient.withTransaction(
                connection -> bookRepository.selectById(connection, id)
                        .map(BookGetByIdResponse::new))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read one book", success)))
                .onFailure(Throwable::printStackTrace);
    }
 //LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read one book", throwable.getMessage()))
    /**
     * Create one book
     *
     * @param book Book
     * @return BookGetByIdResponse
     */
    public Future<BookGetByIdResponse> create(Book book) {
        return dbClient.withTransaction(
                connection -> bookRepository.insert(connection, book)
                        .map(BookGetByIdResponse::new))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Create one book", success)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Create one book", throwable.getMessage())));
    }

    /**
     * Update one book
     *
     * @param id   Book ID
     * @param book Book
     * @return BookGetByIdResponse
     */
    public Future<BookGetByIdResponse> update(int id,
                                              Book book) {
        book.setId(id);

        return dbClient.withTransaction(
                connection -> bookRepository.update(connection, book)
                        .map(BookGetByIdResponse::new))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Update one book", success)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Update one book", throwable.getMessage())));
    }

    /**
     * Delete one book
     *
     * @param id Book ID
     * @return Void
     */
    public Future<Void> delete(Integer id) {
        return dbClient.withTransaction(
                connection -> bookRepository.delete(connection, id))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Delete one book", id)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Delete one book", throwable.getMessage())));
    }

}
