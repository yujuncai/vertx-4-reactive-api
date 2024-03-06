package org.limadelrey.vertx4.reactive.rest.api;

import cn.hutool.core.util.URLUtil;

import java.net.URL;

public class Test {
    public static void main(String[] args) {
        String path="http://localhost:8889/pages/v1/index";

        System.out.println( URLUtil.getPath(path));

        String normalize = URLUtil.normalize(path);

        URL url = URLUtil.url(normalize);

        System.out.println(url.getProtocol());
        System.out.println(url.getHost());
        System.out.println(url.getPort());

    }
}
