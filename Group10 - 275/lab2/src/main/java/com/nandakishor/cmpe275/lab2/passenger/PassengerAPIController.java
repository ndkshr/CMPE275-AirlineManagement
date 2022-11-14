package com.nandakishor.cmpe275.lab2.passenger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/passenger")
public class PassengerAPIController {

    public PassengerAPIController() {}

    @Autowired
    private PassengerService passengerService;

    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET,
            params = "xml",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> getAPassengerWithId(
            @PathVariable("id") String id,
            @RequestParam(value = "xml") String returnXML
    ) {
        MediaType mediaType = (returnXML.equalsIgnoreCase("true")) ?
                MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        return passengerService.getPassengerById(id, mediaType);
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.POST,
            params = "xml",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> createPassenger(
            @RequestParam("firstname") String firstName,
            @RequestParam("lastname") String lastName,
            @RequestParam("birthyear") int birthyear,
            @RequestParam("gender") String gender,
            @RequestParam("phone") String phone,
            @RequestParam("xml") String returnXML
    ) {
        MediaType mediaType = (returnXML.equalsIgnoreCase("true")) ?
                MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        return passengerService.createPassenger(firstName, lastName, birthyear, gender, phone, mediaType);
    }

    @RequestMapping(
            value = "{id}",
            method = RequestMethod.PUT,
            params = "xml",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> updatePassenger(
            @PathVariable("id") String id,
            @RequestParam("firstname") String firstName,
            @RequestParam("lastname") String lastName,
            @RequestParam("birthyear") int birthyear,
            @RequestParam("gender") String gender,
            @RequestParam("phone") String phone,
            @RequestParam("xml") String returnXML
    ) {
        MediaType mediaType = (returnXML.equalsIgnoreCase("true")) ?
                MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        return passengerService.updatePassenger(id, firstName, lastName, birthyear, gender, phone, mediaType);
    }

    @RequestMapping(
            value = "{id}",
            method = RequestMethod.DELETE,
            params = "xml",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE
            }
    )
    public ResponseEntity<?> deletePassenger(
            @PathVariable("id") String id,
            @RequestParam("xml") String returnXML
    ) {
        MediaType mediaType = (returnXML.equalsIgnoreCase("true")) ?
                MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        return passengerService.deletePassenger(id, mediaType);
    }
}
