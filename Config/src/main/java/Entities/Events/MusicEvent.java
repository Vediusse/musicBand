package Entities.Events;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;


@Entity
public class MusicEvent {

    @NotEmpty(message = "Number of singles cannot be empty")
    private String name;

    @NotEmpty(message = "Number of singles cannot be empty")
    private ZonedDateTime time;

    @Min(value = 0, message = "Visitors must be more than 0")
    @NotEmpty(message = "Number of singles cannot be empty")
    private int visitors;


    public int getBandsCount() {
        return bandsCount;
    }

    public void setBandsCount(int bands) {
        this.bandsCount = bands;
    }


    @NotEmpty(message = "Bands  cannot be empty")
    @JsonIgnore
    private int bandsCount = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0, message = "Capability must be more than 0")
    @NotEmpty(message = "Capability cannot be empty")
    private int capability;


    public String getName() {
        return name;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public int getVisitors() {
        return visitors;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getCapability() {
        return capability;
    }
}
