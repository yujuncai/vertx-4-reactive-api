package org.limadelrey.vertx4.reactive.rest.api.utils;

import io.vertx.core.http.HttpServerResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SseMap {
    public static final Map<String, HttpServerResponse> sseClients = new ConcurrentHashMap<>();

}
