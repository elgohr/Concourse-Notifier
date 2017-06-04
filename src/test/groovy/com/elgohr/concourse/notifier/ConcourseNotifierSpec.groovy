package com.elgohr.concourse.notifier

import spock.lang.Specification

class ConcourseNotifierSpec extends Specification {

    def "sets up Notifier"() {
        when:
        def settings = new Settings.SettingsBuilder()
                .url(new URL("http://url"))
                .checkTime(5)
                .notificationDelay(5)
                .build()
        def notifier = new ConcourseNotifier(settings)
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
        ConcourseNotifier.settings.getUrl().toString() == "http://url"
    }

    def "gets check-time from arguments"() {
        given:
        def args = ["-t", "5"] as String[]
        when:
        ConcourseNotifier.main(args)
        then:
        ConcourseNotifier.settings.getCheckTime() == 5
    }

    def "gets notification delay from arguments"() {
        given:
        def args = ["-d", "5"] as String[]
        when:
        ConcourseNotifier.main(args)
        then:
        ConcourseNotifier.settings.getNotificationDelay() == 5
    }

    def "prevents mis-usage of arguments"() {
        given:
        def args = ["-d", "d", "-t", "d", "-c", "NO_URL"] as String[]
        when:
        ConcourseNotifier.main(args)
        then:
        ConcourseNotifier.settings.getUrl().toString() == "https://ci.concourse.ci"
        ConcourseNotifier.settings.getCheckTime() == 5
        ConcourseNotifier.settings.getNotificationDelay() == 5
    }

}
