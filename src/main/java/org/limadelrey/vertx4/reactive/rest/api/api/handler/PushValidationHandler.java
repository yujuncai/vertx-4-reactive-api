package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import com.google.inject.Singleton;
import io.vertx.core.Vertx;
import io.vertx.ext.web.validation.RequestPredicate;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Bodies;
import io.vertx.ext.web.validation.builder.ParameterProcessorFactory;
import io.vertx.ext.web.validation.builder.Parameters;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;

import static io.vertx.json.schema.common.dsl.Keywords.maxLength;
import static io.vertx.json.schema.common.dsl.Keywords.minLength;
import static io.vertx.json.schema.common.dsl.Schemas.*;
import static io.vertx.json.schema.draft7.dsl.Keywords.maximum;
import static io.vertx.json.schema.draft7.dsl.Keywords.minimum;

@Singleton
public class PushValidationHandler {

    private final Vertx vertx=  Vertx.currentContext().owner();;

    public PushValidationHandler() {

    }


    public ValidationHandler templateMessage1() {
        final SchemaParser schemaParser = buildSchemaParser();
        return ValidationHandler
                .builder(schemaParser)
                .queryParameter(Parameters.param("title", stringSchema().with(minLength(1)).with(maxLength(255))))
                .queryParameter(Parameters.param("content", stringSchema().with(minLength(1)).with(maxLength(255))))
                .build();
    }


    private SchemaParser buildSchemaParser() {
        return SchemaParser.createDraft7SchemaParser(SchemaRouter.create(vertx, new SchemaRouterOptions()));
    }




}
