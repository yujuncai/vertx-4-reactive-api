package org.limadelrey.vertx4.reactive.rest.api.api.handler.strategy;

import io.vertx.core.Future;
import io.vertx.redis.client.RedisAPI;

public class RedisContext {

    private RedisStrategy strategy;

    private RedisAPI instance;
    private Integer dbSize;
    /**
     * 构造函数，传入一个具体策略对象
     *
     * @param strategy 具体策略对象
     */
    public RedisContext(RedisStrategy strategy,Integer dbSize, RedisAPI instance ){
        this.strategy = strategy;
        this.dbSize=dbSize;
       this.instance= instance;
    }
    /**
     * 策略方法
     */
    public Future contextInterface(){

       return strategy.handler(dbSize,instance);
    }

}
