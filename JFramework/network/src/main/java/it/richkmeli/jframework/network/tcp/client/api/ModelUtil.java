package it.richkmeli.jframework.network.tcp.client.api;

import java.lang.reflect.Field;

public class ModelUtil {

    public static <MODEL> String getURLWithParameters(String service, MODEL model) {

        StringBuilder stringBuilder = new StringBuilder();
        boolean oneFieldinitialized = false;
        //for (Field field : this.getClass().getFields()) {
        for (Field field : model.getClass().getDeclaredFields()) {
            if (field != null) {
                oneFieldinitialized = true;
                stringBuilder.append(field.toString()); // todo aggiungi aggiunta valori presenti nell'istanza che si passa limit = 1
            }
        }

        if (oneFieldinitialized)
            return service + "?" + stringBuilder.toString();
        else
            return service;
    }

}

