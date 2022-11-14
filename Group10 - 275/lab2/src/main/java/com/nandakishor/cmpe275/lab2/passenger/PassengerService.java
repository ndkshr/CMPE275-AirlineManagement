package com.nandakishor.cmpe275.lab2.passenger;

import com.nandakishor.cmpe275.lab2.utils.BadResponse;
import com.nandakishor.cmpe275.lab2.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
public class PassengerService {

    public PassengerService() {}

    @Autowired
    public PassengerRepository passengerRepository;


    @Transactional
    public ResponseEntity<?> getPassengerById(String id, MediaType mediaType) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if (passenger.isPresent()) {
            return ResponseEntity.ok().contentType(mediaType)
                    .body(
                        passenger.get()
                    );
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(mediaType)
                .body(
                        new BadResponse(
                                HttpStatus.NOT_FOUND.value(),
                                String.format("Sorry, the requested passenger with ID %s does not exist", id)
                        )
                );
    }

    @Transactional
    public ResponseEntity<?> createPassenger(
            String firstName,
            String lastName,
            int birthyear,
            String gender,
            String phone,
            MediaType mediaType
    ) {

        Optional<Passenger> passenger = passengerRepository.findByPhone(phone);

        if (passenger.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(mediaType)
                    .body(
                            new BadResponse(
                                    HttpStatus.BAD_REQUEST.value(),
                                    "Another passenger with the same number already exists."
                            )
                    );
        }

//        Passenger newPassenger = new Passenger(firstName, lastName, birthyear, gender, phone, null);
        Passenger newPassenger = new Passenger(firstName, lastName, birthyear, gender, phone);
        passengerRepository.save(newPassenger);
        return ResponseEntity.ok().contentType(mediaType).body(newPassenger);
    }

    @Transactional
    public ResponseEntity<?> updatePassenger(
            String id,
            String firstName,
            String lastName,
            int birthyear,
            String gender,
            String phone,
            MediaType mediaType
    ) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        Optional<Passenger> passengerWithSamePhone = passengerRepository.findByPhone(phone);

        if (passenger.isPresent() && passengerWithSamePhone.isPresent() && !passengerWithSamePhone.get().hasSameId(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(mediaType)
                    .body(
                            new BadResponse(
                                    HttpStatus.BAD_REQUEST.value(),
                                    "Sorry, can not update passenger. There was a conflict between the new passenger and existing passenger phone number."
                            )
                    );
        }

        if (passenger.isPresent()) {
            Passenger myPassenger = passenger.get();
            myPassenger.setFirstname(firstName);
            myPassenger.setLastname(lastName);
            myPassenger.setBirthyear(birthyear);
            myPassenger.setGender(gender);
            myPassenger.setPhone(phone);
            passengerRepository.save(myPassenger);

            return ResponseEntity.ok().contentType(mediaType).body(myPassenger);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(mediaType)
                .body(
                        new BadResponse(
                                HttpStatus.NOT_FOUND.value(),
                                String.format("Sorry, the requested passenger with ID %s does not exist in the system.", id)
                        )
                );
    }

    @Transactional
    public ResponseEntity<?> deletePassenger(String id, MediaType mediaType) {
        Optional<Passenger> passenger = passengerRepository.findById(id);
        if (passenger.isPresent()) {
            Passenger pass = passenger.get();
            passengerRepository.delete(pass);
            return ResponseEntity.ok().contentType(mediaType)
                    .body(
                            new Response(
                                    HttpStatus.OK.value(),
                                    "Passenger with id " + id + " is deleted successfully."
                            )
            );

        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(mediaType).body(
                new BadResponse(
                        HttpStatus.NOT_FOUND.value(),
                        "Sorry, passenger with id " + id + " does not exist."
                )
        );
    }
}
