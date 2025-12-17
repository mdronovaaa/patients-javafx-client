package com.patientsfx.service;

import com.patientsfx.config.RestTemplateConfig;
import com.patientsfx.model.Patient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class PatientApiService {
    private static final String BASE_URL = "http://localhost:8080/api/patients";
    private final RestTemplate restTemplate;

    public PatientApiService() {
        this.restTemplate = RestTemplateConfig.restTemplate();
    }

    public List<Patient> getAllPatients() {
        try {
            ResponseEntity<List<Patient>> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Patient>>() {}
            );
            return response.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException("Error fetching patients: " + e.getMessage(), e);
        }
    }

    public Patient getPatientById(Long id) {
        try {
            ResponseEntity<Patient> response = restTemplate.getForEntity(
                    BASE_URL + "/" + id,
                    Patient.class
            );
            return response.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException("Error fetching patient by id: " + e.getMessage(), e);
        }
    }

    public Patient createPatient(Patient patient) {
        try {
            ResponseEntity<Patient> response = restTemplate.postForEntity(
                    BASE_URL,
                    patient,
                    Patient.class
            );
            if (response.getStatusCode() == HttpStatus.CREATED) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to create patient: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Error creating patient: " + e.getMessage(), e);
        }
    }

    public Patient updatePatient(Long id, Patient patient) {
        try {
            ResponseEntity<Patient> response = restTemplate.exchange(
                    BASE_URL + "/" + id,
                    HttpMethod.PUT,
                    new org.springframework.http.HttpEntity<>(patient),
                    Patient.class
            );
            return response.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException("Error updating patient: " + e.getMessage(), e);
        }
    }

    public void deletePatient(Long id) {
        try {
            restTemplate.delete(BASE_URL + "/" + id);
        } catch (RestClientException e) {
            throw new RuntimeException("Error deleting patient: " + e.getMessage(), e);
        }
    }
}


