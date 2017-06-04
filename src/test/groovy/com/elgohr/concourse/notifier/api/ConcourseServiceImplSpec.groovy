package com.elgohr.concourse.notifier.api

import com.elgohr.concourse.notifier.Settings
import spock.lang.Specification

class ConcourseServiceImplSpec extends Specification {

    def wrapper

    def setup() {
        def settings = new Settings.SettingsBuilder().url(new URL("http://ci.endpoint")).build()
        wrapper = new ConcourseServiceImpl(settings)
    }

    def "unwraps jobs from concourse response"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","url":"/teams/main/pipelines/main/jobs/fly","next_build":null,"finished_build":{"id":38470,"team_name":"main","name":"671","status":"succeeded","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","pipeline_name":"main","start_time":1495238440,"end_time":1495238756},"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]},{"id":2,"name":"go-concourse","url":"/teams/main/pipelines/main/jobs/go-concourse","next_build":null,"finished_build":{"id":38471,"team_name":"main","name":"698","status":"succeeded","job_name":"go-concourse","url":"/teams/main/pipelines/main/jobs/go-concourse/builds/698","api_url":"/api/v1/builds/38471","pipeline_name":"main","start_time":1495238440,"end_time":1495238550},"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'
        URL.class.metaClass.text = mockConcourseResponse
        when:
        def jobs = wrapper.getJobs()
        then:
        jobs[0].name == "fly"
        jobs[0].url == "/teams/main/pipelines/main/jobs/fly"
        jobs[0].status == "succeeded"
        jobs[0].pipeline == "main"
        jobs[1].name == "go-concourse"
        jobs[1].url == "/teams/main/pipelines/main/jobs/go-concourse"
        jobs[1].status == "succeeded"
        jobs[1].pipeline == "main"
    }

    def "handles jobs without pipeline"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","url":"/teams/main/pipelines/main/jobs/fly","next_build":null,"finished_build":{"id":38470,"team_name":"main","name":"671","status":"succeeded","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","start_time":1495238440,"end_time":1495238756},"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'
        URL.class.metaClass.text = mockConcourseResponse
        when:
        def jobs = wrapper.getJobs()
        then:
        jobs[0].name == "fly"
        jobs[0].url == "/teams/main/pipelines/main/jobs/fly"
        jobs[0].status == "succeeded"
        jobs[0].pipeline == ""
    }

    def "handles jobs without url"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","next_build":null,"finished_build":{"id":38470,"team_name":"main","name":"671","status":"succeeded","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","pipeline_name":"main","start_time":1495238440,"end_time":1495238756},"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'
        URL.class.metaClass.text = mockConcourseResponse
        when:
        def jobs = wrapper.getJobs()
        then:
        jobs[0].name == "fly"
        jobs[0].url == ""
        jobs[0].status == "succeeded"
        jobs[0].pipeline == "main"
    }

    def "handles jobs without status"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","url":"/teams/main/pipelines/main/jobs/fly","next_build":null,"finished_build":{"id":38470,"team_name":"main","name":"671","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","pipeline_name":"main","start_time":1495238440,"end_time":1495238756},"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'
        URL.class.metaClass.text = mockConcourseResponse
        when:
        def jobs = wrapper.getJobs()
        then:
        jobs[0].name == "fly"
        jobs[0].url == "/teams/main/pipelines/main/jobs/fly"
        jobs[0].status == ""
        jobs[0].pipeline == "main"
    }

    def "handles empty responses"() {
        given:
        def mockConcourseResponse = '[]'
        URL.class.metaClass.text = mockConcourseResponse
        when:
        def jobs = wrapper.getJobs()
        then:
        jobs.size() == 0
    }

    def "handles connection errors"() {
        given:
        URL.class.metaClass.text = { -> throw new RuntimeException()}
        when:
        def jobs = wrapper.getJobs()
        then:
        jobs.size() == 0
    }

    def "handles paused jobs"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","url":"/teams/main/pipelines/main/jobs/fly","next_build":null,"finished_build":null,"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'
        URL.class.metaClass.text = mockConcourseResponse
        when:
        def jobs = wrapper.getJobs()
        then:
        jobs[0].name == "fly"
        jobs[0].url == "/teams/main/pipelines/main/jobs/fly"
        jobs[0].status == ""
        jobs[0].pipeline == ""
    }

    def "looks out for new build jobs"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","url":"/teams/main/pipelines/main/jobs/fly","next_build":{"id":38470,"team_name":"main","name":"671","status":"started","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","pipeline_name":"main","start_time":1495238440,"end_time":1495238756},"finished_build":null,"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'
        URL.class.metaClass.text = mockConcourseResponse
        when:
        def jobs = wrapper.getJobs()
        then:
        jobs[0].name == "fly"
        jobs[0].url == "/teams/main/pipelines/main/jobs/fly"
        jobs[0].status == "started"
        jobs[0].pipeline == "main"
    }

    def "prefers new build jobs over finished jobs"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","url":"/teams/main/pipelines/main/jobs/fly","next_build":{"id":38470,"team_name":"main","name":"671","status":"started","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","pipeline_name":"main","start_time":1495238440,"end_time":1495238756},"finished_build":{"id":38469,"team_name":"main","name":"670","status":"finished","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","pipeline_name":"main","start_time":1495238440,"end_time":1495238756},"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'
        URL.class.metaClass.text = mockConcourseResponse
        when:
        def jobs = wrapper.getJobs()
        then:
        jobs[0].name == "fly"
        jobs[0].url == "/teams/main/pipelines/main/jobs/fly"
        jobs[0].status == "started"
        jobs[0].pipeline == "main"
    }
}
