package com.elgohr.concourse.notifier.notifications

import spock.lang.Specification

import javax.swing.JDialog
import java.awt.GraphicsConfiguration
import java.awt.GraphicsDevice
import java.awt.Insets
import java.awt.Rectangle
import java.awt.Toolkit

class NotificationViewSpec extends Specification {

    def "creates multiple notifications for visual check"() {
        when:
        new NotificationView("PIPELINE", "JOB_NAME", "succeeded", 1, 1000)
                .showNotification()
        new NotificationView("PIPELINE", "JOB_NAME", "succeeded", 2, 1000)
                .showNotification()
        then:
        sleep 2000
    }

    def "calls callbacks"() {
        given:
        def calledFirst = false
        def calledSecond = false
        when:
        new NotificationView("PIPELINE", "JOB_NAME", "succeeded", 1, 1)
                .showNotification({calledFirst = true})
        new NotificationView("PIPELINE", "JOB_NAME", "succeeded", 2, 1)
                .showNotification({calledSecond = true})
        sleep 100
        then:
        calledFirst
        calledSecond
    }



}
