package org.limadelrey.vertx4.reactive.rest.api.pages.handler;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import org.limadelrey.vertx4.reactive.rest.api.api.service.AgentInfosService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;

public class TemplatesHandler {
    private final AgentInfosService service= GuiceUtil.getGuice().getInstance(AgentInfosService.class);
    private static final String ID_PARAMETER = "id";
    private static final String PAGE_PARAMETER = "page";
    private static final String LIMIT_PARAMETER = "limit";

    public Future<Void> indexPage(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);
                rc.put("title", "Vert.x Web Example Using Rocker");
                rc.next();
        return Future.succeededFuture();
    }

    public Future<Void> allAgents(RoutingContext rc) {
         service.readAll("1", "10").onSuccess(s ->{
             rc.put("agents",s);
             rc.next();
         });
        return Future.succeededFuture();
    }

}
