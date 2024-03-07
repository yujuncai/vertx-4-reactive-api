package org.limadelrey.vertx4.reactive.rest.api.api.handler;

import com.google.inject.Singleton;
import io.vertx.core.Vertx;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Bodies;
import io.vertx.ext.web.validation.builder.Parameters;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;

import static io.vertx.json.schema.common.dsl.Keywords.maxLength;
import static io.vertx.json.schema.common.dsl.Keywords.minLength;
import static io.vertx.json.schema.common.dsl.Schemas.*;
import static io.vertx.json.schema.common.dsl.Schemas.intSchema;
import static io.vertx.json.schema.draft7.dsl.Keywords.maximum;

@Singleton
public class UserValidationHandler {

    private final Vertx vertx=  Vertx.currentContext().owner();;

    public UserValidationHandler() {

    }


    public ValidationHandler LoginMessage() {
        final SchemaParser schemaParser = buildSchemaParser();
        final ObjectSchemaBuilder schemaBuilder = buildBodySchemaBuilder();
        return ValidationHandler
                .builder(schemaParser)
                .body(Bodies.json(schemaBuilder))
                .build();
    }



    private ObjectSchemaBuilder buildBodySchemaBuilder() {
        return objectSchema()
                .requiredProperty("user_name", stringSchema().with(minLength(1)).with(maxLength(255)))
                .requiredProperty("password", stringSchema().with(minLength(1)).with(maxLength(255)));
    }




    private SchemaParser buildSchemaParser() {
        return SchemaParser.createDraft7SchemaParser(SchemaRouter.create(vertx, new SchemaRouterOptions()));
    }




}
