package jobplatform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jobplatform.model.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findAllByOrderByIdAsc();
    //grabs the next available job while skipping already locked rows.
    @Query(value = "SELECT * FROM jobs WHERE status = 'QUEUED' ORDER BY id LIMIT 1 FOR UPDATE SKIP LOCKED",
           nativeQuery = true)
    Optional<Job> findNextQueuedForUpdate();
}