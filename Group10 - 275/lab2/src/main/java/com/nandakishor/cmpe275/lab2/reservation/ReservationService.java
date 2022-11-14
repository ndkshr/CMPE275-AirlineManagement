package com.nandakishor.cmpe275.lab2.reservation;

import com.nandakishor.cmpe275.lab2.flight.Flight;
import com.nandakishor.cmpe275.lab2.flight.FlightRepository;
import com.nandakishor.cmpe275.lab2.passenger.Passenger;
import com.nandakishor.cmpe275.lab2.passenger.PassengerRepository;
import com.nandakishor.cmpe275.lab2.utils.BadResponse;
import com.nandakishor.cmpe275.lab2.utils.Response;
import com.nandakishor.cmpe275.lab2.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Transactional
    public ResponseEntity<?> getReservationById(String reservationNumber, MediaType mediaType) {
        Optional<Reservation> reservation = reservationRepository.findById(reservationNumber);
        if (reservation.isPresent()) {
            Reservation myReservation = reservation.get();
            List<Flight> myFlights = myReservation.getFlights();
            myFlights.sort(Comparator.comparing(Flight::getDepartureTimeAsDate));
            myReservation.setFlights(myFlights);
            return ResponseEntity.ok().contentType(mediaType).body(myReservation);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(mediaType)
                .body(
                        new BadResponse(
                                HttpStatus.NO_CONTENT.value(),
                                String.format("Reservation with number %s does not exist", reservationNumber)
                        )
                );
    }

    @Transactional
    public ResponseEntity<?> makeReservation(
            String passengerId,
            List<String> flights,
            List<String> departureDates,
            MediaType mediaType
    ) {
        if (flights.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(mediaType)
                    .body(
                            new BadResponse(
                                    HttpStatus.BAD_REQUEST.value(),
                                    "Sorry, flight list is empty for this reservation."
                            )
                    );
        }

        if (departureDates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(mediaType)
                    .body(
                            new BadResponse(
                                    HttpStatus.BAD_REQUEST.value(),
                                    "Sorry, departure date list is empty for this reservation."
                            )
                    );
        }

        if (flights.size() != departureDates.size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(mediaType)
                    .body(
                            new BadResponse(
                                    HttpStatus.BAD_REQUEST.value(),
                                    "Sorry, flight list and departure date sizes do not match."
                            )
                    );
        }

        Optional<Passenger> passenger = passengerRepository.findById(passengerId);

        List<Flight> myFlights = new ArrayList<Flight>();

        for (int i = 0; i < flights.size(); i++) {
            String flightId = flights.get(i) + Flight.SEP_CONST + departureDates.get(i);
            Optional<Flight> flt = flightRepository.findById(flightId);
            if (flt.isPresent()) {
                myFlights.add(flt.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(mediaType)
                        .body(
                                new BadResponse(
                                        HttpStatus.BAD_REQUEST.value(),
                                        String.format("Sorry, Flight %s is not found", flights.get(i))
                                )
                        );
            }
        }

        myFlights.sort(Comparator.comparing(Flight::getDepartureTimeAsDate));

        if (passenger.isPresent()) {
            ResponseEntity<?> response = validate(myFlights, passenger.get(), mediaType);
            if (response.getStatusCodeValue() == HttpStatus.BAD_REQUEST.value()) return response;

            int cost = calculateTotalCost(myFlights, passenger.get());
            Reservation reservation = new Reservation(
                    passenger.get(), myFlights.get(0).getOrigin(), myFlights.get(myFlights.size() - 1).getDestination(),
                    cost, myFlights
            );
            passenger.get().getReservations().add(reservation);
            passengerRepository.save(passenger.get());
            return ResponseEntity.ok().contentType(mediaType).body(reservation);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(mediaType)
                .body(
                        new BadResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                String.format("Sorry, passenger with ID %s does not exist", passengerId)
                        )
                );
    }

    private ResponseEntity<?> validate(
            List<Flight> toBeReserved,
            Passenger passenger,
            MediaType responseIn
    ) {
        List<String> possibleOverlaps = overlapBetweenFlights(toBeReserved);

        if (possibleOverlaps.size() > 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(responseIn)
                    .body(
                            new BadResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Time overlap is not allowed between flights. Flight: "
                                + possibleOverlaps.get(0) + " overlaps with Flight: " + possibleOverlaps.get(1) + "."
                            )
                    );

        possibleOverlaps = overlapInReservedFlights(passenger.getReservations(), toBeReserved);
        if (possibleOverlaps.size() > 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(responseIn)
                    .body(
                            new BadResponse(
                                    HttpStatus.BAD_REQUEST.value(),
                            "Time overlap is not allowed between flights of same/different reservation(s) for a passenger. Flight: "
                                    + possibleOverlaps.get(0) + " overlaps with Flight: " + possibleOverlaps.get(1) + "."
                            )
                    );

        if (checkFlightSeats(toBeReserved) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(responseIn)
                    .body(
                            new BadResponse(
                                    HttpStatus.BAD_REQUEST.value(),
                                    "The total number of passengers cannot exceed the capacity of the plane. Flight: "
                                    + checkFlightSeats(toBeReserved) + " is full."
                            )
                    );

        return ResponseEntity.ok().contentType(responseIn).body("Validation Successful");
    }

    private String checkFlightSeats(List<Flight> toBeReserved) {
        for (Flight flight : toBeReserved) {
            if (flight.getSeatsLeft() == 0)
                return flight.getFlightNumber();
        }
        return null;
    }

    private List<String> overlapBetweenFlights(List<Flight> toBeReserved) {
        // toBeReserved.sort(Comparator.comparing(Flight::getDepartureTime));
        for (int i = 1; i < toBeReserved.size(); i++) {
            if (Helper.doesFlightTimeOverlap(toBeReserved.get(i), toBeReserved.get(i - 1)))
                return Arrays.asList(toBeReserved.get(i - 1).getFlightNumber(), toBeReserved.get(i).getFlightNumber());
        }
        return new ArrayList<>();
    }

    private List<String> overlapInReservedFlights(List<Reservation> reservations, List<Flight> toBeReserved) {
        List<Flight> reservedFlights = new ArrayList<>();
        for (Reservation reservation : reservations) {
            reservedFlights.addAll(reservation.getFlights());
        }
        // reservedFlights.sort(Comparator.comparing(Flight::getDepartureTime));
        for (Flight flight : toBeReserved) {
            for (Flight psF : reservedFlights) {
                if (Helper.doesFlightTimeOverlap(flight, psF))
                    return Arrays.asList(flight.getFlightNumber(), psF.getFlightNumber());
            }
        }
        return new ArrayList<>();
    }

    private int calculateTotalCost(List<Flight> toBeReserved, Passenger passenger) {
        int totalCost = 0;
        for (Flight flight : toBeReserved) {
            totalCost += flight.getPrice();
            flight.setSeatsLeft(flight.getSeatsLeft() - 1);
            flight.getPassengers().add(passenger);
            flightRepository.save(flight);
        }
        return totalCost;
    }

    private Flight getFlight(String id) {
        Optional<Flight> flight = flightRepository.findById(id);
        return flight.orElse(null);
    }

    @Transactional
    public ResponseEntity<?> updateReservation(
            String reservationNumber,
            String newFlights,
            String removedFlights,
            MediaType mediaType
    ) {
        List<String> flightsAdded = null;
        List<String> flightsRemoved = null;

        if (newFlights != null) {
            flightsAdded = new ArrayList<>(List.of(newFlights.split(",")));
            if (flightsAdded.get(0).equals(""))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(mediaType)
                        .body(
                                new BadResponse(
                                        HttpStatus.BAD_REQUEST.value(),
                                        "flightsAdded cannot be empty when present."
                                )
                        );
        }

        if (removedFlights != null) {
            flightsRemoved = new ArrayList<>(List.of(removedFlights.split(",")));
            if (flightsRemoved.get(0).equals(""))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(mediaType)
                        .body(
                                new BadResponse(
                                        HttpStatus.BAD_REQUEST.value(),
                                        "List of Flights to be removed cannot be empty when reservation exists."
                                )
                        );
        }

        if(flightsAdded!=null && flightsRemoved!=null) {
            Set<String> result = flightsAdded.stream()
                    .distinct()
                    .filter(flightsRemoved::contains)
                    .collect(Collectors.toSet());
            if(!result.isEmpty()) {
                List<String> temp = new ArrayList<>(result);
                flightsAdded.removeAll(temp);
                flightsRemoved.removeAll(temp);
            }
        }

        Optional<Reservation> reservation = reservationRepository.findById(reservationNumber);
        if (reservation.isPresent()) {
            Reservation r = reservation.get();
            Passenger passenger = r.getPassenger();
            List<Flight> fl = new ArrayList<>(r.getFlights());
            int totalCost = 0;
            if (flightsRemoved != null) {
                for (String flightNo : flightsRemoved) {
                    Flight flight = getFlight(flightNo.trim());
                    if (flight != null) {
                        fl.remove(flight);
                        if (flight.getPassengers().contains(passenger)) {
                            flight.setSeatsLeft(flight.getSeatsLeft() + 1);
                            flight.getPassengers().remove(passenger);
                            flightRepository.save(flight);
                            totalCost += flight.getPrice();
                        }
                    }
                    else
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(mediaType)
                                .body(
                                        new BadResponse(
                                                HttpStatus.BAD_REQUEST.value(),
                                                String.format("Sorry, Flight %s is does not exist", flightNo)
                                        )
                                );

                }
                r.setFlights(fl);
                r.setPrice(r.getPrice() - totalCost);
            }

            if (flightsAdded != null) {
                List<Flight> toBeAdded = new ArrayList<>();
                for (String flightNo : flightsAdded) {
                    Flight f = getFlight(flightNo.trim());
                    if (f != null)
                        toBeAdded.add(f);
                    else
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(mediaType)
                                .body(
                                        new BadResponse(
                                                HttpStatus.BAD_REQUEST.value(),
                                                String.format("Sorry, Flight %s is does not exist", flightNo)
                                        )
                                );

                }
                ResponseEntity<?> response = validate(toBeAdded, passenger, mediaType);
                if (response.getStatusCodeValue() == 400)
                    return response;

                totalCost = r.getPrice() + calculateTotalCost(toBeAdded, passenger);
                r.setPrice(totalCost);
                fl.addAll(toBeAdded);
            }

            if (fl.size() > 0) {
                fl.sort(Comparator.comparing(Flight::getDepartureTimeAsDate));
                r.setOrigin(fl.get(0).getOrigin());
                r.setDestination(fl.get(fl.size() - 1).getDestination());
                r.setFlights(fl);
                reservationRepository.save(r);
                return ResponseEntity.ok().contentType(mediaType).body(r);

            }

            cancelReservation(reservationNumber, mediaType);

            return ResponseEntity.ok().contentType(mediaType)
                    .body(
                            new Response(
                                    HttpStatus.OK.value(),
                                    String.format("Reservation No: %s has no flights. Removing this reservation.", reservationNumber)
                            )
                    );

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(mediaType)
                .body(
                        new BadResponse(
                                HttpStatus.NOT_FOUND.value(),
                                String.format("Reservation with number: %s does not exists.", reservationNumber)
                        )
                );
    }

    @Transactional
    public ResponseEntity<?> cancelReservation(String reservationNumber, MediaType mediaType) {
        Optional<Reservation> reservation = reservationRepository.findById(reservationNumber);
        if (reservation.isPresent()) {
            Reservation res = reservation.get();
            List<Flight> flights = res.getFlights();
            for (Flight flight: flights) {
                flight.getPassengers().remove(res.getPassenger());
                flight.setSeatsLeft(flight.getSeatsLeft() - 1);
            }
            Passenger passenger = res.getPassenger();
            passenger.getReservations().remove(res);
            passengerRepository.save(passenger);
            flightRepository.saveAll(flights);
            reservationRepository.deleteById(reservationNumber);
            return ResponseEntity.status(HttpStatus.OK).contentType(mediaType)
                    .body(
                            new Response(
                                    HttpStatus.OK.value(),
                                    String.format("Reservation with number %s is cancelled successfully.", reservationNumber)
                            )
                    );
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(mediaType)
                .body(
                        new BadResponse(
                                HttpStatus.NOT_FOUND.value(),
                                String.format("Reservation with number %s does not exist", reservationNumber)
                        )
                );
    }


}
