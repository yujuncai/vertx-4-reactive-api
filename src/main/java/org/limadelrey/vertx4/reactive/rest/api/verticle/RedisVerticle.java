package org.limadelrey.vertx4.reactive.rest.api.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.Configuration;
import org.limadelrey.vertx4.reactive.rest.api.api.handler.strategy.RedisOn4VersionStrategy;
import org.limadelrey.vertx4.reactive.rest.api.codec.CustomizeMessageCodec;
import org.limadelrey.vertx4.reactive.rest.api.message.CorsorMessage;
import org.limadelrey.vertx4.reactive.rest.api.message.KeyVo;
import org.limadelrey.vertx4.reactive.rest.api.utils.ConfigUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.DbUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.HeapSortUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RedisVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(RedisVerticle.class);
    public static  final  String CONSUMER_ADDRESS="com.redis";

    private static ConcurrentHashMap <String,List<KeyVo>> redisMap=new ConcurrentHashMap<>();

    final static Properties properties = ConfigUtils.getInstance().getProperties();
    static  Integer N = Integer.valueOf(properties.getProperty("heapSort.topn"));
    @Override
    public void start(Promise<Void> promise) {
        EventBus eb = vertx.eventBus();
        eb.registerCodec(CustomizeMessageCodec.create());//注册编码器
        //注册处理器,消费com.hou发送的消息
        MessageConsumer<Object> consumer = eb.consumer(CONSUMER_ADDRESS);//订阅地址
        consumer.handler(message -> {//消息处理器
            if(message.body() instanceof CorsorMessage){

                CorsorMessage body = (CorsorMessage) message.body();
                String pingId= body.getPingId();
                List<KeyVo> list = body.getList();
                if(list!=null&&list.size()>0) {
                    LOGGER.info("收到eventBus pingid{} szie {}",pingId,list.size());
                    ArrayList<KeyVo> keyVos = getTopArray(list,N);


                    if(redisMap.contains(pingId)){
                        redisMap.get(pingId).addAll(keyVos);
                    }else{
                        redisMap.put(pingId,keyVos);
                    }



                    if( redisMap.get(pingId).size()>(5*N)){
                        ArrayList<KeyVo> all_keyVos=  getTopArray(redisMap.get(pingId),N);
                        redisMap.get(pingId).clear();
                        redisMap.get(pingId).addAll(all_keyVos);
                    }
                    LOGGER.info("收到eventBus 排序完成 ");
                }
            }
        }).completionHandler(res -> {//注册完成后通知事件,适用于集群中比较慢的情况下
            LOGGER.info("注册处理器结果"+res.succeeded());
        });
        //撤销处理器
        //consumer.unregister();
        promise.complete();
    }

    private ArrayList<KeyVo> getTopArray(List<KeyVo> all,int N) {


        KeyVo[] all_arr = all.toArray(new KeyVo[0]);
        HeapSortUtils.heapSort(all_arr);
        KeyVo[] all_arr_topN = new KeyVo[Math.min(N, all_arr.length)];
        System.arraycopy(all_arr, 0, all_arr_topN, 0, Math.min(N, all_arr.length));
        ArrayList<KeyVo> all_keyVos = new ArrayList<>(List.of(all_arr_topN));
        return all_keyVos;
    }

    
    public static Future<List<KeyVo>> getRedisTopN(String pingId){
        return Future.succeededFuture(redisMap.get(pingId));
    }


    public static Future<List<KeyVo>> clearRedisTopN(String pingId){
        redisMap.get(pingId).clear();
        return    Future.succeededFuture();
    }


}
