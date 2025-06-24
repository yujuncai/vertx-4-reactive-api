package org.limadelrey.vertx4.reactive.rest.api.api.repository;

import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.model.Roles;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;

import java.util.*;

@Singleton
public class RolesRepository {

    private static final Logger LOGGER = LogManager.getLogger(RolesRepository.class);

    private static final String SQL_SELECT_ALL = "SELECT * FROM roles  LIMIT #{limit} OFFSET #{offset}";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM roles WHERE id = #{id}";
    private static final String SQL_INSERT = "INSERT INTO roles (id,role_name,role_prompt,role_value) " +
            "VALUES (#{id}, #{role_name}, #{role_prompt} ,#{role_value}) ";
    private static final String SQL_UPDATE = "UPDATE roles SET role_name = #{role_name}, role_prompt = #{role_prompt},role_value = #{role_value} WHERE id = #{id}";
    private static final String SQL_DELETE = "DELETE FROM roles WHERE id = #{id}";
    private static final String SQL_COUNT = "SELECT COUNT(*) AS total FROM roles ";

    public RolesRepository() {
    }

    /**
     * Read all books using pagination
     *
     * @param connection PostgreSQL connection
     * @param limit      Limit
     * @param offset     Offset
     * @return List<Book>
     */
    public Future<List<Roles>> selectAll(SqlConnection connection,
                                         int limit,
                                         int offset) {
        return SqlTemplate
                .forQuery(connection, SQL_SELECT_ALL)
                .mapTo(Roles.class)
                .execute(Map.of("limit", limit, "offset", offset))
                .map(rowSet -> {
                    final List<Roles> books = new ArrayList<>();
                    rowSet.forEach(books::add);

                    return books;
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read all books", SQL_SELECT_ALL)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all books", throwable.getMessage())));
    }

    /**
     * Read one book
     *
     * @param connection PostgreSQL connection
     * @param id         Book ID
     * @return Book
     */
    public Future<Roles>  selectById(SqlConnection connection,
                                     String id) {

        return SqlTemplate
                .forQuery(connection, SQL_SELECT_BY_ID)
                .mapTo(Roles.class)
                .execute(Collections.singletonMap("id", id))
                .map(rowSet -> {
                    final RowIterator<Roles> iterator = rowSet.iterator();

                    if (iterator.hasNext()) {
                        return iterator.next();
                    } else {
                        throw new NoSuchElementException(LogUtils.NO_ENTITY_WITH_ID_MESSAGE.buildMessage(id));
                    }
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read Roles by id", SQL_SELECT_BY_ID)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read Roles by id", throwable.getMessage())));
    }

    /**
     * Create one book
     *
     * @param connection PostgreSQL connection
     * @param book       Book
     * @return Book
     */
    public Future<Roles> insert(SqlConnection connection,
                                Roles book) {
        return SqlTemplate
                .forUpdate(connection, SQL_INSERT)
                .mapFrom(Roles.class)
                .mapTo(Roles.class)
                .execute(book)
                .map(rowSet -> {
                    if (rowSet .rowCount()> 0){
                        return book;
                    } else {
                        throw new IllegalStateException(LogUtils.CANNOT_CREATE_ENTITY_MESSAGE.buildMessage());
                    }
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Insert Roles", SQL_INSERT)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Insert Roles", throwable.getMessage())));
    }

    /**
     * Update one book
     *
     * @param connection PostgreSQL connection
     * @param book       Book
     * @return Book
     */
    public Future<Roles> update(SqlConnection connection,
                                Roles book) {
        return SqlTemplate
                .forUpdate(connection, SQL_UPDATE)
                .mapFrom(Roles.class)
                .execute(book)
                .flatMap(rowSet -> {
                    if (rowSet.rowCount() > 0) {
                        return Future.succeededFuture(book);
                    } else {
                        throw new NoSuchElementException(LogUtils.NO_ENTITY_WITH_ID_MESSAGE.buildMessage(book.getId()));
                    }
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Update book", SQL_UPDATE)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Update book", throwable.getMessage())));
    }

    /**
     * Update one book
     *
     * @param connection PostgreSQL connection
     * @param id         Book ID
     * @return Void
     */
    public Future<Void> delete(SqlConnection connection,
                               String id) {
        return SqlTemplate
                .forUpdate(connection, SQL_DELETE)
                .execute(Collections.singletonMap("id", id))
                .flatMap(rowSet -> {
                    if (rowSet.rowCount() > 0) {
                        LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Delete Roles", SQL_DELETE));
                        return Future.succeededFuture();
                    } else {
                        LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Delete Roles", LogUtils.NO_ENTITY_WITH_ID_MESSAGE.buildMessage(id)));
                        throw new NoSuchElementException(LogUtils.NO_ENTITY_WITH_ID_MESSAGE.buildMessage(id));
                    }
                });
    }

    /**
     * Count all books
     *
     * @param connection PostgreSQL connection
     * @return Integer
     */
    public Future<Integer> count(SqlConnection connection) {
        final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("total");

        return SqlTemplate
                .forQuery(connection, SQL_COUNT)
                .mapTo(ROW_MAPPER)
                .execute(Collections.emptyMap())
                .map(rowSet -> rowSet.iterator().next())
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Count Roles", SQL_COUNT)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Count Roles", throwable.getMessage())));
    }

}
