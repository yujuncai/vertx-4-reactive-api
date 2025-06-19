package org.limadelrey.vertx4.reactive.rest.api.api.service;

import cn.hutool.core.lang.UUID;
import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.model.*;
import org.limadelrey.vertx4.reactive.rest.api.api.repository.RolesRepository;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.DbUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.QueryUtils;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class RolesService {

    private static final Logger LOGGER = LogManager.getLogger(RolesService.class);

    private final Pool dbClient ;
    private final RolesRepository rolesRepository= GuiceUtil.getGuice().getInstance(RolesRepository.class);

    public RolesService() {

        dbClient= DbUtils.getInstance();
}


    /**
     * Read all books using pagination
     *
     * @param p Page
     * @param l Limit
     * @return BookGetAllResponse
     */
    public Future<RolesGetAllResponse> readAll(String p,
                                               String l) {

        return dbClient.withTransaction(
                connection -> {
                    final int page = QueryUtils.getPage(p);
                    final int limit = QueryUtils.getLimit(l);
                    final int offset = QueryUtils.getOffset(page, limit);
                    LOGGER.info("page {}  , limit {} ,  offset {}" , page, limit, offset);
                    return rolesRepository.count(connection)
                            .flatMap(total ->
                                    rolesRepository.selectAll(connection, limit, offset)
                                            .map(result -> {
                                                final List<RolesGetByIdResponse> books = result.stream()
                                                        .map(RolesGetByIdResponse::new)
                                                        .collect(Collectors.toList());

                                                return new RolesGetAllResponse(total, limit, page, books);
                                            })
                            );
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read all books", success.getRoles())))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all books", throwable.getMessage())));
    }

    /**
     * Read one book
     *
     * @param id Book ID
     * @return BookGetByIdResponse
     */
    public Future<RolesGetByIdResponse> readOne(String id) {

        return dbClient.withTransaction(
                connection -> rolesRepository.selectById(connection, id)
                        .map(RolesGetByIdResponse::new))
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
    public Future<RolesGetByIdResponse> create(Roles book) {
        book.setId(UUID.fastUUID().toString());
        return dbClient.withTransaction(
                connection -> rolesRepository.insert(connection, book)
                        .map(RolesGetByIdResponse::new))
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
    public Future<RolesGetByIdResponse> update(String id,
                                              Roles book) {
        book.setId(id);

        return dbClient.withTransaction(
                connection -> rolesRepository.update(connection, book)
                        .map(RolesGetByIdResponse::new))
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
                connection -> rolesRepository.delete(connection, id))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Delete one book", id)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Delete one book", throwable.getMessage())));
    }

}
