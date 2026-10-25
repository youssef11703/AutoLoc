package tn.esprit.autoloc.autolocapi.service;

import tn.esprit.autoloc.autolocapi.domain.Vehicle;

import java.util.List;

public interface IVehicleService {
    Vehicle create(Vehicle vehicle);
    Vehicle findById(Long id);
    List<Vehicle> findAll();
    Vehicle update(Long id, Vehicle vehicle);
    void deleteById(Long id);
}