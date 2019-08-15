package it.richkmeli.jframework.network;

import java.lang.reflect.Field;

public class ModelUtil {

    public static <MODEL> String getURLWithParameters(String service, MODEL model) {

        //TODO da testare
        StringBuilder stringBuilder = new StringBuilder();
        boolean oneFieldinitialized = false;
        //for (Field field : this.getClass().getFields()) {
        for (Field field : model.getClass().getFields()) {
            if (field != null) {
                oneFieldinitialized = true;
                stringBuilder.append(field.toString());
            }
        }

        if (oneFieldinitialized)
            return service + "?" + stringBuilder.toString();
        else
            return service;
    }

}

