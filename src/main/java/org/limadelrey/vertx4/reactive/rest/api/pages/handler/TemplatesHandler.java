package org.limadelrey.vertx4.reactive.rest.api.pages.handler;

import cn.hutool.core.util.URLUtil;
import com.fizzed.rocker.Rocker;
import com.fizzed.rocker.RockerOutput;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.rocker.RockerTemplateEngine;
import org.apache.commons.lang3.StringUtils;
import org.limadelrey.vertx4.reactive.rest.api.api.model.Book;
import org.limadelrey.vertx4.reactive.rest.api.api.model.BookGetAllResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.model.BookGetByIdResponse;
import org.limadelrey.vertx4.reactive.rest.api.api.service.BookService;
import org.limadelrey.vertx4.reactive.rest.api.guice.GuiceUtil;
import org.limadelrey.vertx4.reactive.rest.api.utils.ResponseUtils;
import templates.index;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TemplatesHandler {

    private static final String ID_PARAMETER = "id";
    private static final String PAGE_PARAMETER = "page";
    private static final String LIMIT_PARAMETER = "limit";
    private final BookService bookService= GuiceUtil.getGuice().getInstance(BookService.class);

    public Future<Void> indexPage(RoutingContext rc) {
        final String id = rc.pathParam(ID_PARAMETER);
        if(StringUtils.isNotBlank(id)) {
            Future<BookGetByIdResponse> bookGetByIdResponseFuture = bookService.readOne(Integer.parseInt(id));
            bookGetByIdResponseFuture.onSuccess(bookGetByIdResponse -> {
                rc.put("title", "Vert.x Web Example Using Rocker");
                rc.put("name", bookGetByIdResponse.getAuthor());
                rc.put("path", rc.request().path());
                rc.next();
            });
        }else{
            rc.put("title", "Impact");
            rc.put("name", "22");
            rc.put("path","1" );
            rc.next();
        }
        return Future.succeededFuture();
    }


    public Future<Void> basicPage(RoutingContext rc) {
        rc.put("name", "HAHAHAHAH");
        rc.next();
        return Future.succeededFuture();
    }
}
