package it.richkmeli.jframework.network.tcp.server.http.payload.response;

import org.json.JSONObject;

public abstract class Response {
    // VERBOSE: true=message visible; false=message not visible TODO prendi da file di config
    private static boolean VERBOSE = true;
    private String status;
    private StatusCode statusCode;
    private String additionalMessage;

    public Response(String status, StatusCode statusCode) {
        setStatus(status);
        setStatusCode(statusCode);
    }

    public Response(String status, StatusCode statusCode, String additionalMessage) {
        setStatus(status);
        setStatusCode(statusCode);
        setAdditionalMessage(additionalMessage);
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getAdditionalMessage() {
        return additionalMessage;
    }

    /**
     * Concatenate StatusCode message and additionalMessage of this response
     * @return the concatenated message
     */
    public String getMessage() {
        if (VERBOSE) {
            String message;
            if (this.additionalMessage == null) {
                // no additionalMessage, so return only "message"
                return this.statusCode.getMessage();
            } else {
                if ("".equalsIgnoreCase(this.statusCode.getMessage())) {
                    // message is empty, so return only "additionalMessage" (success status code o generic error)
                    return this.additionalMessage;
                } else {
                    // concat "message. additionalMessage"
                    return this.statusCode.getMessage() + ". " + this.additionalMessage;
                }
            }
        } else {
            return "";
        }
    }

    public void setAdditionalMessage(String message) {
        this.additionalMessage = message;
    }

    public String json() {
        JSONObject output = new JSONObject();
        output.put("status", getStatus());
        output.put("statusCode", getStatusCode().getCode());
        output.put("message", getMessage());
        return output.toString();
    }
}
