package jobplatform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import jobplatform.model.JobStatus;
import jobplatform.repository.JobRepository;

@Component
public class JobWorker {

    private static final Logger log = LoggerFactory.getLogger(JobWorker.class);
    private static final long WORK_MILLIS = 5000; // pretend each job takes 5 seconds

    private final JobRepository jobRepository;
    private final TransactionTemplate tx;

    public JobWorker(JobRepository jobRepository, TransactionTemplate tx) {
        this.jobRepository = jobRepository;
        this.tx = tx;
    }

    @Scheduled(fixedDelay = 2000) //every 2 seconds, check job list and tackle next avaialable one
    public void processNextJob() {
        // Step 1: claim a job and mark it RUNNING (committed right away so the UI can see it)
        Long jobId = tx.execute(status ->
            jobRepository.findNextQueuedForUpdate()
                .map(job -> {
                    job.setStatus(JobStatus.RUNNING);
                    return jobRepository.save(job).getId();
                })
                .orElse(null));

        if (jobId == null) {
            return; // nothing waiting
        }

        // Step 2: do the work, then record the outcome
        try {
            log.info("Job {} started", jobId);

            Thread.sleep(WORK_MILLIS); // real work would go here
            
            setStatus(jobId, JobStatus.COMPLETED);
            log.info("Job {} completed", jobId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            setStatus(jobId, JobStatus.FAILED);
        } catch (Exception e) {
            log.error("Job {} failed", jobId, e);
            setStatus(jobId, JobStatus.FAILED);
        }
    }

    private void setStatus(Long jobId, JobStatus newStatus) { //sets new status given jobid and whatever the new one is supposed to be.
        tx.executeWithoutResult(status ->
            jobRepository.findById(jobId).ifPresent(job -> {
                job.setStatus(newStatus);
                jobRepository.save(job);
            }));
    }
}