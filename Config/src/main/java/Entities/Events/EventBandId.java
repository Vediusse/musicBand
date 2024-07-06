package Entities.Events;

import java.io.Serializable;
import java.util.Objects;

public class EventBandId implements Serializable {

    private Long eventId;
    private Long bandId;

    public EventBandId() {
    }

    public EventBandId(Long eventId, Long bandId) {
        this.eventId = eventId;
        this.bandId = bandId;
    }

    // Геттеры и сеттеры для eventId и bandId

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventBandId that = (EventBandId) o;
        return Objects.equals(eventId, that.eventId) && Objects.equals(bandId, that.bandId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, bandId);
    }
}
