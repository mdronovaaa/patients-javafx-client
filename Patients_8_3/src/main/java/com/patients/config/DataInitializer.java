package com.patients.config;

import com.patients.domain.Patient;
import com.patients.repo.PatientRepository;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    private final PatientRepository patientRepository;

    public DataInitializer(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @PostConstruct
    public void seedPatients() {
        if (patientRepository.count() > 0) {
            return;
        }
        Patient p1 = new Patient();
        p1.setName("Иван Петров");
        p1.setBirthDate(LocalDate.of(1990, 5, 12));
        p1.setDiagnosis("ОРВИ");

        Patient p2 = new Patient();
        p2.setName("Анна Смирнова");
        p2.setBirthDate(LocalDate.of(1985, 11, 3));
        p2.setDiagnosis("Гипертония");

        Patient p3 = new Patient();
        p3.setName("Сергей Иванов");
        p3.setBirthDate(LocalDate.of(1978, 2, 24));
        p3.setDiagnosis("Диабет 2 типа");

        patientRepository.saveAll(List.of(p1, p2, p3));
    }
}






