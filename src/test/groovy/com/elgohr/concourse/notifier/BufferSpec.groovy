package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.api.Job
import com.elgohr.concourse.notifier.api.Pipeline
import spock.lang.Specification

class BufferSpec extends Specification {

    Buffer buffer

    void setup() {
        buffer = new Buffer()
    }

    def "buffers jobs"() {
        given:
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
        def pipelines = [new Pipeline("NAME", "TEAM", new URL("http://URL")),
                         new Pipeline("NAME1", "TEAM1", new URL("http://URL"))]
        when:
        pipelines.forEach({
            buffer.setPipeline(it)
        })
        then:
        buffer.getPipelines().containsAll(pipelines)
        buffer.getPipelines().size() == pipelines.size()
    }

    def "updates pipelines when already present in team"() {
        given:
        def updatedPipeline = new Pipeline("NAME", "TEAM", new URL("http://UPDATED"))
        def pipelines = [new Pipeline("NAME", "TEAM", new URL("http://URL")),
                         updatedPipeline]
        when:
        pipelines.forEach({
            buffer.setPipeline(it)
        })
        then:
        buffer.getPipelines().size() == 1
        buffer.getPipelines().contains(updatedPipeline)
    }
}
