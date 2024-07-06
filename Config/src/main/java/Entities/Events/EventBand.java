package Entities.Events;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(EventBandId.class)
public class EventBand implements Serializable {

    @Id
    @Column(name = "event_id")
    private Long eventId;

    @Id
    @Column(name = "band_id")
    private Long bandId;

    public EventBand() {
    }

    public EventBand(Long eventId, Long bandId) {
        this.eventId = eventId;
        this.bandId = bandId;
    }

    // Геттеры и сеттеры для eventId и bandId
}
