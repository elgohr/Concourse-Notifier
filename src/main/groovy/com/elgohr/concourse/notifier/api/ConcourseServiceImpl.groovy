package com.elgohr.concourse.notifier.api

import com.elgohr.concourse.notifier.ConcourseService
import com.elgohr.concourse.notifier.Settings
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

@Slf4j
class ConcourseServiceImpl implements ConcourseService {

    def static final PIPELINES_SUFFIX = "/api/v1/pipelines"
    def static final JOBS_SUFFIX = "/jobs"

    def final pipelinesEndpoint, jsonSlurper, settings, httpClient

    ConcourseServiceImpl(Settings settings, HttpClient httpClient) {
        this.jsonSlurper = new JsonSlurper()
        this.settings = settings
        this.httpClient = httpClient
        this.pipelinesEndpoint = new URL(settings.url.toString() + PIPELINES_SUFFIX)
    }

    List getPipelines() {
        def responseContent = httpClient.get(pipelinesEndpoint)
        def response = jsonSlurper.parseText responseContent
        def pipelines = [] as List
        for (pipeline in response) {
            def absoluteUrl = new URL(settings.getUrl().toString() + "/api/v1" + pipeline.url)
            pipelines.add(new Pipeline(
                    pipeline.name,
                    pipeline.team_name,
                    absoluteUrl
            ))
        }
        pipelines
    }

    List getJobs(Pipeline pipeline) {
        def newJobs = [] as List
        try {
            def url = new URL(pipeline.getUrl().toString() + JOBS_SUFFIX)
            def responseContent = httpClient.get(url)
            def response = jsonSlurper.parseText responseContent
            for (job in response) {
                def finishedPipeline = (job.finished_build != null) ? job.finished_build.pipeline_name : ""
                def finishedStatus = (job.finished_build != null) ? job.finished_build.status : ""
                def nextPipeline = (job.next_build != null) ? job.next_build.pipeline_name : finishedPipeline
                def nextStatus = (job.next_build != null) ? job.next_build.status : finishedStatus
                def absoluteUrl = (job.url != null) ? new URL(settings.getUrl().toString()+ "/api/v1" + job.url)
                        : new URL(settings.getUrl().toString())

                newJobs.add(new Job(
                        job.name,
                        nextPipeline,
                        absoluteUrl,
                        nextStatus
                ))
            }
        } catch (Exception e) {
            log.error "Could not get jobs. " + e.getCause()
        }
        newJobs
    }

}
