package com.elgohr.concourse.notifier.api

import com.elgohr.concourse.notifier.Settings
import spock.lang.Specification

class ConcourseServiceImplSpec extends Specification {

    def wrapper, mockHttpClient
    def mockPipeline = new Pipeline("test", "test", new URL("http://PIPELINE_URL"))

    def setup() {
        def settings = new Settings()
        settings.setUrl(new URL("http://baseUrl"))
        mockHttpClient = Mock(HttpClient)
        wrapper = new ConcourseServiceImpl(settings, mockHttpClient)
    }

    def "getJobs - unwraps jobs from concourse response"() {
        given:
        def mockConcourseResponse = '[\n' +
                '  {\n' +
                '    "id": 1,\n' +
                '    "name": "fly",\n' +
                '    "url": "/teams/main/pipelines/main/jobs/fly",\n' +
                '    "next_build": null,\n' +
                '    "finished_build": {\n' +
                '      "id": 38470,\n' +
                '      "team_name": "main",\n' +
                '      "name": "671",\n' +
                '      "status": "succeeded",\n' +
                '      "job_name": "fly",\n' +
                '      "url": "/teams/main/pipelines/main/jobs/fly/builds/671",\n' +
                '      "api_url": "/api/v1/builds/38470",\n' +
                '      "pipeline_name": "main",\n' +
                '      "start_time": 1495238440,\n' +
                '      "end_time": 1495238756\n' +
                '    },\n' +
                '    "inputs": [\n' +
                '      {\n' +
                '        "name": "concourse",\n' +
                '        "resource": "concourse",\n' +
                '        "trigger": true\n' +
                '      }\n' +
                '    ],\n' +
                '    "outputs": [],\n' +
                '    "groups": [\n' +
                '      "develop"\n' +
                '    ]\n' +
                '  },\n' +
                '  {\n' +
                '    "id": 2,\n' +
                '    "name": "go-concourse",\n' +
                '    "url": "/teams/main/pipelines/main/jobs/go-concourse",\n' +
                '    "next_build": null,\n' +
                '    "finished_build": {\n' +
                '      "id": 38471,\n' +
                '      "team_name": "main",\n' +
                '      "name": "698",\n' +
                '      "status": "succeeded",\n' +
                '      "job_name": "go-concourse",\n' +
                '      "url": "/teams/main/pipelines/main/jobs/go-concourse/builds/698",\n' +
                '      "api_url": "/api/v1/builds/38471",\n' +
                '      "pipeline_name": "main",\n' +
                '      "start_time": 1495238440,\n' +
                '      "end_time": 1495238550\n' +
                '    },\n' +
                '    "inputs": [\n' +
                '      {\n' +
                '        "name": "concourse",\n' +
                '        "resource": "concourse",\n' +
                '        "trigger": true\n' +
                '      }\n' +
                '    ],\n' +
                '    "outputs": [],\n' +
                '    "groups": [\n' +
                '      "develop"\n' +
                '    ]\n' +
                '  }\n' +
                ']'
        when:
        def jobs = wrapper.getJobs(mockPipeline)

        then:
        1 * mockHttpClient.get(new URL("http://PIPELINE_URL/jobs")) >> mockConcourseResponse

        jobs[0].name == "fly"
        jobs[0].url.toString() == "http://baseUrl/api/v1/teams/main/pipelines/main/jobs/fly"
        jobs[0].status == "succeeded"
        jobs[0].pipeline == "main"
        jobs[1].name == "go-concourse"
        jobs[1].url.toString() == "http://baseUrl/api/v1/teams/main/pipelines/main/jobs/go-concourse"
        jobs[1].status == "succeeded"
        jobs[1].pipeline == "main"
    }

    def "getJobs - handles jobs without pipeline"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","url":"/teams/main/pipelines/main/jobs/fly","next_build":null,"finished_build":{"id":38470,"team_name":"main","name":"671","status":"succeeded","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","start_time":1495238440,"end_time":1495238756},"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'

        when:
        def jobs = wrapper.getJobs(mockPipeline)

        then:
        1 * mockHttpClient.get(_) >> mockConcourseResponse

        jobs[0].name == "fly"
        jobs[0].url.toString() == "http://baseUrl/api/v1/teams/main/pipelines/main/jobs/fly"
        jobs[0].status == "succeeded"
        jobs[0].pipeline == ""
    }

    def "getJobs - handles jobs without url"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","next_build":null,"finished_build":{"id":38470,"team_name":"main","name":"671","status":"succeeded","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","pipeline_name":"main","start_time":1495238440,"end_time":1495238756},"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'

        when:
        def jobs = wrapper.getJobs(mockPipeline)

        then:
        1 * mockHttpClient.get(_) >> mockConcourseResponse

        jobs[0].name == "fly"
        jobs[0].url.toString() == "http://baseUrl"
        jobs[0].status == "succeeded"
        jobs[0].pipeline == "main"
    }

    def "getJobs - handles jobs without status"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","url":"/teams/main/pipelines/main/jobs/fly","next_build":null,"finished_build":{"id":38470,"team_name":"main","name":"671","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","pipeline_name":"main","start_time":1495238440,"end_time":1495238756},"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'

        when:
        def jobs = wrapper.getJobs(mockPipeline)

        then:
        1 * mockHttpClient.get(_) >> mockConcourseResponse

        jobs[0].name == "fly"
        jobs[0].url.toString() == "http://baseUrl/api/v1/teams/main/pipelines/main/jobs/fly"
        jobs[0].status == ""
        jobs[0].pipeline == "main"
    }

    def "getJobs - handles empty responses"() {
        given:
        def mockConcourseResponse = '[]'

        when:
        def jobs = wrapper.getJobs(mockPipeline)

        then:
        1 * mockHttpClient.get(_) >> mockConcourseResponse

        jobs.size() == 0
    }

    def "getJobs - handles connection errors"() {
        when:
        def jobs = wrapper.getJobs(mockPipeline)

        then:
        1 * mockHttpClient.get(_) >> { URL url -> throw new RuntimeException() }

        jobs.size() == 0
    }

    def "getJobs - handles paused jobs"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","url":"/teams/main/pipelines/main/jobs/fly","next_build":null,"finished_build":null,"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'

        when:
        def jobs = wrapper.getJobs(mockPipeline)

        then:
        1 * mockHttpClient.get(_) >> mockConcourseResponse

        jobs[0].name == "fly"
        jobs[0].url.toString() == "http://baseUrl/api/v1/teams/main/pipelines/main/jobs/fly"
        jobs[0].status == ""
        jobs[0].pipeline == ""
    }

    def "getJobs - looks out for new build jobs"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","url":"/teams/main/pipelines/main/jobs/fly","next_build":{"id":38470,"team_name":"main","name":"671","status":"started","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","pipeline_name":"main","start_time":1495238440,"end_time":1495238756},"finished_build":null,"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'

        when:
        def jobs = wrapper.getJobs(mockPipeline)

        then:
        1 * mockHttpClient.get(_) >> mockConcourseResponse

        jobs[0].name == "fly"
        jobs[0].url.toString() == "http://baseUrl/api/v1/teams/main/pipelines/main/jobs/fly"
        jobs[0].status == "started"
        jobs[0].pipeline == "main"
    }

    def "getJobs - prefers new build jobs over finished jobs"() {
        given:
        def mockConcourseResponse = '[{"id":1,"name":"fly","url":"/teams/main/pipelines/main/jobs/fly","next_build":{"id":38470,"team_name":"main","name":"671","status":"started","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","pipeline_name":"main","start_time":1495238440,"end_time":1495238756},"finished_build":{"id":38469,"team_name":"main","name":"670","status":"finished","job_name":"fly","url":"/teams/main/pipelines/main/jobs/fly/builds/671","api_url":"/api/v1/builds/38470","pipeline_name":"main","start_time":1495238440,"end_time":1495238756},"inputs":[{"name":"concourse","resource":"concourse","trigger":true}],"outputs":[],"groups":["develop"]}]'

        when:
        def jobs = wrapper.getJobs(mockPipeline)

        then:
        1 * mockHttpClient.get(_) >> mockConcourseResponse

        jobs[0].name == "fly"
        jobs[0].url.toString() == "http://baseUrl/api/v1/teams/main/pipelines/main/jobs/fly"
        jobs[0].status == "started"
        jobs[0].pipeline == "main"
    }

    def "getPipelines - unwraps pipelines from concourse response"() {
        given:
        def mockConcourseResponse = '[\n' +
                '  {\n' +
                '    "id": 1,\n' +
                '    "name": "main",\n' +
                '    "url": "/teams/main/pipelines/main",\n' +
                '    "paused": false,\n' +
                '    "public": true,\n' +
                '    "groups": [\n' +
                '      {\n' +
                '        "name": "develop",\n' +
                '        "jobs": [\n' +
                '          "atc",\n' +
                '          "blackbox",\n' +
                '          "fly",\n' +
                '          "go-concourse",\n' +
                '          "baggageclaim",\n' +
                '          "groundcrew",\n' +
                '          "tsa",\n' +
                '          "rc",\n' +
                '          "build-fly",\n' +
                '          "bin-rc",\n' +
                '          "bosh-rc",\n' +
                '          "bin-testflight",\n' +
                '          "bosh-testflight",\n' +
                '          "bin-smoke",\n' +
                '          "bin-docker",\n' +
                '          "bin-docker-testflight",\n' +
                '          "bosh-deploy",\n' +
                '          "topgun"\n' +
                '        ]\n' +
                '      },\n' +
                '      {\n' +
                '        "name": "publish",\n' +
                '        "jobs": [\n' +
                '          "major",\n' +
                '          "minor",\n' +
                '          "patch",\n' +
                '          "shipit",\n' +
                '          "github-release",\n' +
                '          "push-docs",\n' +
                '          "promote-docker"\n' +
                '        ]\n' +
                '      },\n' +
                '      {\n' +
                '        "name": "workers",\n' +
                '        "jobs": [\n' +
                '          "install-go-windows"\n' +
                '        ]\n' +
                '      },\n' +
                '      {\n' +
                '        "name": "dependencies",\n' +
                '        "jobs": [\n' +
                '          "bump-btrfs",\n' +
                '          "bump-golang"\n' +
                '        ]\n' +
                '      }\n' +
                '    ],\n' +
                '    "team_name": "main"\n' +
                '  },\n' +
                '  {\n' +
                '    "id": 2,\n' +
                '    "name": "resources",\n' +
                '    "url": "/teams/main/pipelines/resources",\n' +
                '    "paused": false,\n' +
                '    "public": true,\n' +
                '    "team_name": "main"\n' +
                '  },\n' +
                '  {\n' +
                '    "id": 5,\n' +
                '    "name": "images",\n' +
                '    "url": "/teams/main/pipelines/images",\n' +
                '    "paused": false,\n' +
                '    "public": true,\n' +
                '    "team_name": "main"\n' +
                '  },\n' +
                '  {\n' +
                '    "id": 7,\n' +
                '    "name": "hangar",\n' +
                '    "url": "/teams/main/pipelines/hangar",\n' +
                '    "paused": true,\n' +
                '    "public": true,\n' +
                '    "team_name": "main"\n' +
                '  },\n' +
                '  {\n' +
                '    "id": 8,\n' +
                '    "name": "prs",\n' +
                '    "url": "/teams/main/pipelines/prs",\n' +
                '    "paused": false,\n' +
                '    "public": true,\n' +
                '    "team_name": "main"\n' +
                '  }\n' +
                ']'

        when:
        def pipelines = wrapper.getPipelines()

        then:
        1 * mockHttpClient.get(new URL("http://baseUrl/api/v1/pipelines")) >> mockConcourseResponse

        wrapper.pipelinesEndpoint.toString() == "http://baseUrl/api/v1/pipelines"

        pipelines[0].name == "main"
        pipelines[0].team == "main"
        pipelines[0].url.toString() == "http://baseUrl/api/v1/teams/main/pipelines/main"
        pipelines[1].name == "resources"
        pipelines[1].team == "main"
        pipelines[1].url.toString() == "http://baseUrl/api/v1/teams/main/pipelines/resources"
    }

}
