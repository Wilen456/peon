package jobplatform.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jobplatform.model.Job;
import jobplatform.repository.JobRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // returns all jobs from the database, oldest first
    public List<Job> getJobs() {
        return jobRepository.findAllByOrderByIdAsc();
    }

    // saves a new job to the database; Postgres assigns the id
    public Job createJob(String type) {
        return jobRepository.save(new Job(type));
    }
}