package com.nandakishor.cmpe275.lab2.passenger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nandakishor.cmpe275.lab2.reservation.Reservation;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;

@Entity
public class Passenger {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name= "system-uuid", strategy = "uuid")
    private String id;   // primary key
    private String firstname;
    private String lastname;
    private int birthyear;  // Full form only (see definition below)
    private String gender;  // Full form only
    @Column(unique = true)
    private String phone; // Phone numbers must be unique.   Full form only

    @OneToMany(targetEntity = Reservation.class, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({
            "passenger",
            "flights",
            "price",
            "flight"
    })
    private List<Reservation> reservations;   // Full form only

    public Passenger() {}

    public Passenger(
            String firstname,
            String lastname,
            int birthyear,
            String gender,
            String phone
    ) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthyear = birthyear;
        this.gender = gender;
        this.phone = phone;
    }

    public Passenger(
            String firstname,
            String lastname,
            int birthyear,
            String gender,
            String phone,
            List<Reservation> reservations
    ) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthyear = birthyear;
        this.gender = gender;
        this.phone = phone;
        this.reservations = reservations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getBirthyear() {
        return birthyear;
    }

    public void setBirthyear(int birthyear) {
        this.birthyear = birthyear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public boolean hasSameId(String otherId) {
        return this.id.equals(otherId);
    }
}

