package org.limadelrey.vertx4.reactive.rest.api.api.service;

import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.model.*;
import org.limadelrey.vertx4.reactive.rest.api.api.repository.BookRepository;
import org.limadelrey.vertx4.reactive.rest.api.api.repository.UserRepository;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.DbUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.QueryUtils;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    private  PgPool dbClient ;
    private final UserRepository userRepository= GuiceUtil.getGuice().getInstance(UserRepository.class);

    public UserService() {

        dbClient= DbUtils.getInstance();
}



    public Future<UserGetAllResponse> readAll(String p,
                                              String l) {

        return dbClient.withTransaction(
                connection -> {
                    final int page = QueryUtils.getPage(p);
                    final int limit = QueryUtils.getLimit(l);
                    final int offset = QueryUtils.getOffset(page, limit);
                    LOGGER.info("page {}  , limit {} ,  offset {}" , page, limit, offset);
                    return userRepository.count(connection)
                            .flatMap(total ->
                                    userRepository.selectAll(connection, limit, offset)
                                            .map(result -> {
                                                final List<UserGetByIdResponse> users = result.stream()
                                                        .map(UserGetByIdResponse::new)
                                                        .collect(Collectors.toList());

                                                return new UserGetAllResponse(total, limit, page, users);
                                            })
                            );
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read all users", success.getUsers())))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all users", throwable.getMessage())));
    }


    public Future<UserGetByIdResponse> readOne(long id) {

        return dbClient.withTransaction(
                connection -> userRepository.selectById(connection, id)
                        .map(UserGetByIdResponse::new))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read one user", success)))
                .onFailure(Throwable::printStackTrace);
    }


    public Future<UserGetByIdResponse> create(User u) {
        return dbClient.withTransaction(
                connection -> userRepository.insert(connection, u)
                        .map(UserGetByIdResponse::new))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Create one user", success)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Create one user", throwable.getMessage())));
    }


    public Future<UserGetByIdResponse> update(long id,
                                              User u) {
        u.setId(id);

        return dbClient.withTransaction(
                connection -> userRepository.update(connection, u)
                        .map(UserGetByIdResponse::new))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Update one user", success)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Update one user", throwable.getMessage())));
    }

    /**
     * Delete one book
     *
     * @param id Book ID
     * @return Void
     */
    public Future<Void> delete(Long id) {
        return dbClient.withTransaction(
                connection -> userRepository.delete(connection, id))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Delete one user", id)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Delete one user", throwable.getMessage())));
    }




    public Future<UserGetByIdResponse> login(String name) {

        return dbClient.withTransaction(
                        connection -> userRepository.selectByName(connection, name)
                                .map(UserGetByIdResponse::new))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read one user", success)))
                .onFailure(Throwable::printStackTrace);
    }



}
