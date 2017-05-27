package com.elgohr.concourse.notifier.api

import com.elgohr.concourse.notifier.ConcourseService
import groovy.json.JsonSlurper

class ConcourseServiceImpl implements ConcourseService{

    def static final JOBS_URL = "/api/v1/teams/main/pipelines/main/jobs"

    def final jobsEndpoint
    def final jsonSlurper

    ConcourseServiceImpl(URL endpoint) {
        this.jsonSlurper = new JsonSlurper()
        this.jobsEndpoint = new URL(endpoint.toString() + JOBS_URL)
    }

    List getJobs() {
        def responseContent = jobsEndpoint.text
        def response = jsonSlurper.parseText responseContent
        def newJobs = [] as List
        for (job in response) {
            def pipeline = (job.finished_build != null) ? job.finished_build.pipeline_name : ""
            def status = (job.finished_build != null) ? job.finished_build.status : ""
            newJobs.add(new Job(
                    job.name,
                    pipeline,
                    job.url,
                    status
            ))
        }
        newJobs
    }

}
