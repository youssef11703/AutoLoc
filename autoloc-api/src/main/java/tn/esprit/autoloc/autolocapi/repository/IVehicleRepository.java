package tn.esprit.autoloc.autolocapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.autolocapi.domain.Vehicle;

public interface IVehicleRepository extends JpaRepository<Vehicle, Long> {
}
