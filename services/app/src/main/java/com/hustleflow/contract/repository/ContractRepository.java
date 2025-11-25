package com.hustleflow.contract.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hustleflow.contract.domain.Contract;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByEmployeeId(Long employeeId);
}
