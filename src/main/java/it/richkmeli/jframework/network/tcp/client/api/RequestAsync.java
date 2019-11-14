package it.richkmeli.jframework.network.tcp.client.api;

import com.google.gson.Gson;
import it.richkmeli.jframework.util.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

public class RequestAsync extends Thread {
    private OkHttpClient client;
    private String url;
    private Request request;
    private Response response;
    private Type type;
    private RequestListener requestListener;

    public RequestAsync(RequestListener requestListener, Type type, String url) {
        client = new OkHttpClient();
        this.url = url;
        this.type = type;
        this.requestListener = requestListener;

        request = new Request.Builder()
                .url(url)
                .get()
                .build();

    }


    @Override
    public void run() {
        super.run();

        String result = null;

        try {
            response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                result = responseBody.string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result + "mm");

        // TODO testare che non serva bloccare

        Gson gson = new Gson();
        //String json = gson.toJson(result);

        JSONObject s = null;
        try {
            JSONArray a = new JSONObject(result).getJSONArray("results");
            s = a.getJSONObject(0);

        } catch (JSONException e) {
            Logger.error("RequestAsync: 4xx ", e);

            try {
                // {"statusCode":404,"error":"Not Found"}
                s = new JSONObject(result);
                //type = ErrorResponse.class;
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            //error.printStackTrace();
        }

        //System.out.println("TEST_1: "+String.valueOf(s));

        requestListener.onResult(gson.fromJson(String.valueOf(s), type));


    }


}
