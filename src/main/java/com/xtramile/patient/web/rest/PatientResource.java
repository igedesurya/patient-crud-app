package com.xtramile.patient.web.rest;

import com.xtramile.patient.domain.Patient;
import com.xtramile.patient.repository.PatientRepository;
import com.xtramile.patient.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.xtramile.patient.domain.Patient}.
 */
@RestController
@RequestMapping("/api/patients")
@Transactional
public class PatientResource {

    private static final Logger LOG = LoggerFactory.getLogger(PatientResource.class);

    private static final String ENTITY_NAME = "patient";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PatientRepository patientRepository;

    public PatientResource(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * {@code POST  /patients} : Create a new patient.
     *
     * @param patient the patient to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new patient, or with status {@code 400 (Bad Request)} if the patient has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) throws URISyntaxException {
        LOG.debug("REST request to save Patient : {}", patient);
        if (patient.getId() != null) {
            throw new BadRequestAlertException("A new patient cannot already have an ID", ENTITY_NAME, "idexists");
        }
        patient = patientRepository.save(patient);
        return ResponseEntity.created(new URI("/api/patients/" + patient.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, patient.getId().toString()))
            .body(patient);
    }

    /**
     * {@code PUT  /patients/:id} : Updates an existing patient.
     *
     * @param id the id of the patient to save.
     * @param patient the patient to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated patient,
     * or with status {@code 400 (Bad Request)} if the patient is not valid,
     * or with status {@code 500 (Internal Server Error)} if the patient couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Patient patient
    ) throws URISyntaxException {
        LOG.debug("REST request to update Patient : {}, {}", id, patient);
        if (patient.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, patient.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!patientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        patient = patientRepository.save(patient);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, patient.getId().toString()))
            .body(patient);
    }

    /**
     * {@code PATCH  /patients/:id} : Partial updates given fields of an existing patient, field will ignore if it is null
     *
     * @param id the id of the patient to save.
     * @param patient the patient to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated patient,
     * or with status {@code 400 (Bad Request)} if the patient is not valid,
     * or with status {@code 404 (Not Found)} if the patient is not found,
     * or with status {@code 500 (Internal Server Error)} if the patient couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Patient> partialUpdatePatient(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Patient patient
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Patient partially : {}, {}", id, patient);
        if (patient.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, patient.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!patientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Patient> result = patientRepository
            .findById(patient.getId())
            .map(existingPatient -> {
                if (patient.getPid() != null) {
                    existingPatient.setPid(patient.getPid());
                }
                if (patient.getFirstName() != null) {
                    existingPatient.setFirstName(patient.getFirstName());
                }
                if (patient.getLastName() != null) {
                    existingPatient.setLastName(patient.getLastName());
                }
                if (patient.getDateOfBirth() != null) {
                    existingPatient.setDateOfBirth(patient.getDateOfBirth());
                }
                if (patient.getGender() != null) {
                    existingPatient.setGender(patient.getGender());
                }
                if (patient.getAddress() != null) {
                    existingPatient.setAddress(patient.getAddress());
                }
                if (patient.getSuburb() != null) {
                    existingPatient.setSuburb(patient.getSuburb());
                }
                if (patient.getState() != null) {
                    existingPatient.setState(patient.getState());
                }
                if (patient.getPostcode() != null) {
                    existingPatient.setPostcode(patient.getPostcode());
                }
                if (patient.getPhoneNumber() != null) {
                    existingPatient.setPhoneNumber(patient.getPhoneNumber());
                }

                return existingPatient;
            })
            .map(patientRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, patient.getId().toString())
        );
    }

    /**
     * {@code GET  /patients} : get all the patients.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of patients in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Patient>> getAllPatients(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Patients");
        Page<Patient> page = patientRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam String query) {
        List<Patient> results = patientRepository
            .findByPidContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query, query);
        return ResponseEntity.ok(results);
    }

    /**
     * {@code GET  /patients/:id} : get the "id" patient.
     *
     * @param id the id of the patient to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the patient, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Patient : {}", id);
        Optional<Patient> patient = patientRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(patient);
    }

    /**
     * {@code DELETE  /patients/:id} : delete the "id" patient.
     *
     * @param id the id of the patient to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Patient : {}", id);
        patientRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }


}
