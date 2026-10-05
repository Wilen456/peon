package jobplatform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ThreadLocalRandom;

import jobplatform.model.JobStatus;
import jobplatform.repository.JobRepository;

@Component
public class JobWorker {

    private static final Logger log = LoggerFactory.getLogger(JobWorker.class);
    private static final long MIN_WORK_MILLIS = 2000;  // fastest a job can take, arbitrary for testing
    private static final long MAX_WORK_MILLIS = 10000; // slowest a job can take, arbitrary for testing
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
            long workMillis = ThreadLocalRandom.current().nextLong(MIN_WORK_MILLIS, MAX_WORK_MILLIS + 1);
            log.info("Job {} started (will take {} ms)", jobId, workMillis);
            Thread.sleep(workMillis); // real work would go here

            if (ThreadLocalRandom.current().nextInt(100) < 10) {
                throw new IllegalStateException("Simulated failure");
            }


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