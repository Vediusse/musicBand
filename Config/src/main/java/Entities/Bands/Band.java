package Entities.Bands;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
public class Band {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    private LocalDateTime creationDate;

    @NotEmpty(message = "Number of participants cannot be empty")
    private Integer numberOfParticipants;

    @NotEmpty(message = "Number of singles cannot be empty")
    private long singlesCount;

    private ZonedDateTime establishmentDate;

    @NotEmpty(message = "Genre cannot be empty")
    @Enumerated(EnumType.STRING)
    private MusicGenre genre;

    @OneToOne
    private Person frontMan;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("ID should be greater than 0");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        if (creationDate == null) {
            throw new IllegalArgumentException("Creation date cannot be null");
        }
        this.creationDate = creationDate;
    }

    public Integer getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(Integer numberOfParticipants) {
        if (numberOfParticipants != null && numberOfParticipants <= 0) {
            throw new IllegalArgumentException("Number of participants should be greater than 0");
        }
        this.numberOfParticipants = numberOfParticipants;
    }

    public long getSinglesCount() {
        return singlesCount;
    }

    public void setSinglesCount(long singlesCount) {
        if (singlesCount <= 0) {
            throw new IllegalArgumentException("Singles count should be greater than 0");
        }
        this.singlesCount = singlesCount;
    }

    public ZonedDateTime getEstablishmentDate() {
        return establishmentDate;
    }

    public void setEstablishmentDate(ZonedDateTime establishmentDate) {
        if (establishmentDate == null) {
            throw new IllegalArgumentException("Establishment date cannot be null");
        }
        this.establishmentDate = establishmentDate;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public void setGenre(MusicGenre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Genre cannot be null");
        }
        this.genre = genre;
    }

    public Person getFrontMan() {
        return frontMan;
    }

    public void setFrontMan(Person frontMan) {
        this.frontMan = frontMan;
    }

    // Other methods omitted for brevity
}
