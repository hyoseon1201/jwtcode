package com.example.demo.repository;

import com.example.demo.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    List<Study> findAllByCreatorUsername(String username);
}
