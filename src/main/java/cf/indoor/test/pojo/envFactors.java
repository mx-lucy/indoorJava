package cf.indoor.test.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "env_factor")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class envFactors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    int room_id;
    double height;
    double atten_factor;
    double p0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getAtten_factor() {
        return atten_factor;
    }

    public void setAtten_factor(double atten_factor) {
        this.atten_factor = atten_factor;
    }

    public double getP0() {
        return p0;
    }

    public void setP0(double p0) {
        this.p0 = p0;
    }
}
