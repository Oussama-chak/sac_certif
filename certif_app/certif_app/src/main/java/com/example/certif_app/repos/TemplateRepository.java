package com.example.certif_app.repos;

import com.example.certif_app.entities.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TemplateRepository extends JpaRepository<Template, Integer> {
    // Fetch the last inserted record (highest ID)
        @Query(value = "SELECT * FROM template ORDER BY id DESC LIMIT 1", nativeQuery = true)
        Template findLastInserted();
    }


