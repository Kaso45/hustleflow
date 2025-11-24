package hustle.employee_management1.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String gender;
    private int age;
    private String jobTitle;
    private Timestamp hireDate;

}