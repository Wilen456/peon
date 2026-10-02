package jobplatform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String status;

    protected Job() {} // required by JPA

    public Job(String type) {
        this.type = type;
        this.status = "QUEUED"; // auto-assigned on creation
    }

    public Long getId() { return id; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public void setStatus(String newStatus) { this.status = newStatus; }
}