package com.nandakishor.cmpe275.lab2.reservation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.nandakishor.cmpe275.lab2.flight.Flight;
import com.nandakishor.cmpe275.lab2.passenger.Passenger;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;

@Entity
@Component
@Table(name = "Reservation")
public class Reservation {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String reservationNumber; // primary key
    @OneToOne(targetEntity = Passenger.class, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({
            "birthyear",
            "gender",
            "phone",
            "reservations"
    })
    private Passenger passenger;     // Full form only
    private String origin;
    private String destination;
    private int price; // sum of each flight’s price.   // Full form only
    @ManyToMany(targetEntity = Flight.class, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({
            "passengers",
            "plane",
            "description",
            "price",
            "passenger"
    })
    @JacksonXmlElementWrapper(localName = "flights")
    @JacksonXmlProperty(localName = "flight")
    private List<Flight> flights;    // Full form only, CANNOT be empty, ordered chronologically by departureTime

    public Reservation() {}

    public Reservation(
            Passenger passenger,
            String origin,
            String destination,
            int price,
            List<Flight> flights
    ) {
        this.passenger = passenger;
        this.origin = origin;
        this.destination = destination;
        this.price = price;
        this.flights = flights;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}
