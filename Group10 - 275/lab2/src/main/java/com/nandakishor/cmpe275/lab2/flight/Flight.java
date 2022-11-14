package com.nandakishor.cmpe275.lab2.flight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.nandakishor.cmpe275.lab2.Plane;
import com.nandakishor.cmpe275.lab2.passenger.Passenger;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
@Component
@Table(name = "Flight")
public class Flight {

    public static final String SEP_CONST = "<<::>>";
    @Id
    private String flightId;

    @JsonIgnore
    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    private String flightNumber; // part of the primary key

    /*  Date format: yy-mm-dd, do not include hours, minutes, or seconds.
     ** Example: 2022-03-22
     **The system only needs to support PST. You can ignore other time zones.
     */
    private String departureDate; //  serve as the primary key together with flightNumber

    /*  Date format: yy-mm-dd-hh, do not include minutes or seconds.
     ** Example: 2017-03-22-19
     */
    private Date departureTime; // Must be within the same calendar day as departureDate.
    private Date arrivalTime;
    private int price;    // Full form only
    private String origin;
    private String destination;
    private int seatsLeft;
    private String description;   // Full form only
    @Embedded
    private Plane plane;  // Embedded,    Full form only
    @ManyToMany(targetEntity = Passenger.class, fetch = FetchType.LAZY)
    @JacksonXmlElementWrapper(localName = "passengers")
    @JacksonXmlProperty(localName = "passenger")
    @JsonIgnoreProperties({
            "flight",
            "reservation",
            "flights",
            "reservations"
    })
    private List<Passenger> passengers;    // Full form only

    public Flight() {}

    public Flight(
            String flightNumber,
            Date departureTime,
            Date arrivalTime,
            int price,
            String origin,
            String destination,
            String description,
            Plane plane,
            int seatsLeft
    ) {
        this.flightNumber = flightNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.departureDate = new SimpleDateFormat("yyyy-MM-dd").format(departureTime);
        this.price = price;
        this.origin = origin;
        this.destination = destination;
        this.description = description;
        this.plane = plane;
        this.seatsLeft = seatsLeft;
        this.flightId = this.flightNumber + SEP_CONST + this.departureDate;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    @JsonIgnore
    public String getDepartureDateAsDate() {
        return departureDate;
    }

    public String getDepartureDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(departureDate);
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    @JsonIgnore
    public Date getDepartureTimeAsDate() {
        return departureTime;
    }

    public String getDepartureTime() {
        return new SimpleDateFormat("yyyy-MM-dd-HH").format(departureTime);
    }


    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    @JsonIgnore
    public Date getArrivalTimeAsDate() {
        return arrivalTime;
    }

    public String getArrivalTime() {
        return new SimpleDateFormat("yyyy-MM-dd-HH").format(arrivalTime);
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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

    public int getSeatsLeft() {
        return seatsLeft;
    }

    public void setSeatsLeft(int seatsLeft) {
        this.seatsLeft = seatsLeft;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }
}
