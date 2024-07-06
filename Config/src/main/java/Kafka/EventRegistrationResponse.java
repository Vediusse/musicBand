package Kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventRegistrationResponse {

    @JsonProperty("eventId")
    private Long eventId;

    @JsonProperty("bandId")
    private Long bandId;

    @JsonProperty("success")
    private boolean success;

    public EventRegistrationResponse() {
    }

    public String getCorrelationId() {
        return correlationId;
    }

    @JsonProperty("correlationId")
    private String correlationId;

    public EventRegistrationResponse(Long eventId, Long bandId, boolean success) {
        this.eventId = eventId;
        this.bandId = bandId;
        this.success = success;
    }

    public EventRegistrationResponse(Long eventId, Long bandId, boolean success, String correlationId) {
        this.eventId = eventId;
        this.bandId = bandId;
        this.success = success;
        this.correlationId = correlationId;
    }

    public EventRegistrationResponse(boolean success) {
        this.success = success;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getBandId() {
        return bandId;
    }

    public boolean isSuccess() {
        return success;
    }

    public byte[] toBytes() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(this);
    }

    public static EventRegistrationResponse fromBytes(byte[] bytes) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(bytes, EventRegistrationResponse.class);
    }

    @Override
    public String toString() {
        return "EventRegistrationResponse{" +
                "eventId=" + eventId +
                ", bandId=" + bandId +
                ", success=" + success +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}
