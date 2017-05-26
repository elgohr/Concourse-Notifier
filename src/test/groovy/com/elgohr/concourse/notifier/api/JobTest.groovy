package com.elgohr.concourse.notifier.api

import spock.lang.Specification

class JobTest extends Specification {

    def "uses pipeline and name to generate key"() {
        when:
        def job = new Job("NAME", "PIPELINE", null, null)
        then:
        job.getKey() == "PIPELINE.NAME"
    }

}
