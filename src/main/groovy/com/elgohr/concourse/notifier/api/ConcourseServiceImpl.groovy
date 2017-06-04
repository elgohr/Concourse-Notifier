package com.elgohr.concourse.notifier.api

import com.elgohr.concourse.notifier.ConcourseService
import com.elgohr.concourse.notifier.Settings
import groovy.json.JsonSlurper

class ConcourseServiceImpl implements ConcourseService{

    def static final JOBS_URL = "/api/v1/teams/main/pipelines/main/jobs"

    def final jobsEndpoint
    def final jsonSlurper

    ConcourseServiceImpl(Settings settings) {
        this.jsonSlurper = new JsonSlurper()
        this.jobsEndpoint = new URL(settings.url.toString() + JOBS_URL)
    }

    List getJobs() {
        def responseContent = jobsEndpoint.text
        def response = jsonSlurper.parseText responseContent
        def newJobs = [] as List
        for (job in response) {
            def finishedPipeline = (job.finished_build != null) ? job.finished_build.pipeline_name : ""
            def finishedStatus = (job.finished_build != null) ? job.finished_build.status : ""
            def nextPipeline = (job.next_build != null) ? job.next_build.pipeline_name : finishedPipeline
            def nextStatus = (job.next_build != null) ? job.next_build.status : finishedStatus
            newJobs.add(new Job(
                    job.name,
                    nextPipeline,
                    job.url,
                    nextStatus
            ))
        }
        newJobs
    }

}
