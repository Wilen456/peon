package jobplatform.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jobplatform.model.Job;

@Service
public class JobService {
// will change this to accomodate sql
    private final List<Job> jobs = new ArrayList<>();// simulated database, just a list, but helpful for now.
    private Long nextId = 1L;

    //spits out current array of jobs
    public List<Job> getJobs() {
        return jobs;
    }
    //makes new job, appends it to end of jobs array.
    public Job createJob(String type) {
        Job job = new Job(nextId, type);
        nextId++;

        jobs.add(job);

        return job;
    }
}