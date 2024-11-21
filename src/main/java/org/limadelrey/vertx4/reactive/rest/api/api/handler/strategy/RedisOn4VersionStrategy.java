package org.limadelrey.vertx4.reactive.rest.api.api.handler.strategy;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.limadelrey.vertx4.reactive.rest.api.message.CorsorMessage;
import org.limadelrey.vertx4.reactive.rest.api.message.KeyVo;
import org.limadelrey.vertx4.reactive.rest.api.utils.ConfigUtils;
import org.limadelrey.vertx4.reactive.rest.api.utils.RedisUtils;
import org.limadelrey.vertx4.reactive.rest.api.verticle.RedisVerticle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RedisOn4VersionStrategy implements  RedisStrategy{
    private static final Logger LOGGER = LogManager.getLogger(RedisOn4VersionStrategy.class);
    private  static  final String ONE="1";
    private  static  final String TWO="2";
    private  static  final String START="0";
    @Override
    public Future handler(Integer dbSize)  {

        final Properties properties = ConfigUtils.getInstance().getProperties();
        String threshold = properties.getProperty("scan.threshold.byte");

        RedisAPI instance = RedisUtils.getInstance();
        String uuid = UUID.randomUUID().toString();
            loadScripts(instance).onSuccess(s ->
            {
                scanKeys(instance,s,threshold,START,uuid);
            });



        return Future.succeededFuture();
    }



    private  void scanKeys(RedisAPI instance, String sha,String threshold, String cur,String uuid) {

        instance.eval(List.of(sha, ONE, TWO, threshold, cur),o ->{

            if(o.succeeded()){
                Response result = o.result();
              //  LOGGER.info("result {}",result);
                List<KeyVo> list=new ArrayList<>(100);

                result.stream().forEach(key ->{
                    String string = key.toString();
                    if(string.startsWith("cursor:->")){
                        String[] split = string.split(":->");
                        String newCur=split[1];
                        if(!newCur.equals("0")){
                            scanKeys(instance,sha,threshold,newCur,uuid);
                        }else {
                            LOGGER.info("结束SCAN CUR "+newCur);

                        }
                    }else{
                        String[] split = string.split(":->");
                        String keyName=split[0];
                        String keySize=split[1];
                        String keyTtl=split[2];
                        String keyType=split[3];
                        KeyVo vo=new KeyVo();
                        vo.setKey(keyName);
                        vo.setTtl(Integer.valueOf(keyTtl));
                        vo.setLengthOrsize(Integer.valueOf(keySize));
                        vo.setType(keyType);
                        list.add(vo);
                    }
                });


                if(list!=null&&list.size()>0)
                {
                    DeliveryOptions options = new DeliveryOptions().setCodecName("myCodec");//必须指定名字
                    EventBus eb = Vertx.currentContext().owner().eventBus();
                    CorsorMessage message = new CorsorMessage();
                    message.setPingId(uuid);
                    message.setList(list);
                    eb.send(RedisVerticle.CONSUMER_ADDRESS, message,options );
                }

            }else{
                LOGGER.error(o.cause());
            }

        });
    }





    private Future<String> loadScripts(final RedisAPI redisAPI)  {
        String script = null;
        try (InputStream inputStream = RedisOn4VersionStrategy.class.getResourceAsStream(
                "/on4bigkey.lua")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String        line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
                script = sb.toString();
            }

            redisAPI.send(Command.SCRIPT, "load", script).onSuccess(res -> {
                LOGGER.info("Redis 加载高版本Redis的: "+ res.toString());
            }).onFailure(err ->  LOGGER.error("Redis 加载高版本Redis的 Lua 脚本出错"+ err));

        } catch (IOException e) {
           LOGGER.error("Redis 加载高版本Redis的 Lua 脚本出错"+ e);
        }
        return Future.succeededFuture(script);

        }



}
