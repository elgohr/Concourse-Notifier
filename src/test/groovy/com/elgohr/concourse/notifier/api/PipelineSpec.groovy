package com.elgohr.concourse.notifier.api

import spock.lang.Specification


class PipelineSpec extends Specification {

    Pipeline pipeline

    void setup() {
        pipeline = new Pipeline("NAME", "TEAM", new URL("http://url"))
    }

    def "pipeline is not paused by default"() {
        when:
        def paused = pipeline.isPaused()
        then:
        !paused
    }

    def "pause - pauses pipeline"() {
        when:
        pipeline.pause()
        then:
        pipeline.isPaused()
    }

    def "resume - resumes pipeline"() {
        given:
        pipeline.paused = true
        when:
        pipeline.resume()
        then:
        !pipeline.isPaused()
    }
}
