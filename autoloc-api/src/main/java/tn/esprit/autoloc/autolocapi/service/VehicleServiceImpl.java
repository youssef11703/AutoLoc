package tn.esprit.autoloc.autolocapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.autolocapi.domain.Vehicle;
import tn.esprit.autoloc.autolocapi.exception.ResourceNotFoundException;
import tn.esprit.autoloc.autolocapi.repository.IVehicleRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements IVehicleService {

    private final IVehicleRepository vehicleRepository;

    @Override
    public Vehicle create(Vehicle vehicle) {
        if (vehicle.getIdVehicle() != null) {
            throw new IllegalArgumentException("Un nouveau véhicule ne doit pas avoir d'identifiant");
        }
        validateVehicle(vehicle);
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public Vehicle update(Long id, Vehicle vehicle) {
        Vehicle existant = findById(id);
        validateVehicle(vehicle);
        existant.setImmatriculation(vehicle.getImmatriculation());
        existant.setMarque(vehicle.getMarque());
        existant.setModele(vehicle.getModele());
        existant.setCategorie(vehicle.getCategorie());
        existant.setTarifJournalier(vehicle.getTarifJournalier());
        existant.setStatut(vehicle.getStatut());
        return vehicleRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle", id);
        }
        vehicleRepository.deleteById(id);
    }

    private void validateVehicle(Vehicle vehicle) {
        if (vehicle.getTarifJournalier() == null
                || vehicle.getTarifJournalier().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le tarif journalier ne peut pas être négatif");
        }
    }
}