package jobplatform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;

    @Enumerated(EnumType.STRING)
    private JobStatus status;
    //auto assign from jobstatus list, avoiding invalid statuses.

    private Instant startedAt;

    public Instant getStartedAt() { return startedAt; }//framework for timeouts
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }



    protected Job() {} // required by JPA

    public Job(String type) {
        this.type = type;
        this.status = JobStatus.QUEUED; // auto-assigned on creation
    }

    public Long getId() { return id; }
    public String getType() { return type; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus newStatus) { this.status = newStatus; }
}