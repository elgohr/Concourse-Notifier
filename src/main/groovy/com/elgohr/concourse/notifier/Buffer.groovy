package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.api.Job
import com.elgohr.concourse.notifier.api.Pipeline

class Buffer {

    private HashMap<String, Job> jobs = new HashMap<>()
    private HashMap<String, Pipeline> pipelines = new HashMap<>()

    def getPipelines() {
        return pipelines
    }

    void setPipeline(String pipelineId, Pipeline pipeline) {
        pipelines.put(pipelineId, pipeline)
    }

    def getJobs() {
        return jobs
    }

    void setJob(String jobId, Job job) {
        jobs.put(jobId, job)
    }
}
