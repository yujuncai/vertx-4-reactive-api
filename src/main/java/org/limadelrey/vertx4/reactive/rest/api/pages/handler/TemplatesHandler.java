package org.limadelrey.vertx4.reactive.rest.api.pages.handler;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import org.limadelrey.vertx4.reactive.rest.api.api.model.Book;
import org.limadelrey.vertx4.reactive.rest.api.api.model.BookGetAllResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.model.BookGetByIdResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.service.BookService;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;

public class TemplatesHandler {




    public Future<Void> indexPage(RoutingContext ctx) {


            ctx.put("title", "Vert.x Web Example Using Rocker");
            ctx.put("name", "Rocker");
            ctx.put("path", ctx.request().path());
            ctx.next();
            return null;
    }



    public Future<Void> basicPage(RoutingContext ctx) {
        ctx.put("name", "HAHAHAHAH");
        ctx.next();
        return null;
    }
}
