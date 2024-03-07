package org.limadelrey.vertx4.reactive.rest.api.api.repository;

import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.model.Book;
import org.limadelrey.vertx4.reactive.rest.api.api.model.User;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;

import java.util.*;

@Singleton
public class UserRepository {

    private static final Logger LOGGER = LogManager.getLogger(UserRepository.class);

    private static final String SQL_SELECT_ALL = "SELECT * FROM users LIMIT #{limit} OFFSET #{offset}";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM users WHERE id = #{id}";

    private static final String SQL_SELECT_BY_NAME = "SELECT * FROM users WHERE user_name = #{userName}";

    private static final String SQL_INSERT = "INSERT INTO users (user_name, password, create_time, user_status) " +
            "VALUES (#{userName}, #{password}, #{createTime}, #{userStatus}) ";
    private static final String SQL_UPDATE = "UPDATE users SET user_name = #{userName}, password = #{password}, create_time = #{createTime}, " +
            "user_status = #{userStatus} WHERE id = #{id}";
    private static final String SQL_DELETE = "DELETE FROM users WHERE id = #{id}";
    private static final String SQL_COUNT = "SELECT COUNT(*) AS total FROM users";

    public UserRepository() {
    }


    public Future<List<User>> selectAll(SqlConnection connection,
                                        int limit,
                                        int offset) {
        return SqlTemplate
                .forQuery(connection, SQL_SELECT_ALL)
                .mapTo(User.class)
                .execute(Map.of("limit", limit, "offset", offset))
                .map(rowSet -> {
                    final List<User> users = new ArrayList<>();
                    rowSet.forEach(users::add);

                    return users;
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read all users", SQL_SELECT_ALL)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all users", throwable.getMessage())));
    }


    public Future<User>  selectById(SqlConnection connection,
                                   Long id) {

        return SqlTemplate
                .forQuery(connection, SQL_SELECT_BY_ID)
                .mapTo(User.class)
                .execute(Collections.singletonMap("id", id))
                .map(rowSet -> {
                    final RowIterator<User> iterator = rowSet.iterator();

                    if (iterator.hasNext()) {
                        return iterator.next();
                    } else {
                        throw new NoSuchElementException(LogUtils.NO_BOOK_WITH_ID_MESSAGE.buildMessage(id));
                    }
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read User by id", SQL_SELECT_BY_ID)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read User by id", throwable.getMessage())));
    }


    public Future<User> insert(SqlConnection connection,
                               User user) {
        return SqlTemplate
                .forUpdate(connection, SQL_INSERT)
                .mapFrom(User.class)
                .mapTo(User.class)
                .execute(user)
                .map(rowSet -> {
                    if (rowSet .rowCount()> 0){
                        return user;
                    } else {
                        throw new IllegalStateException(LogUtils.CANNOT_CREATE_BOOK_MESSAGE.buildMessage());
                    }
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Insert user", SQL_INSERT)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Insert user", throwable.getMessage())));
    }


    public Future<User> update(SqlConnection connection,
                               User user) {
        return SqlTemplate
                .forUpdate(connection, SQL_UPDATE)
                .mapFrom(User.class)
                .execute(user)
                .flatMap(rowSet -> {
                    if (rowSet.rowCount() > 0) {
                        return Future.succeededFuture(user);
                    } else {
                        throw new NoSuchElementException(LogUtils.NO_BOOK_WITH_ID_MESSAGE.buildMessage(user.getId()));
                    }
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Update User", SQL_UPDATE)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Update User", throwable.getMessage())));
    }


    public Future<Void> delete(SqlConnection connection,
                               Long id) {
        return SqlTemplate
                .forUpdate(connection, SQL_DELETE)
                .execute(Collections.singletonMap("id", id))
                .flatMap(rowSet -> {
                    if (rowSet.rowCount() > 0) {
                        LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Delete User", SQL_DELETE));
                        return Future.succeededFuture();
                    } else {
                        LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Delete User", LogUtils.NO_BOOK_WITH_ID_MESSAGE.buildMessage(id)));
                        throw new NoSuchElementException(LogUtils.NO_BOOK_WITH_ID_MESSAGE.buildMessage(id));
                    }
                });
    }


    public Future<Integer> count(SqlConnection connection) {
        final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("total");

        return SqlTemplate
                .forQuery(connection, SQL_COUNT)
                .mapTo(ROW_MAPPER)
                .execute(Collections.emptyMap())
                .map(rowSet -> rowSet.iterator().next())
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Count Users", SQL_COUNT)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Count Users", throwable.getMessage())));
    }

    public Future<User>  selectByName(SqlConnection connection,
                                    String name) {

        return SqlTemplate
                .forQuery(connection, SQL_SELECT_BY_NAME)
                .mapTo(User.class)
                .execute(Collections.singletonMap("userName", name))
                .map(rowSet -> {
                    final RowIterator<User> iterator = rowSet.iterator();

                    if (iterator.hasNext()) {
                        return iterator.next();
                    } else {
                        throw new NoSuchElementException(LogUtils.NO_BOOK_WITH_ID_MESSAGE.buildMessage(name));
                    }
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read User by name", SQL_SELECT_BY_ID)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read User by name", throwable.getMessage())));
    }





}
