
package com.nandakishor.cmpe275.lab2.utils;

import com.nandakishor.cmpe275.lab2.flight.Flight;

import java.util.List;

public class Helper {

    public static Boolean doesFlightTimeOverlap(Flight f1, Flight f2) {
        String d1 = f1.getDepartureTime();
        String r1 = f1.getArrivalTime();
        String d2 = f2.getDepartureTime();
        String r2 = f2.getArrivalTime();

        Boolean flight2StartInFlight1Time = d1.compareTo(d2) <= 0 && d2.compareTo(r1) <= 0;
        Boolean flight1StartInFlight2Time = d2.compareTo(d1) <= 0 && d1.compareTo(r2) <= 0;

        return flight1StartInFlight2Time || flight2StartInFlight1Time;
    }

    public static Boolean doesFlightOverlapWithOtherFlights(
            Flight flight,
            List<Flight> otherFlights
    ) {
        try {
            for(Flight otherFlight : otherFlights) {
                boolean isNotTheSameFlight = !flight.getFlightNumber().equals(otherFlight.getFlightNumber());
                boolean flightTimeOverlaps = doesFlightTimeOverlap(flight, otherFlight);
                if (isNotTheSameFlight && flightTimeOverlaps) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}

