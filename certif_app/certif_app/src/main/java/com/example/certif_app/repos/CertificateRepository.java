package com.example.certif_app.repos;

import com.example.certif_app.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Integer> {


    Certificate findCertificateByCertificateNumber(Long id);
    Certificate findByFirstNameAndLastNameAndTitle(String firstName, String lastName, String title);

    Optional<Certificate> findByFirstNameAndLastNameAndCinAndTitle(String firstName, String lastName, Long cin, String title);
}

