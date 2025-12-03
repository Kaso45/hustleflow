package com.hustleflow.performance_assessment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hustleflow.performance_assessment.domain.Assessment;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    public List<Assessment> findByEmployeeId(Long employeeId);

}
