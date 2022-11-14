package com.nandakishor.cmpe275.lab2.flight;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Embeddable
public class FlightId implements Serializable {
    private String flightNumber; // part of the primary key

    /*  Date format: yy-mm-dd, do not include hours, minutes, or seconds.
     ** Example: 2022-03-22
     **The system only needs to support PST. You can ignore other time zones.
     */
    private String departureDate;

    public FlightId() {}

    public FlightId(String flightNumber, String departureDate) {
        this.flightNumber = flightNumber;
        this.departureDate = departureDate;
    }

    public FlightId(String flightNumber, Date departureDate) {
        this.flightNumber = flightNumber;
        this.departureDate =  new SimpleDateFormat("yyyy-MM-dd").format(departureDate);
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }
}
