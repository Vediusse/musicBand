package Kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BandRegisteredEvent implements Serializable {

    @JsonProperty("bandId")
    private Long bandId;

    @JsonProperty("eventId")
    private Long eventId;

    public BandRegisteredEvent(Long bandId, Long eventId, String correlationId) {
        this.bandId = bandId;
        this.eventId = eventId;
        this.correlationId = correlationId;
    }


    public String getCorrelationId() {
        return correlationId;
    }

    @JsonProperty("correlationId")
    private String correlationId;

    public BandRegisteredEvent() {
    }

    public BandRegisteredEvent(Long bandId, Long eventId) {
        this.bandId = bandId;
        this.eventId = eventId;
    }

    public Long getBandId() {
        return bandId;
    }

    public void setBandId(Long bandId) {
        this.bandId = bandId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    // Add module information
    public static void main(String[] args) {
        System.out.println(BandRegisteredEvent.class.getModule().getName());
    }
}
