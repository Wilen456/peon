package jobplatform.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jobplatform.model.Job;
import jobplatform.service.JobService;
import jobplatform.dto.CreateJobRequest;

@RestController
@RequestMapping("/api/jobs")//temporary routing. will change for later
public class JobController {
    //main controller for program. default constructor that ties in the jobservice class which carries the interactions for the job (get, set, etc.)

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }
    //gets jobs from current instance of jobservice, currently stored in RAM. will be moved to DB later.
    @GetMapping
    public List<Job> getJobs() {
        return jobService.getJobs();
    }
    //calls on the jobservice to make a job, using the type provided by client.
    @PostMapping
    public Job createJob(@RequestBody CreateJobRequest request) {
        return jobService.createJob(request.getType());
}
}