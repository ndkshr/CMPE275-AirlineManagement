package com.nandakishor.cmpe275.lab2.passenger;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRepository extends CrudRepository<Passenger, String> {
    Optional<Passenger> findByPhone(String phone);
}
