package com.nandakishor.cmpe275.lab2.flight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/flight")
public class FlightAPIController {
    @Autowired
    private FlightService flightService;

    @RequestMapping(
            value = "/{flightNumber}/{departureDate}",
            method = RequestMethod.GET,
            params = "xml",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> getFlightByNumber(
            @PathVariable("flightNumber") String flightNum,
            @PathVariable("departureDate") String departureDate,
            @RequestParam(value = "xml") String returnXML
    ) {
        MediaType mediaType = (returnXML.equalsIgnoreCase("true")) ?
                MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        return flightService.getFlight(flightNum, departureDate, mediaType);
    }

    @RequestMapping(
            value = "/{flightNumber}/{departureDate}",
            method = RequestMethod.POST,
            params = "xml",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> createOrUpdateFlight(
            @PathVariable("flightNumber") String flightNum,
            @PathVariable("departureDate") String departureDate,
            @RequestParam("price") int price,
            @RequestParam("origin") String origin,
            @RequestParam("destination") String destination,
            @RequestParam("departureTime") String departureTime,
            @RequestParam("arrivalTime") String arrivalTime,
            @RequestParam("description") String description,
            @RequestParam("capacity") int capacity,
            @RequestParam("model") String model,
            @RequestParam("manufacturer") String manufacturer,
            @RequestParam("yearOfManufacture") int yearOfManufacture,
            @RequestParam(value = "xml") String returnXML
    ) {
        MediaType mediaType = (returnXML.equalsIgnoreCase("true")) ?
                MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        return flightService.createOrUpdateFlight(
                flightNum, departureDate, price, origin, destination, departureTime, arrivalTime, capacity,
                description, model, manufacturer, yearOfManufacture, mediaType
        );
    }

    @RequestMapping(
            value="/{flightNumber}/{departureDate}",
            method=RequestMethod.DELETE,
            params="xml",
            produces={
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> deleteFlight(
            @PathVariable("flightNumber") String flightNum,
            @PathVariable("departureDate") String departureDate,
            @RequestParam (value="xml") String returnXML
    ) {
        MediaType mediaType = (returnXML.equalsIgnoreCase("true")) ?
                MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        return flightService.deleteFlight(flightNum, departureDate, mediaType);
    }
}
