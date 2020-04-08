
package com.baeldung.captcha;

import com.fasterxml.jackson.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "success", "score", "action","challenge_ts", "hostname", "error-codes" })
public class GoogleResponse {

    @JsonProperty("success")
    private boolean success;
    @JsonProperty("challenge_ts")
    private String challengeTs;
    @JsonProperty("hostname")
    private String hostname;
    @JsonProperty("score")
    private float score;
    @JsonProperty("action")
    private String action;
    @JsonProperty("error-codes")
    private ErrorCode[] errorCodes;

    
    static enum ErrorCode {
        MissingSecret, InvalidSecret, MissingResponse, InvalidResponse, BadRequest, TimeoutOrDuplicate;

        private static Map<String, ErrorCode> errorsMap = new HashMap<String, ErrorCode>(4);

        static {
            errorsMap.put("missing-input-secret", MissingSecret);
            errorsMap.put("invalid-input-secret", InvalidSecret);
            errorsMap.put("missing-input-response", MissingResponse);
            errorsMap.put("bad-request", InvalidResponse);
            errorsMap.put("invalid-input-response", BadRequest);
            errorsMap.put("timeout-or-duplicate", TimeoutOrDuplicate);
        }

        @JsonCreator
        public static ErrorCode forValue(final String value) {
            return errorsMap.get(value.toLowerCase());
        }
    }

    @JsonProperty("success")
    public boolean isSuccess() {
        return success;
    }

    @JsonProperty("success")
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @JsonProperty("challenge_ts")
    public String getChallengeTs() {
        return challengeTs;
    }

    @JsonProperty("challenge_ts")
    public void setChallengeTs(String challengeTs) {
        this.challengeTs = challengeTs;
    }

    @JsonProperty("hostname")
    public String getHostname() {
        return hostname;
    }

    @JsonProperty("hostname")
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @JsonProperty("error-codes")
    public void setErrorCodes(ErrorCode[] errorCodes) {
        this.errorCodes = errorCodes;
    }

    @JsonProperty("error-codes")
    public ErrorCode[] getErrorCodes() {
        return errorCodes;
    }

    @JsonProperty("score")
    public float getScore() {
        return score;
    }

    @JsonProperty("score")
    public void setScore(float score) {
        this.score = score;
    }

    @JsonProperty("action")
    public String getAction() {
        return action;
    }

    @JsonProperty("action")
    public void setAction(String action) {
        this.action = action;
    }

    @JsonIgnore
    public boolean hasClientError() {
        final ErrorCode[] errors = getErrorCodes();
        if (errors == null) {
            return false;
        }
        for (final ErrorCode error : errors) {
            switch (error) {
            case InvalidResponse:
            case MissingResponse:
            case BadRequest:
                return true;
            default:
                break;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "GoogleResponse{" + "success=" + success + ", challengeTs='" + challengeTs + '\'' + ", hostname='" + hostname + '\''+ ", score='" + score + '\''+ ", action='" + action+ '\'' + ", errorCodes=" + Arrays.toString(errorCodes) + '}';
    }
}
