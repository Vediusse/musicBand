package Entities.Bands;




import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Person implements Serializable {


    private String frontManName;

    private Integer frontManHeight;

    private Color eyeColor;

    private Color hairColor;

    private Country nationality;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    public String getName() {
        return frontManName;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.frontManName = name;
    }

    public Integer getHeight() {
        return frontManHeight;
    }

    public void setHeight(Integer height) {
        if (height != null && height <= 0) {
            throw new IllegalArgumentException("Height should be greater than 0");
        }
        this.frontManHeight = height;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(Country nationality) {
        if (nationality == null) {
            throw new IllegalArgumentException("Nationality cannot be null");
        }
        this.nationality = nationality;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}


