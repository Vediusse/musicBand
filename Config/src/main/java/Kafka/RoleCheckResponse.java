package Kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RoleCheckResponse {

    @JsonProperty("correlationId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String correlationId;

    @JsonProperty("allowed")
    private boolean allowed;

    @JsonProperty("text")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String text;

    @JsonProperty("error")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

    public RoleCheckResponse() {}

    public RoleCheckResponse(boolean allowed) {
        this.allowed = allowed;
    }

    public RoleCheckResponse(String correlationId, boolean allowed) {
        this.allowed = allowed;
        this.correlationId = correlationId;
    }

    public RoleCheckResponse(String correlationId, boolean allowed, String text, String error) {
        this.correlationId = correlationId;
        this.allowed = allowed;
        this.text = text;
        this.error = error;
    }

    public RoleCheckResponse(String correlationId, boolean allowed, String error) {
        this.correlationId = correlationId;
        this.allowed = allowed;
        this.error = error;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getText() {
        return text;
    }

    public String getError() {
        return error;
    }

    public void setText(String text) {
        this.text = text;
    }
}
