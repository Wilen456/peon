package jobplatform.repository;

import java.time.Instant;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jobplatform.model.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findAllByOrderByIdAsc();
    @Modifying
        @Query("update Job j set j.status = jobplatform.model.JobStatus.QUEUED, j.startedAt = null "
         + "where j.status = jobplatform.model.JobStatus.RUNNING and j.startedAt < :cutoff")
    int requeueStaleJobs(@Param("cutoff") Instant cutoff);//returns how many jobs we've moved, for logging purposes.


    //grabs the next available job while skipping already locked rows.
    @Query(value = "SELECT * FROM jobs WHERE status = 'QUEUED' ORDER BY id LIMIT 1 FOR UPDATE SKIP LOCKED",
           nativeQuery = true)
    Optional<Job> findNextQueuedForUpdate();
}