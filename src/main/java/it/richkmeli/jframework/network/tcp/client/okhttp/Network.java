package it.richkmeli.jframework.network.tcp.client.okhttp;

import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import it.richkmeli.jframework.network.tcp.client.okhttp.util.ResponseParser;
import it.richkmeli.jframework.util.Logger;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Network {

    private String urlString;
    private OkHttpClient client;
    private Headers lastHeaders;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Network() {
        client = new OkHttpClient();
        lastHeaders = null;
    }

    public void setURL(String protocol, String server, String port, String service) throws NetworkException {
        try {
            this.urlString = String.valueOf(new URL(protocol + "://" + server + ":" + port + "/" + service + "/"));
        } catch (MalformedURLException e) {
            throw new NetworkException(e);
        }
    }

    public void deleteSession() {
        lastHeaders = null;
    }


    public void getRequest(String servlet, String jsonParametersString, String additionalParameters, Crypto.Client cryptoClient, NetworkCallback callback) {
        StringBuilder parameters = new StringBuilder("?");
        if (jsonParametersString != null && !jsonParametersString.isEmpty()) {
            JSONObject jsonParameters = new JSONObject(jsonParametersString);
            for (String key : jsonParameters.keySet()) {
                parameters.append("&").append(key).append("=").append(jsonParameters.get(key));
            }
        }

        URL url = null;

        if (cryptoClient != null) {
            String params = parameters.toString();
            try {
                url = new URL(this.urlString + servlet + params);
            } catch (MalformedURLException e) {
                callback.onFailure(new NetworkException(e));
            }

            jsonParametersString = jsonParametersString == null ? "" : jsonParametersString;
            //String encryptedParameters = cryptoClient.encrypt(params);
            // when encryption is enabled they are passed as JSON
            Logger.info("Get request to: (decrypted) " + url + " :\"" + jsonParametersString + "\"");
            String encryptedParameters = null;
            try {
                encryptedParameters = cryptoClient.encrypt(jsonParametersString);
            } catch (CryptoException e) {
                callback.onFailure(e);
            }

            Logger.info("Get request to:  (encrypted) " + url + " :\"" + encryptedParameters + "\"");

            parameters = new StringBuilder("?" + additionalParameters + "&data=" + encryptedParameters);
        }

//        URL url = null;
        try {
            url = new URL(this.urlString + servlet + parameters);
        } catch (MalformedURLException e) {
            callback.onFailure(new NetworkException(e));
        }


        Logger.info("Get request to: " + url);

        Request request = buildRequestWithHeader(VERB.GET, url, lastHeaders, null);


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = "";
                if (response.body() != null) {
                    jsonResponse = response.body().string().trim();
                } else {
                    Logger.error("response.body() == null");
                }

                lastHeaders = setHeader(response, lastHeaders);

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


                        callback.onSuccess(jsonResponse);
                    } else {
                        Logger.info("GET response: " + jsonResponse);

                        callback.onSuccess(jsonResponse);
                    }
                } else {
                    Logger.info("GET response: " + jsonResponse);

                    callback.onFailure(new Exception(ResponseParser.parseMessage(jsonResponse)));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(new NetworkException(e));
            }
        });
    }

    enum VERB {
        GET, PUT
    }

    private static Request buildRequestWithHeader(VERB verb, URL url, Headers lastHeaders, RequestBody body) {
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
                if (lastHeaders != null) {
                    if (lastHeaders.get("Set-Cookie") != null) {
                        builder.addHeader("Cookie", lastHeaders.get("Set-Cookie"));
                    } else {
                        Logger.error("lastHeaders.get(\"Set-Cookie\") is null");
                    }
                } else {
                    Logger.error("lastHeaders is null");
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

    private static Headers setHeader(Response response, Headers lastHeaders) {
        if (response.headers().get("Set-Cookie") != null) {
            if (lastHeaders != null) {

                if (lastHeaders.get("Set-Cookie") != null) {

                    for (String s : response.headers("Set-Cookie")) {
                        String[] ss = s.split("=");
                        if (ss[0].equalsIgnoreCase("JFRAMEWORKSESSIONID")) {
                            //JFRAMEWORKSESSIONID = ss[1];
                            Logger.info("JFRAMEWORKSESSIONID present");
                            lastHeaders = response.headers();
                        }
                    }


                } else {
                    Logger.error("lastHeaders.get(\"Set-Cookie\") == null");
                    lastHeaders = response.headers();
                }
            } else {
                Logger.error("lastHeaders == null");
                lastHeaders = response.headers();
            }
        }
        return lastHeaders;
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

        request = buildRequestWithHeader(VERB.GET, url, lastHeaders, null);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string().trim();

                lastHeaders = setHeader(response, lastHeaders);

                Logger.info("GET response: " + jsonResponse);

                callback.onSuccess(jsonResponse);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(new NetworkException(e));
            }
        });
    }

//    public void getRequestCompat(String servlet, String jsonParametersString, boolean encryption, NetworkCallback callback) {
//        StringBuilder parameters = new StringBuilder("?");
//        if (jsonParametersString != null && !jsonParametersString.isEmpty()) {
//            JSONObject jsonParameters = new JSONObject(jsonParametersString);
//            for (String key : jsonParameters.keySet()) {
//                parameters.append("&").append(key).append("=").append(jsonParameters.get(key));
//            }
//        }
//
//        PrivateKey RSAprivateKeyClient = null;
//        try {
//            if (encryption) {
//                KeyPair keyPair = CryptoCompat.getGeneratedKeyPairRSA();
//                PublicKey RSApublicKeyClient = keyPair.getPublic();
//                RSAprivateKeyClient = keyPair.getPrivate();
//
//                parameters.append("?&encryption=true&Kpub=").append(CryptoCompat.savePublicKey(RSApublicKeyClient));
//            }
//        } catch (GeneralSecurityException | CryptoException e) {
//            callback.onFailure(new NetworkException(e));
//        }
//
//        URL url = null;
//        try {
//            url = new URL(this.url + servlet + parameters);
//        } catch (MalformedURLException e) {
//            callback.onFailure(new NetworkException(e));
//        }
//
//        Request request;
//
//        Logger.info("Get request to: " + url);
//
//        if (lastHeaders != null)
//            request = new Request.Builder()
//                    .url(url)
//                    .addHeader("Cookie", lastHeaders.get("Set-Cookie"))
//                    .get()
//                    .build();
//        else
//            request = new Request.Builder()
//                    .url(url)
//                    .get()
//                    .build();
//
//        PrivateKey finalRSAprivateKeyClient = RSAprivateKeyClient;
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String jsonResponse = response.body().string().trim();
//                if (response.headers().get("Set-Cookie") != null)
//                    lastHeaders = response.headers();
//
//                try {
//
//                    if (encryption) {
//                        Logger.info("GET response (encrypted): " + jsonResponse);
//
//                        String messageResponse = ResponseParser.parseMessage(jsonResponse);
//
//                        Type listType = new TypeToken<KeyExchangePayloadCompat>() {
//                        }.getType();
//                        Gson gson = new Gson();
//                        KeyExchangePayloadCompat keyExchangePayload = gson.fromJson(messageResponse, listType);
//
//                        SecretKey AESsecretKey = CryptoCompat.getAESKeyFromKeyExchange(keyExchangePayload, finalRSAprivateKeyClient);
//                        String data = keyExchangePayload.getData();
//
//                        messageResponse = CryptoCompat.decryptRC4(data, new String(AESsecretKey.getEncoded()));
//
//                        //CREATE new JSON
//                        JSONObject json = new JSONObject(jsonResponse);
//                        json.remove("message");
//                        json.put("message", messageResponse);
//                        jsonResponse = json.toString();
//
//                        Logger.info("GET response (decrypted): " + jsonResponse);
//
//                        callback.onSuccess(jsonResponse);
//                    } else {
//                        Logger.info("GET response: " + jsonResponse);
//
//                        callback.onSuccess(jsonResponse);
//                    }
//                } catch (CryptoException e) {
//                    Logger.error(e.getMessage());
//                    callback.onFailure(new NetworkException(e));
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                callback.onFailure(new NetworkException(e));
//            }
//        });
//    }

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


        request = buildRequestWithHeader(VERB.PUT, url, lastHeaders, body);
        ;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string().trim();
                lastHeaders = setHeader(response, lastHeaders);

                if (cryptoClient != null) {
                    Logger.info("PUT response (encrypted): " + jsonResponse);

                    //TODO DECRYPT

                    Logger.info("PUT response (decrypted): " + jsonResponse);

                    callback.onSuccess(jsonResponse);
                } else {
                    Logger.info("PUT response: " + jsonResponse);

                    callback.onSuccess(jsonResponse);
                }
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

        Request request = buildRequestWithHeader(VERB.GET, url, lastHeaders, null);

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new NetworkException(e);
        }

        String responseString = null;
        try {
            responseString = response.body().string().trim();
        } catch (IOException e) {
            throw new NetworkException(e);
        }

        Logger.info(responseString);

        lastHeaders = setHeader(response, lastHeaders);

        return responseString;
    }

}

