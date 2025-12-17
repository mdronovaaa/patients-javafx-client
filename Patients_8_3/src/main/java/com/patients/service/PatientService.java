package com.patients.service;

import com.patients.domain.Patient;
import com.patients.repo.PatientRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient getById(@NotNull Long id) {
        return patientRepository.findById(id).orElseThrow();
    }

    public Patient create(@Valid Patient patient) {
        patient.setId(null);
        return patientRepository.save(patient);
    }

    public Patient update(@NotNull Long id, @Valid Patient updated) {
        Patient existing = getById(id);
        existing.setName(updated.getName());
        existing.setBirthDate(updated.getBirthDate());
        existing.setDiagnosis(updated.getDiagnosis());
        return patientRepository.save(existing);
    }

    public void delete(@NotNull Long id) {
        patientRepository.deleteById(id);
    }
}


