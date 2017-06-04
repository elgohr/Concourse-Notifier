package com.elgohr.concourse.notifier.notifications

import spock.lang.Specification

class NotificationViewSpec extends Specification {

    def "creates Notification"() {
        given:
        new NotificationView("PIPELINE", "JOB_NAME", "succeeded")
    }

}
