package com.xtramile.patient.repository;

import com.xtramile.patient.domain.Patient;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Patient entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByPidContainingIgnoreCase(String pid);
    List<Patient> findByFirstNameContainingIgnoreCase(String firstName);
    List<Patient> findByLastNameContainingIgnoreCase(String lastName);
    List<Patient> findByPidContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        String pid, String firstName, String lastName);
    }


