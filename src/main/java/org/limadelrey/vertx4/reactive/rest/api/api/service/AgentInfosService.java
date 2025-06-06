package org.limadelrey.vertx4.reactive.rest.api.api.service;

import com.google.inject.Singleton;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.api.model.AgentInfos;
import org.limadelrey.vertx4.reactive.rest.api.api.model.AgentInfosGetAllResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.model.AgentInosGetByIdResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.repository.AgentInfosRepository;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.DbUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.LogUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.QueryUtils;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class AgentInfosService {

    private static final Logger LOGGER = LogManager.getLogger(AgentInfosService.class);

    private final Pool dbClient ;
    private final AgentInfosRepository agentInfosRepository= GuiceUtil.getGuice().getInstance(AgentInfosRepository.class);

    public AgentInfosService() {

        dbClient= DbUtils.getInstance();
}



    public Future<AgentInfosGetAllResponse> readAll(String p,
                                              String l) {

        return dbClient.withTransaction(
                connection -> {
                    final int page = QueryUtils.getPage(p);
                    final int limit = QueryUtils.getLimit(l);
                    final int offset = QueryUtils.getOffset(page, limit);
                    LOGGER.info("page {}  , limit {} ,  offset {}" , page, limit, offset);
                    return agentInfosRepository.count(connection)
                            .flatMap(total ->
                                    agentInfosRepository.selectAll(connection, limit, offset)
                                            .map(result -> {
                                                final List<AgentInosGetByIdResponse> users = result.stream()
                                                        .map(AgentInosGetByIdResponse::new)
                                                        .collect(Collectors.toList());

                                                return new AgentInfosGetAllResponse(total, limit, page, users);
                                            })
                            );
                })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read all users", success.getAgents())))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all users", throwable.getMessage())));
    }


    public Future<AgentInosGetByIdResponse> readOne(long id) {

        return dbClient.withTransaction(
                connection -> agentInfosRepository.selectById(connection, id)
                        .map(AgentInosGetByIdResponse::new))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read one user", success)))
                .onFailure(Throwable::printStackTrace);
    }


    public Future<AgentInosGetByIdResponse> create(AgentInfos u) {
        return dbClient.withTransaction(
                connection -> agentInfosRepository.insert(connection, u)
                        .map(AgentInosGetByIdResponse::new))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Create one user", success)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Create one user", throwable.getMessage())));
    }




    public Future<List<AgentInosGetByIdResponse>> selectByType(String type,String action) {

        return dbClient.withTransaction(
                        connection -> {
                            return  agentInfosRepository.selectByType(connection,type,action)
                                                    .map(result -> {
                                                        final List<AgentInosGetByIdResponse> list = result.stream()
                                                                .map(AgentInosGetByIdResponse::new)
                                                                .collect(Collectors.toList());
                                                        return list;
                                                    })
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Read all users", success)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Read all users", throwable.getMessage())));
    });

    }




    public Future<Void> delete(Long id) {
        return dbClient.withTransaction(
                connection -> agentInfosRepository.delete(connection, id))
                .onSuccess(success -> LOGGER.info(LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage("Delete one ", id)))
                .onFailure(throwable -> LOGGER.error(LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage("Delete one ", throwable.getMessage())));
    }








}
