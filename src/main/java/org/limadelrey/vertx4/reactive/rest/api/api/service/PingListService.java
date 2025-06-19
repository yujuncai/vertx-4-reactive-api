package org.limadelrey.vertx4.reactive.rest.api.api.service;

import cn.hutool.core.lang.UUID;
import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.model.*;
import org.limadelrey.vertx4.reactive.rest.api.api.repository.PingListRepository;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.DbUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.QueryUtils;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class PingListService {

    private static final Logger LOGGER = LogManager.getLogger(PingListService.class);

    private final Pool dbClient ;
    private final PingListRepository pingListRepository= GuiceUtil.getGuice().getInstance(PingListRepository.class);

    public PingListService() {

        dbClient= DbUtils.getInstance();
}


    /**
     * Read all books using pagination
     *
     * @param p Page
     * @param l Limit
     * @return BookGetAllResponse
     */
    public Future<PingListGetAllResponse> readAll(String p,
                                                        String l) {

        return dbClient.withTransaction(
                connection -> {
                    final int page = QueryUtils.getPage(p);
                    final int limit = QueryUtils.getLimit(l);
                    final int offset = QueryUtils.getOffset(page, limit);
                    LOGGER.info("page {}  , limit {} ,  offset {}" , page, limit, offset);
                    return pingListRepository.count(connection)
                            .flatMap(total ->
                                    pingListRepository.selectAll(connection, limit, offset)
                                            .map(result -> {
                                                final List<PingListGetByIdResponse> books = result.stream()
                                                        .map(PingListGetByIdResponse::new)
                                                        .collect(Collectors.toList());

                                                return new PingListGetAllResponse(total, limit, page, books);
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
    public Future<PingListGetByIdResponse> readOne(String id) {

        return dbClient.withTransaction(
                connection -> pingListRepository.selectById(connection, id)
                        .map(PingListGetByIdResponse::new))
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
    public Future<PingListGetByIdResponse> create(PingList book) {
        book.setPingId(UUID.fastUUID().toString());
        return dbClient.withTransaction(
                connection -> pingListRepository.insert(connection, book)
                        .map(PingListGetByIdResponse::new))
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
    public Future<PingListGetByIdResponse> update(String id,
                                                  PingList book) {
        book.setPingId(id);

        return dbClient.withTransaction(
                connection -> pingListRepository.update(connection, book)
                        .map(PingListGetByIdResponse::new))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Update one book", success)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Update one book", throwable.getMessage())));
    }

    /**
     * Delete one book
     *
     * @param id Book ID
     * @return Void
     */
    public Future<Void> delete(String id) {
        return dbClient.withTransaction(
                connection -> pingListRepository.delete(connection, id))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Delete one book", id)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Delete one book", throwable.getMessage())));
    }

}
