package com.elgohr.concourse.notifier

import spock.lang.Specification

class ConcourseNotifierSpec extends Specification {

    def "sets up Notifier"() {
        when:
        def notifier = new ConcourseNotifier(new URL("http://ANY"))
        then:
        notifier.notificationFactory != null
        notifier.concourseService != null
        notifier.notificationScheduler != null
    }

    def "gets url from arguments"() {
        given:
        def args = ["-c", "http://url"] as String[]
        when:
        ConcourseNotifier.main(args)
        then:
        ConcourseNotifier.url.toString() == "http://url"
    }

    def "gets check-time from arguments"() {
        given:
        def args = ["-t", "5"] as String[]
        when:
        ConcourseNotifier.main(args)
        then:
        ConcourseNotifier.checkTime == 5
    }

    def "gets notification delay from arguments"() {
        given:
        def args = ["-d", "5"] as String[]
        when:
        ConcourseNotifier.main(args)
        then:
        ConcourseNotifier.notificationDelay == 5
    }

    def "prevents mis-usage of arguments"() {
        given:
        def args = ["-d", "d", "-t", "d", "-c", "NO_URL"] as String[]
        when:
        ConcourseNotifier.main(args)
        then:
        ConcourseNotifier.url.toString() == "https://ci.concourse.ci"
        ConcourseNotifier.checkTime == 5
        ConcourseNotifier.notificationDelay == 5
    }

}
