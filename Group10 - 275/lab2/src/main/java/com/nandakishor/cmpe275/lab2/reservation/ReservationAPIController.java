package com.nandakishor.cmpe275.lab2.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "reservation")
public class ReservationAPIController {

    @Autowired
    private ReservationService reservationService;

    @RequestMapping(
            value = "{number}",
            method = RequestMethod.GET,
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> getReservation(
            @PathVariable("number") String number,
            @RequestParam(
                    value = "xml",
                    required = false,
                    defaultValue = "false"
            ) String returnXML
    ) {
        MediaType mediaType = (returnXML.equalsIgnoreCase("true")) ?
                MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        return reservationService.getReservationById(number, mediaType);
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.PUT,
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> createReservation(
            @RequestParam("passengerId") String passengerId,
            @RequestParam("flightNumbers") List<String> flightNumbers,
            @RequestParam("departureDates") List<String> depDates,
            @RequestParam(
                    value = "xml",
                    required = false,
                    defaultValue = "false"
            ) String returnXML
    ) {
        MediaType mediaType = (returnXML.equalsIgnoreCase("true")) ?
                MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        return reservationService.makeReservation(passengerId, flightNumbers, depDates, mediaType);
    }

    @RequestMapping(
            value = "{number}",
            method = RequestMethod.POST,
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> updateReservation(
            @PathVariable("number") String number, // Reservation Number
            @RequestParam(value = "flightsAdded", required = false) String flightsAdded,
            @RequestParam(value = "flightsRemoved", required = false) String flightsRemoved,
            @RequestParam(
                    value = "xml",
                    required = false,
                    defaultValue = "false"
            ) String returnXML
    ) {
        MediaType mediaType = (returnXML.equalsIgnoreCase("true")) ?
                MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        return reservationService.updateReservation(number, flightsAdded, flightsRemoved, mediaType);
    }

    @RequestMapping(
            value = "{number}",
            method = RequestMethod.DELETE,
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> cancelReservation(
            @PathVariable("number") String number,
            @RequestParam(
                    value = "xml",
                    required = false,
                    defaultValue = "false"
            ) String returnXML
    ) {
        MediaType mediaType = (returnXML.equalsIgnoreCase("true")) ?
                MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        return reservationService.cancelReservation(number, mediaType);
    }
}
