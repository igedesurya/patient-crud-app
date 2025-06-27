package com.xtramile.patient.web.rest;

import static com.xtramile.patient.domain.PatientAsserts.*;
import static com.xtramile.patient.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtramile.patient.IntegrationTest;
import com.xtramile.patient.domain.Patient;
import com.xtramile.patient.repository.PatientRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PatientResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PatientResourceIT {

    private static final String DEFAULT_PID = "AAAAAAAAAA";
    private static final String UPDATED_PID = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_GENDER = "AAAAAAAAAA";
    private static final String UPDATED_GENDER = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_SUBURB = "AAAAAAAAAA";
    private static final String UPDATED_SUBURB = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_POSTCODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTCODE = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/patients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPatientMockMvc;

    private Patient patient;

    private Patient insertedPatient;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Patient createEntity() {
        return new Patient()
            .pid(DEFAULT_PID)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
            .gender(DEFAULT_GENDER)
            .address(DEFAULT_ADDRESS)
            .suburb(DEFAULT_SUBURB)
            .state(DEFAULT_STATE)
            .postcode(DEFAULT_POSTCODE)
            .phoneNumber(DEFAULT_PHONE_NUMBER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Patient createUpdatedEntity() {
        return new Patient()
            .pid(UPDATED_PID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .gender(UPDATED_GENDER)
            .address(UPDATED_ADDRESS)
            .suburb(UPDATED_SUBURB)
            .state(UPDATED_STATE)
            .postcode(UPDATED_POSTCODE)
            .phoneNumber(UPDATED_PHONE_NUMBER);
    }

    @BeforeEach
    void initTest() {
        patient = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPatient != null) {
            patientRepository.delete(insertedPatient);
            insertedPatient = null;
        }
    }

    @Test
    @Transactional
    void createPatient() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Patient
        var returnedPatient = om.readValue(
            restPatientMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Patient.class
        );

        // Validate the Patient in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPatientUpdatableFieldsEquals(returnedPatient, getPersistedPatient(returnedPatient));

        insertedPatient = returnedPatient;
    }

    @Test
    @Transactional
    void createPatientWithExistingId() throws Exception {
        // Create the Patient with an existing ID
        patient.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isBadRequest());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPidIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        patient.setPid(null);

        // Create the Patient, which fails.

        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        patient.setFirstName(null);

        // Create the Patient, which fails.

        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        patient.setLastName(null);

        // Create the Patient, which fails.

        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateOfBirthIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        patient.setDateOfBirth(null);

        // Create the Patient, which fails.

        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGenderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        patient.setGender(null);

        // Create the Patient, which fails.

        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        patient.setAddress(null);

        // Create the Patient, which fails.

        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSuburbIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        patient.setSuburb(null);

        // Create the Patient, which fails.

        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        patient.setState(null);

        // Create the Patient, which fails.

        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPostcodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        patient.setPostcode(null);

        // Create the Patient, which fails.

        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        patient.setPhoneNumber(null);

        // Create the Patient, which fails.

        restPatientMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPatients() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.saveAndFlush(patient);

        // Get all the patientList
        restPatientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(patient.getId().intValue())))
            .andExpect(jsonPath("$.[*].pid").value(hasItem(DEFAULT_PID)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(DEFAULT_DATE_OF_BIRTH.toString())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].suburb").value(hasItem(DEFAULT_SUBURB)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].postcode").value(hasItem(DEFAULT_POSTCODE)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)));
    }

    @Test
    @Transactional
    void getPatient() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.saveAndFlush(patient);

        // Get the patient
        restPatientMockMvc
            .perform(get(ENTITY_API_URL_ID, patient.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(patient.getId().intValue()))
            .andExpect(jsonPath("$.pid").value(DEFAULT_PID))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.dateOfBirth").value(DEFAULT_DATE_OF_BIRTH.toString()))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.suburb").value(DEFAULT_SUBURB))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE))
            .andExpect(jsonPath("$.postcode").value(DEFAULT_POSTCODE))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER));
    }

    @Test
    @Transactional
    void getNonExistingPatient() throws Exception {
        // Get the patient
        restPatientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPatient() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.saveAndFlush(patient);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the patient
        Patient updatedPatient = patientRepository.findById(patient.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPatient are not directly saved in db
        em.detach(updatedPatient);
        updatedPatient
            .pid(UPDATED_PID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .gender(UPDATED_GENDER)
            .address(UPDATED_ADDRESS)
            .suburb(UPDATED_SUBURB)
            .state(UPDATED_STATE)
            .postcode(UPDATED_POSTCODE)
            .phoneNumber(UPDATED_PHONE_NUMBER);

        restPatientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPatient.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPatient))
            )
            .andExpect(status().isOk());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPatientToMatchAllProperties(updatedPatient);
    }

    @Test
    @Transactional
    void putNonExistingPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPatientMockMvc
            .perform(put(ENTITY_API_URL_ID, patient.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isBadRequest());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(patient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatientMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patient)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePatientWithPatch() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.saveAndFlush(patient);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the patient using partial update
        Patient partialUpdatedPatient = new Patient();
        partialUpdatedPatient.setId(patient.getId());

        partialUpdatedPatient.gender(UPDATED_GENDER).suburb(UPDATED_SUBURB).phoneNumber(UPDATED_PHONE_NUMBER);

        restPatientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPatient.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPatient))
            )
            .andExpect(status().isOk());

        // Validate the Patient in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPatientUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPatient, patient), getPersistedPatient(patient));
    }

    @Test
    @Transactional
    void fullUpdatePatientWithPatch() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.saveAndFlush(patient);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the patient using partial update
        Patient partialUpdatedPatient = new Patient();
        partialUpdatedPatient.setId(patient.getId());

        partialUpdatedPatient
            .pid(UPDATED_PID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .gender(UPDATED_GENDER)
            .address(UPDATED_ADDRESS)
            .suburb(UPDATED_SUBURB)
            .state(UPDATED_STATE)
            .postcode(UPDATED_POSTCODE)
            .phoneNumber(UPDATED_PHONE_NUMBER);

        restPatientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPatient.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPatient))
            )
            .andExpect(status().isOk());

        // Validate the Patient in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPatientUpdatableFieldsEquals(partialUpdatedPatient, getPersistedPatient(partialUpdatedPatient));
    }

    @Test
    @Transactional
    void patchNonExistingPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPatientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, patient.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(patient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(patient))
            )
            .andExpect(status().isBadRequest());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPatient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patient.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatientMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(patient)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Patient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePatient() throws Exception {
        // Initialize the database
        insertedPatient = patientRepository.saveAndFlush(patient);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the patient
        restPatientMockMvc
            .perform(delete(ENTITY_API_URL_ID, patient.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return patientRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Patient getPersistedPatient(Patient patient) {
        return patientRepository.findById(patient.getId()).orElseThrow();
    }

    protected void assertPersistedPatientToMatchAllProperties(Patient expectedPatient) {
        assertPatientAllPropertiesEquals(expectedPatient, getPersistedPatient(expectedPatient));
    }

    protected void assertPersistedPatientToMatchUpdatableProperties(Patient expectedPatient) {
        assertPatientAllUpdatablePropertiesEquals(expectedPatient, getPersistedPatient(expectedPatient));
    }
}
