package com.nandakishor.cmpe275.lab2.flight;

import com.nandakishor.cmpe275.lab2.utils.BadResponse;
import com.nandakishor.cmpe275.lab2.utils.Response;
import com.nandakishor.cmpe275.lab2.utils.Helper;
import com.nandakishor.cmpe275.lab2.Plane;
import com.nandakishor.cmpe275.lab2.passenger.Passenger;
import com.nandakishor.cmpe275.lab2.reservation.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Transactional
    public ResponseEntity<?> getFlight(
            String flightNumber,
            String departureDate,
            MediaType mediaType
    ) {
        String flightId = flightNumber + Flight.SEP_CONST + departureDate;
        Optional<Flight> flight = flightRepository.findById(flightId);

        if (flight.isPresent()) {
            return ResponseEntity.ok().contentType(mediaType).body(flight.get());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(mediaType).body(
                new BadResponse(
                        HttpStatus.NOT_FOUND.value(),
                        String.format("Sorry, the requested flight with number %s does not exist", flightNumber)
                )
        );
    }

    @Transactional
    public ResponseEntity<?> createOrUpdateFlight(String flightNum, String departureDate, int price, String origin, String destination,
                                                  String departureTime1, String arrivalTime1, int capacity, String description, String model, String manufacturer,
                                                  int yearOfManufacture, MediaType mediaType) {
        Date arrivalTime, departureTime;
        String flightId = flightNum + Flight.SEP_CONST + departureDate;
        Optional<Flight> flight = flightRepository.findById(flightId);
        try {
            arrivalTime = new SimpleDateFormat("yyyy-MM-dd-HH").parse(arrivalTime1);
            departureTime = new SimpleDateFormat("yyyy-MM-dd-HH").parse(departureTime1);
        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(mediaType)
                    .body(
                            new BadResponse(
                                    HttpStatus.BAD_REQUEST.value(),
                                    "Sorry, arrival or departure time format is invalid. Please check again."
                            )
                    );
        }

        if (flight.isPresent())
            return updateFlight(
                    flightNum, departureDate, price, origin, destination,
                    departureTime, arrivalTime, capacity, description, model,
                    manufacturer, yearOfManufacture, mediaType
            );

        else
            return createFlight(
                    flightNum, price, origin, destination, departureTime,
                    arrivalTime, capacity, description, model,
                    manufacturer, yearOfManufacture, mediaType
            );
    }

    @Transactional
    public ResponseEntity<?> createFlight(
            String flightNum,
            int price,
            String origin,
            String destination,
            Date departureTime,
            Date arrivalTime,
            int capacity,
            String description,
            String model,
            String manufacturer,
            int yearOfManufacture,
            MediaType mediaType
    ) {
        Plane plane = new Plane(model, capacity, manufacturer, yearOfManufacture);
        Flight flight = new Flight(flightNum, departureTime, arrivalTime, price, origin, destination, description, plane, capacity);
        flightRepository.save(flight);
        return ResponseEntity.ok().contentType(mediaType).body(flight);
    }

    @Transactional
    public ResponseEntity<?> updateFlight(
            String flightNum,
            String departureDate,
            int price,
            String origin,
            String destination,
            Date departureTime,
            Date arrivalTime,
            int capacity,
            String description,
            String model,
            String manufacturer,
            int yearOfManufacture,
            MediaType mediaType
    ) {
        String flightId = flightNum + Flight.SEP_CONST + departureDate;
        Optional<Flight> flightOptional = flightRepository.findById(flightId);
        Flight flight = null;
        if (flightOptional.isPresent())
                flight = flightOptional.get();
        else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(mediaType).body(
                        new BadResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Sorry, the flight does not exist"
                        )
                );


        Plane plane = flight.getPlane();
        List<Passenger> passengers = flight.getPassengers();
        BadResponse badResponse = null;
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        if (capacity < passengers.size()) {
            httpStatus = HttpStatus.BAD_REQUEST;
            badResponse = new BadResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Sorry, we have already reserved seats more than this capacity"
            );
        }

        if (badResponse == null) {
            for (Passenger passenger : passengers) {
                List<Flight> passengerFlights = new ArrayList<>();

                for (Reservation reservation : passenger.getReservations()) {
                    passengerFlights.addAll(reservation.getFlights());
                }

                if (Helper.doesFlightOverlapWithOtherFlights(flight, passengerFlights)) {
                    httpStatus = HttpStatus.BAD_REQUEST;
                    badResponse = new BadResponse(
                            HttpStatus.BAD_REQUEST.value(),
                            "Sorry, this departure and arrival time overlaps with one of the flights of some passenger"
                    );

                    break;
                }
            }
        }

        if (badResponse != null) {
            return ResponseEntity.status(httpStatus).contentType(mediaType).body(badResponse);
        }

        int newNumOfSeatsLeft = capacity - passengers.size();
        flight.setSeatsLeft(newNumOfSeatsLeft);
        flight.setPrice(price);
        flight.setOrigin(origin);
        flight.setDestination(destination);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setDescription(description);
        plane.setCapacity(capacity);
        plane.setModel(model);
        plane.setManufacturer(manufacturer);
        plane.setYearOfManufacture(yearOfManufacture);
        flight.setPlane(plane);

        flightRepository.save(flight);

        return ResponseEntity.ok().contentType(mediaType).body(flight);
    }

    @Transactional
    public ResponseEntity<?> deleteFlight(String flightNum, String departureDate, MediaType mediaType) {
        String flightId = flightNum + Flight.SEP_CONST + departureDate;
        Optional<Flight> flightOptional = flightRepository.findById(flightId);

        BadResponse badResponse = null;

        if (flightOptional.isEmpty()) {
            badResponse = new BadResponse(
                    HttpStatus.NOT_FOUND.value(),
                    String.format("Sorry, Flight with number %s is not present", flightNum)
            );
        }

        if (badResponse == null) {
            Flight flight = flightOptional.get();
            List<Passenger> passengers = flight.getPassengers();

            if (passengers.size() > 0) {
                badResponse = new BadResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        String.format("Sorry, Flight with number %s cannot be deleted as it has one or more reservations", flightNum)
                );
            }
        }

        if (badResponse != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(mediaType).body(badResponse);
        }

        Flight flight = flightOptional.get();
        flightRepository.delete(flight);

        return ResponseEntity.ok().contentType(mediaType)
                .body(
                        new Response(
                            HttpStatus.OK.value(),
                            "Flight with number " + flightNum + " is deleted successfully"
                        )
                );
    }
}
