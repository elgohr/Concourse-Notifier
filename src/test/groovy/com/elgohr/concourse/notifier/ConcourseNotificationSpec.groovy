package com.elgohr.concourse.notifier

import spock.lang.Specification

class ConcourseNotificationSpec extends Specification {

    def "sets up Notifier"() {
        when:
        def notifier = new ConcourseNotifier(new URL("http://ANY"))
        then:
        notifier.notificationFactory != null
        notifier.concourseService != null
        notifier.notificationScheduler != null
    }


}
