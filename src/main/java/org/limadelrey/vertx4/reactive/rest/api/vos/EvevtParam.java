package org.limadelrey.vertx4.reactive.rest.api.vos;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvevtParam {

    private JsonObject source;
    private JsonObject target;
    private Integer loop;
}
