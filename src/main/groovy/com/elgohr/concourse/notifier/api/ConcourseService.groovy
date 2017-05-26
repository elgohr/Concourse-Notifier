package com.elgohr.concourse.notifier.api

import groovy.json.JsonSlurper

class ConcourseService {

    def static final JOBS_URL = "/api/v1/teams/main/pipelines/main/jobs"

    def final jobsEndpoint
    def final jsonSlurper

    ConcourseService(URL endpoint) {
        this.jsonSlurper = new JsonSlurper()
        this.jobsEndpoint = new URL(endpoint.toString() + JOBS_URL)
    }

    def getJobs() {
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
