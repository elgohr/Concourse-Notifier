package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.api.Job
import com.elgohr.concourse.notifier.api.Pipeline
import spock.lang.Specification

class BufferSpec extends Specification {

    def "buffers jobs"() {
        given:
        def buffer = new Buffer()
        def jobs = [new Job("NAME", "PIPELINE1", new URL("http://URL"), "STATUS"),
                    new Job("NAME1", "PIPELINE1", new URL("http://URL1"), "STATUS1"),
                    new Job("NAME2", "PIPELINE1", new URL("http://URL2"), "STATUS2")]
        when:
        jobs.forEach({
            buffer.setJob(it.name, it)
        })

        then:
        buffer.getJobs().values().containsAll(jobs)
        buffer.getJobs().keySet().containsAll(["NAME", "NAME1", "NAME2"])
        buffer.getJobs().values().size() == jobs.size()
    }

    def "buffers pipelines"() {
        given:
        def buffer = new Buffer()
        def pipelines = [new Pipeline("NAME", "TEAM", new URL("http://URL")),
                         new Pipeline("NAME1", "TEAM1", new URL("http://URL"))]
        when:
        pipelines.forEach({
            buffer.setPipeline(it.name, it)
        })
        then:
        buffer.getPipelines().values().containsAll(pipelines)
        buffer.getPipelines().keySet().containsAll(["NAME", "NAME1"])
        buffer.getPipelines().values().size() == pipelines.size()
    }
}
