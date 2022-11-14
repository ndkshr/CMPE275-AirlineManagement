package com.nandakishor.cmpe275.lab2;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlRootElement;

@Embeddable
@XmlRootElement
public class Plane { // Embedded
    private String model;
    private int capacity;
    private String manufacturer;
    private int yearOfManufacture;

    public Plane() {}

    public Plane(
            String model,
            int capacity,
            String manufacturer,
            int yearOfManufacture
    ) {
        this.model = model;
        this.capacity = capacity;
        this.manufacturer = manufacturer;
        this.yearOfManufacture = yearOfManufacture;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(int yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }
}
