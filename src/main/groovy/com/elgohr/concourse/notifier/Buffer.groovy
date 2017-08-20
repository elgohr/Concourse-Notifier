package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.api.Job
import com.elgohr.concourse.notifier.api.Pipeline

class Buffer {

    private HashMap<String, Job> jobs = new HashMap<>()
    private List<Pipeline> pipelines = new ArrayList<>()

    def getPipelines() {
        return pipelines
    }

    void setPipeline(Pipeline pipeline) {
        pipelines.add(pipeline)
    }

    def getJobs() {
        return jobs
    }

    void setJob(String jobId, Job job) {
        jobs.put(jobId, job)
    }
}
