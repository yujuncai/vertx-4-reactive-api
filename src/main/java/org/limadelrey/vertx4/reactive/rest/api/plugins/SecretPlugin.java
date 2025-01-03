package org.limadelrey.vertx4.reactive.rest.api.plugins;

import org.teavm.jso.JSExport;
import org.teavm.jso.dom.html.HTMLDocument;
public class SecretPlugin {
   /* public static void main(String[] args) {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Hello, TeaVM and WebAssembly!");
     *//*   var document = HTMLDocument.current();
        var div = document.createElement("div");
        div.appendChild(document.createTextNode("TeaVM generated element"));
        document.getBody().appendChild(div);*//*
    }*/
    @JSExport
    public static String sayHello() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Hello from Java!");

        return "Hello from Java!";
    }
}
