package com.elgohr.concourse.notifier.notifications

import spock.lang.Specification

class NotificationSpec extends Specification {

    def "creates Notification"() {
        given:
        new Notification("PIPELINE", "JOB_NAME", "succeeded")
    }

}
