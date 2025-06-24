package org.limadelrey.vertx4.reactive.rest.api;

import cn.hutool.core.util.URLUtil;

public class Test {
    public static void main(String[] args) {
        String path="http://localhost:8889/pages/v1/index";

        System.out.println( URLUtil.getPath(path));

        String normalize = URLUtil.normalize(path);
        System.out.println( normalize);
        System.out.println(Runtime.getRuntime().availableProcessors()/16 == 0 ?1 : Runtime.getRuntime().availableProcessors()/16);
    }
}
