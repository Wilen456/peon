package jobplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jobplatform.model.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findAllByOrderByIdAsc();
    //list of jobs found in the database
}