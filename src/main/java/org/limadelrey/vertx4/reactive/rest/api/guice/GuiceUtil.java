package org.limadelrey.vertx4.reactive.rest.api.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceUtil {

  public static final   Injector instance = Guice.createInjector();
    public static Injector getGuice(){

       return instance;
    }
}
