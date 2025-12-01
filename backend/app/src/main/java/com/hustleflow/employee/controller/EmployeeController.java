package com.hustleflow.employee.controller;

import com.hustleflow.employee.domain.EmployeeRequest;
import com.hustleflow.employee.domain.EmployeeResponse;
import com.hustleflow.employee.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    // GET /api/employees
    @GetMapping
    public List<EmployeeResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Long employeeId) {
        EmployeeResponse res = service.getById(employeeId);
        if (res == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<CreateEmployeeResponse> create(@RequestBody EmployeeRequest req) {
        Long id = service.create(req);
        return ResponseEntity.ok(new CreateEmployeeResponse("Create employee successfully", id));
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable Long employeeId,
            @RequestBody EmployeeRequest req) {
        EmployeeResponse res = service.update(employeeId, req);
        if (res == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> delete(@PathVariable Long employeeId) {
        service.delete(employeeId);
        return ResponseEntity.noContent().build();
    }

    static class CreateEmployeeResponse {
        private final String message;
        private final Long employeeId;

        public CreateEmployeeResponse(String message, Long employeeId) {
            this.message = message;
            this.employeeId = employeeId;
        }

        public String getMessage() {
            return message;
        }

        public Long getEmployeeId() {
            return employeeId;
        }
    }
}
