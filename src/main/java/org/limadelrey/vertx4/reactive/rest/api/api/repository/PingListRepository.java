package org.limadelrey.vertx4.reactive.rest.api.api.repository;

import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.model.PingList;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;

import java.util.*;

@Singleton
public class PingListRepository {

    private static final Logger LOGGER = LogManager.getLogger(PingListRepository.class);

    private static final String SQL_SELECT_ALL = "SELECT * FROM ping_list  LIMIT #{limit} OFFSET #{offset}";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM ping_list WHERE ping_id = #{pingId}";
    private static final String SQL_INSERT = "INSERT INTO ping_list (ping_id,role_id,source_id,target_id,desc_info,status,reports) " +
            "VALUES (#{ping_id}, #{role_id}, #{source_id},#{target_id},#{desc_info} ,#{status},#{reports}) ";
    private static final String SQL_UPDATE = "UPDATE ping_list SET role_id = #{role_id}, source_id = #{source_id},target_id = #{target_id},desc_info = #{desc_info},status=#{status},reports=#{reports} WHERE ping_id = #{ping_id}";
    private static final String SQL_DELETE = "DELETE FROM ping_list WHERE ping_id = #{pingId}";
    private static final String SQL_COUNT = "SELECT COUNT(*) AS total FROM ping_list ";

    public PingListRepository() {
    }

    /**
     * Read all books using pagination
     *
     * @param connection PostgreSQL connection
     * @param limit      Limit
     * @param offset     Offset
     * @return List<Book>
     */
    public Future<List<PingList>> selectAll(SqlConnection connection,
                                            int limit,
                                            int offset) {
        return SqlTemplate
                .forQuery(connection, SQL_SELECT_ALL)
                .mapTo(PingList.class)
                .execute(Map.of("limit", limit, "offset", offset))
                .map(rowSet -> {
                    final List<PingList> books = new ArrayList<>();
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
    public Future<PingList>  selectById(SqlConnection connection,
                                   String id) {

        return SqlTemplate
                .forQuery(connection, SQL_SELECT_BY_ID)
                .mapTo(PingList.class)
                .execute(Collections.singletonMap("pingId", id))
                .map(rowSet -> {
                    final RowIterator<PingList> iterator = rowSet.iterator();

                    if (iterator.hasNext()) {
                        return iterator.next();
                    } else {
                        throw new NoSuchElementException(LogUtils.NO_ENTITY_WITH_ID_MESSAGE.buildMessage(id));
                    }
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read PingList by id", SQL_SELECT_BY_ID)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read PingList by id", throwable.getMessage())));
    }

    /**
     * Create one book
     *
     * @param connection PostgreSQL connection
     * @param book       Book
     * @return Book
     */
    public Future<PingList> insert(SqlConnection connection,
                                PingList book) {
        return SqlTemplate
                .forUpdate(connection, SQL_INSERT)
                .mapFrom(PingList.class)
                .mapTo(PingList.class)
                .execute(book)
                .map(rowSet -> {
                    if (rowSet .rowCount()> 0){
                        return book;
                    } else {
                        throw new IllegalStateException(LogUtils.CANNOT_CREATE_ENTITY_MESSAGE.buildMessage());
                    }
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Insert PingList", SQL_INSERT)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Insert PingList", throwable.getMessage())));
    }

    /**
     * Update one book
     *
     * @param connection PostgreSQL connection
     * @param book       Book
     * @return Book
     */
    public Future<PingList> update(SqlConnection connection,
                                PingList book) {
        return SqlTemplate
                .forUpdate(connection, SQL_UPDATE)
                .mapFrom(PingList.class)
                .execute(book)
                .flatMap(rowSet -> {
                    if (rowSet.rowCount() > 0) {
                        return Future.succeededFuture(book);
                    } else {
                        throw new NoSuchElementException(LogUtils.NO_ENTITY_WITH_ID_MESSAGE.buildMessage(book.getPingId()));
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
                .execute(Collections.singletonMap("pingId", id))
                .flatMap(rowSet -> {
                    if (rowSet.rowCount() > 0) {
                        LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Delete PingList", SQL_DELETE));
                        return Future.succeededFuture();
                    } else {
                        LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Delete PingList", LogUtils.NO_ENTITY_WITH_ID_MESSAGE.buildMessage(id)));
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
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Count PingList", SQL_COUNT)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Count PingList", throwable.getMessage())));
    }

}
