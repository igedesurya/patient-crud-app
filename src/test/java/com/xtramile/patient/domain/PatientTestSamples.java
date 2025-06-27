package com.xtramile.patient.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PatientTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Patient getPatientSample1() {
        return new Patient()
            .id(1L)
            .pid("pid1")
            .firstName("firstName1")
            .lastName("lastName1")
            .gender("gender1")
            .address("address1")
            .suburb("suburb1")
            .state("state1")
            .postcode("postcode1")
            .phoneNumber("phoneNumber1");
    }

    public static Patient getPatientSample2() {
        return new Patient()
            .id(2L)
            .pid("pid2")
            .firstName("firstName2")
            .lastName("lastName2")
            .gender("gender2")
            .address("address2")
            .suburb("suburb2")
            .state("state2")
            .postcode("postcode2")
            .phoneNumber("phoneNumber2");
    }

    public static Patient getPatientRandomSampleGenerator() {
        return new Patient()
            .id(longCount.incrementAndGet())
            .pid(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .gender(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .suburb(UUID.randomUUID().toString())
            .state(UUID.randomUUID().toString())
            .postcode(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString());
    }
}
