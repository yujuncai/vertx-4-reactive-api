package org.limadelrey.vertx4.reactive.rest.api.api.repository;

import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.model.AgentInfos;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;

import java.util.*;

@Singleton
public class AgentInfosRepository {

    private static final Logger LOGGER = LogManager.getLogger(AgentInfosRepository.class);

    private static final String SQL_SELECT_ALL = "SELECT * FROM agent_infos LIMIT #{limit} OFFSET #{offset}";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM agent_infos WHERE id = #{id}";

    private static final String SQL_SELECT_BY_TYPE = "SELECT * FROM agent_infos WHERE type = #{type}";

    private static final String SQL_INSERT = "INSERT INTO agent_infos (id, hosts, port, uri,type,apikey) " +
            "VALUES (#{id}, #{hosts}, #{port}, #{uri},#{type},#{apikey}) ";



    private static final String SQL_DELETE = "DELETE FROM agent_infos WHERE id = #{id}";
    private static final String SQL_COUNT = "SELECT COUNT(*) AS total FROM agent_infos";

    public AgentInfosRepository() {
    }



    public Future<List<AgentInfos>> selectByType(SqlConnection connection,String type
                                              ) {
        return SqlTemplate
                .forQuery(connection, SQL_SELECT_BY_TYPE)
                .mapTo(AgentInfos.class)
                .execute(Collections.singletonMap("type", type))
                .map(rowSet -> {
                    final List<AgentInfos> agent_infos = new ArrayList<>();
                    rowSet.forEach(agent_infos::add);
                    return agent_infos;
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read type agent_infos", SQL_SELECT_ALL)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read type agent_infos", throwable.getMessage())));
    }




    public Future<List<AgentInfos>> selectAll(SqlConnection connection,
                                              int limit,
                                              int offset) {
        return SqlTemplate
                .forQuery(connection, SQL_SELECT_ALL)
                .mapTo(AgentInfos.class)
                .execute(Map.of("limit", limit, "offset", offset))
                .map(rowSet -> {
                    final List<AgentInfos> agent_infos = new ArrayList<>();
                    rowSet.forEach(agent_infos::add);

                    return agent_infos;
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read all agent_infos", SQL_SELECT_ALL)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all agent_infos", throwable.getMessage())));
    }


    public Future<AgentInfos>  selectById(SqlConnection connection,
                                   Long id) {

        return SqlTemplate
                .forQuery(connection, SQL_SELECT_BY_ID)
                .mapTo(AgentInfos.class)
                .execute(Collections.singletonMap("id", id))
                .map(rowSet -> {
                    final RowIterator<AgentInfos> iterator = rowSet.iterator();

                    if (iterator.hasNext()) {
                        return iterator.next();
                    } else {
                        throw new NoSuchElementException(LogUtils.NO_ENTITY_WITH_ID_MESSAGE.buildMessage(id));
                    }
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read User by id", SQL_SELECT_BY_ID)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read User by id", throwable.getMessage())));
    }


    public Future<AgentInfos> insert(SqlConnection connection,
                                     AgentInfos user) {

        return SqlTemplate
                .forUpdate(connection, SQL_INSERT)
                .mapFrom(AgentInfos.class)
                .mapTo(AgentInfos.class)
                .execute(user)
                .map(rowSet -> {
                    if (rowSet .rowCount()> 0){
                        return user;
                    } else {
                        throw new IllegalStateException(LogUtils.CANNOT_CREATE_ENTITY_MESSAGE.buildMessage());
                    }
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Insert user", SQL_INSERT)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Insert user", throwable.getMessage())));
    }





    public Future<Void> delete(SqlConnection connection,
                               Long id) {
        return SqlTemplate
                .forUpdate(connection, SQL_DELETE)
                .execute(Collections.singletonMap("id", id))
                .flatMap(rowSet -> {
                    if (rowSet.rowCount() > 0) {
                        LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Delete Agent", SQL_DELETE));
                        return Future.succeededFuture();
                    } else {
                        LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Delete Agent", LogUtils.NO_ENTITY_WITH_ID_MESSAGE.buildMessage(id)));
                        throw new NoSuchElementException(LogUtils.NO_ENTITY_WITH_ID_MESSAGE.buildMessage(id));
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
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Count agent_infos", SQL_COUNT)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Count agent_infos", throwable.getMessage())));
    }






}
