package it.richkmeli.jframework.network.tcp.client.okhttp;

import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import it.richkmeli.jframework.network.tcp.client.okhttp.util.ResponseParser;
import it.richkmeli.jframework.util.log.Logger;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network {

    private String urlString;
    private final OkHttpClient client;
    private static List<String> cookieList;
    private static Map<String, String> headerList;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Network() {
        client = new OkHttpClient();
        //lastHeaders = null;
        cookieList = new ArrayList<>();
        headerList = new HashMap<>();
    }

    public OkHttpClient getClient() {
        return client;
    }

    public static Map<String, String> getHeaderList() {
        return headerList;
    }

    public void setCredentials(String username, String password){
        String credential = Credentials.basic(username, password);
        headerList.put("Authorization", credential); //Authorization: Basic
        /*client.newBuilder().authenticator(new Authenticator() {
            @Override public Request authenticate(Route route, Response response) throws IOException {
                if (response.request().header("Authorization") != null) {
                    return null; // Give up, we've already attempted to authenticate.
                }

                Logger.info("Authenticating for response: " + response);
                Logger.info("Challenges: " + response.challenges());
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder()
                        .header("Authorization", credential)
                        .build();
            }
        });*/
    }

    public void setUserAgent(String userAgent){
        headerList.put("User-Agent", "Richk RichkClient/X-1.0.0 info@richk.com");
    }

    public void setURL(String protocol, String server, String port, String service) throws NetworkException {
        try {
            this.urlString = String.valueOf(new URL(protocol + "://" + server + ":" + port + "/" + service + "/"));
        } catch (MalformedURLException e) {
            throw new NetworkException(e);
        }
    }

    public void deleteSession() {
        cookieList = new ArrayList<>();
    }

    /**
     * @param servlet
     * @param jsonParametersString if crypto is not null, these parameters are encrypted
     * @param additionalParameters these parameters are always not encrypted
     * @param cryptoClient
     * @param isJsonResponse
     * @param callback
     */

    public void getRequest(String servlet, String jsonParametersString, String additionalParameters, Crypto.Client cryptoClient, boolean isJsonResponse, NetworkCallback callback) {
        String parameters = "";
        boolean jsonParamCondition = jsonParametersString != null && !jsonParametersString.isEmpty();
        boolean additionalParamCondition = additionalParameters != null && !additionalParameters.isEmpty();

        Logger.info("GET request: servlet: " + servlet + ", param:" + jsonParametersString + ", additionalParam: " + additionalParameters);

        if (cryptoClient != null) {

            // if null (no parameters), it is set as empty string for CryptoClient
            jsonParametersString = jsonParametersString == null ? "" : jsonParametersString;

            //String encryptedParameters = cryptoClient.encrypt(params);
            // when encryption is enabled they are passed as JSON
            String encryptedParameters = null;
            try {
                encryptedParameters = cryptoClient.encrypt(jsonParametersString);
            } catch (CryptoException e) {
                callback.onFailure(e);
            }

            // reformatting param for url
            if (additionalParamCondition) {
                parameters = "?" + additionalParameters + "&data=" + encryptedParameters;
            } else {
                parameters = "?data=" + encryptedParameters;
            }

        } else {
            if (additionalParamCondition || jsonParamCondition) {
                parameters = "?";
            }

            if (additionalParamCondition) {
                parameters += additionalParameters;
                if (jsonParamCondition) {
                    parameters += "&";
                    parameters += JsonParamToUrlParam(jsonParametersString);
                }
            } else {
                if (jsonParamCondition) {
                    parameters += JsonParamToUrlParam(jsonParametersString);
                }
            }

        }

        // composing url with serer name, servlet name and parameters
        URL url = null;
        try {
            url = new URL(this.urlString + servlet + parameters);
        } catch (MalformedURLException e) {
            callback.onFailure(new NetworkException(e));
        }

        Logger.info("GET request: " + url);

        Request request = buildRequestWithHeader(VERB.GET, url, null);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = "";
                if (response.body() != null) {
                    jsonResponse = response.body().string().trim();
                } else {
                    Logger.error("response.body() == null");
                }

                /*lastHeaders = */
                saveCookies(response/*, lastHeaders*/);

                if (isJsonResponse) {
                    try {
                        if (ResponseParser.parseStatus(jsonResponse).equalsIgnoreCase("ok")) {

                            if (cryptoClient != null) {
                                Logger.info("GET response (encrypted): " + jsonResponse);

                                String messageResponse = ResponseParser.parseMessage(jsonResponse);

                                try {
                                    messageResponse = cryptoClient.decrypt(messageResponse);
                                } catch (CryptoException e) {
                                    callback.onFailure(e);
                                }

                                //CREATE new JSON
                                JSONObject json = new JSONObject(jsonResponse);
                                json.remove("message");
                                json.put("message", messageResponse);
                                jsonResponse = json.toString();

                                Logger.info("GET response (decrypted): " + jsonResponse);

                                //callback.onSuccess(jsonResponse);
                            } else {
                                Logger.info("GET response: " + jsonResponse);
                                //callback.onSuccess(jsonResponse);
                            }
                            callback.onSuccess(jsonResponse);
                        } else {
                            Logger.info("GET response: " + jsonResponse);
                            callback.onFailure(new Exception(ResponseParser.parseMessage(jsonResponse)));
                        }
                    } catch (JSONException e) {
                        Logger.error("Error parsing GET response: " + jsonResponse, e);
                        callback.onFailure(new Exception(ResponseParser.parseMessage(jsonResponse)));
                    }
                } else {
                    callback.onSuccess(jsonResponse);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(new NetworkException(e));
            }
        });
    }

    public void getRequest(String servlet, String jsonParametersString, String additionalParameters, Crypto.Client cryptoClient, NetworkCallback callback) {
        getRequest(servlet, jsonParametersString, additionalParameters, cryptoClient, true, callback);
    }

    enum VERB {
        GET, PUT
    }

    private Request buildRequestWithHeader(VERB verb, URL url, RequestBody body) {
        Request request = null;
        // create builder, in which has to be set the request details
        Request.Builder builder = new Request.Builder();
        try {
            if (url != null) {
                // set url
                builder.url(url);
                // set http verb
                switch (verb) {
                    case GET:
                        builder.get();
                        break;
                    case PUT:
                        builder.put(body);
                        break;
                    default:
                        Logger.error("VERB: " + verb.name() + " is not implemented");
                }

                // check headers
                StringBuilder stringBuilder = new StringBuilder();
                for (String key : cookieList) {
                    stringBuilder.append(key).append(";");
                }
                //builder.addHeader("Cookie", stringBuilder.toString());
                headerList.put("Cookie", stringBuilder.toString());

                for(String headerName : headerList.keySet()){
                    //Logger.info(headerName + " - " + headerList.get(headerName));
                    builder.addHeader(headerName, headerList.get(headerName));
                }

            } else {
                Logger.error("url is null");
            }
            request = builder.build();
        } catch (Exception e) {
            Logger.error(e);
            e.printStackTrace();
        }
        return request;
    }

    public static String urlParamToJsonParam(String urlParam) {
        urlParam = urlParam.replaceAll("=", "\":\"");
        urlParam = urlParam.replaceAll("&", "\",\"");
        return "{\"" + urlParam + "\"}";
    }

    public static String JsonParamToUrlParam(String jsonParam) {
        jsonParam = jsonParam.replaceAll("\":\"", "=");
        jsonParam = jsonParam.replaceAll("\",\"", "&");
        jsonParam = jsonParam.replace("{\"", "");
        jsonParam = jsonParam.replace("\"}", "");
        return jsonParam;
    }

    private static void saveCookies(Response response) {
        for (String s : response.headers("Set-Cookie")) {
            if (cookieList.contains(s)) {
            } else {
                cookieList.add(s);
            }
        }
        //Logger.info("CookieMap: " + cookieList);

    }

    public void getRequestCompat(String servlet, String jsonParametersString, NetworkCallback callback) {
        StringBuilder parameters = new StringBuilder("?");
        if (jsonParametersString != null && !jsonParametersString.isEmpty()) {
            JSONObject jsonParameters = new JSONObject(jsonParametersString);
            for (String key : jsonParameters.keySet()) {
                parameters.append("&").append(key).append("=").append(jsonParameters.get(key));
            }
        }

        URL url = null;
        try {
            url = new URL(this.urlString + servlet + parameters);
        } catch (MalformedURLException e) {
            callback.onFailure(new NetworkException(e));
        }

        Request request = null;

        Logger.info("Get request to: " + url);

        request = buildRequestWithHeader(VERB.GET, url, null);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String jsonResponse = "";
                if (responseBody != null) {
                    jsonResponse = responseBody.string().trim();
                } else {
                    Logger.error("ResponseBody is null");
                }

                saveCookies(response);

                Logger.info("GET response: " + jsonResponse);

                callback.onSuccess(jsonResponse);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(new NetworkException(e));
            }
        });
    }

    public void putRequest(String servlet, String jsonParamentesString, Crypto.Client cryptoClient, NetworkCallback callback) {

        JSONObject jsonParameters = new JSONObject(jsonParamentesString);

        if (cryptoClient != null) {
            //TODO ENCRYPT
        }

        URL url = null;
        try {
            url = new URL(this.urlString + servlet);
        } catch (MalformedURLException e) {
            callback.onFailure(new NetworkException(e));
        }

        Request request;

        Logger.info("Put request to: " + url);
        Logger.info("Put body json: " + jsonParameters.toString());

        RequestBody body = RequestBody.create(JSON, jsonParameters.toString());


        request = buildRequestWithHeader(VERB.PUT, url, body);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String jsonResponse = "";
                if (responseBody != null) {
                    jsonResponse = responseBody.string().trim();
                } else {
                    Logger.error("ResponseBody is null");
                }
                /*lastHeaders = */
                saveCookies(response/*, lastHeaders*/);

                if (cryptoClient != null) {
                    Logger.info("PUT response (encrypted): " + jsonResponse);

                    //TODO DECRYPT

                    Logger.info("PUT response (decrypted): " + jsonResponse);
                    //callback.onSuccess(jsonResponse);
                } else {
                    Logger.info("PUT response: " + jsonResponse);
                    //callback.onSuccess(jsonResponse);
                }
                callback.onSuccess(jsonResponse);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(new NetworkException(e));
            }
        });
    }

    public String GetRequestSync(String parameter) throws NetworkException {
        URL url = null;
        try {
            url = new URL(this.urlString + parameter);
        } catch (MalformedURLException e) {
            throw new NetworkException(e);
        }

        Response response;


        Logger.info("Request to: " + url);

        Request request = buildRequestWithHeader(VERB.GET, url, null);

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new NetworkException(e);
        }

        String responseString = null;
        try {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                responseString = responseBody.string().trim();
            } else {
                Logger.error("ResponseBody is null");
            }
        } catch (IOException e) {
            throw new NetworkException(e);
        }

        Logger.info(responseString);

        saveCookies(response);

        return responseString;
    }

}

