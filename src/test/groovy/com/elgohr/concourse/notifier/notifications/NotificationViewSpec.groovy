package com.elgohr.concourse.notifier.notifications

import spock.lang.Specification

class NotificationViewSpec extends Specification {

    def "creates multiple notifications for visual check"() {
        when:
        new NotificationView("PIPELINE", "JOB_NAME", "http://JOB_URL", "succeeded", 1, 1000)
                .showNotification()
        new NotificationView("PIPELINE", "JOB_NAME", "http://JOB_URL", "succeeded", 2, 1000)
                .showNotification()
        then:
        sleep 3000
    }

    def "calls callbacks"() {
        given:
        def calledFirst = false
        def calledSecond = false
        when:
        new NotificationView("PIPELINE", "JOB_NAME", "http://JOB_URL", "succeeded", 1, 1)
                .showNotification({ calledFirst = true })
        new NotificationView("PIPELINE", "JOB_NAME", "http://JOB_URL", "succeeded", 2, 1)
                .showNotification({ calledSecond = true })
        sleep 20
        then:
        calledFirst
        calledSecond
    }

    def "closes notifications after timeout"() {
        given:
        def notificationView = new NotificationView(
                "PIPELINE",
                "JOB_NAME",
                "http://JOB_URL",
                "succeeded",
                1,
                1)
        when:
        notificationView.showNotification({})
        sleep 20
        then:
        !notificationView.getDialog().isVisible()
    }

    def "calls NotificationClickListener on click"() {
        when:
        def notificationView = new NotificationView(
                "PIPELINE",
                "JOB_NAME",
                "http://JOB_URL",
                "succeeded",
                1,
                1)
        notificationView.showNotification()
        def dialog = notificationView.getDialog()
        then:
        dialog.getMouseListeners().toArrayString().contains("NotificationClickListener")
    }

    def "saves job url for NotificationClickListener"() {
        when:
        def notificationView = new NotificationView(
                "PIPELINE",
                "JOB_NAME",
                "http://JOB_URL",
                "succeeded",
                1,
                1)
        notificationView.showNotification()
        then:
        notificationView.notificationClickListener.uri == new URI("http://JOB_URL")
    }
}
