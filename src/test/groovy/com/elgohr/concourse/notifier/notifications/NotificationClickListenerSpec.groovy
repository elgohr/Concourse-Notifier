package com.elgohr.concourse.notifier.notifications

import spock.lang.Specification

import java.awt.Desktop
import java.awt.event.MouseEvent

class NotificationClickListenerSpec extends Specification {

    def "opens uri on click"() {
        given:
        def spyDesktop = GroovySpy(Desktop, global:true)
        def uri = new URI("http://www.google.de")
        def notificationClickListener = new NotificationClickListener(uri)
        notificationClickListener.desktop = spyDesktop

        when:
        notificationClickListener.mouseClicked(Mock(MouseEvent))

        then:
        1 * spyDesktop.isDesktopSupported() >> true
        1 * spyDesktop.isSupported(Desktop.Action.BROWSE) >> true
        1 * spyDesktop.browse(uri) >> {}
    }

    def "does nothing if no desktop is not supported"() {
        given:
        def spyDesktop = GroovySpy(Desktop, global:true)
        def uri = new URI("http://www.google.de")
        def notificationClickListener = new NotificationClickListener(uri)
        notificationClickListener.desktop = spyDesktop

        when:
        notificationClickListener.mouseClicked(Mock(MouseEvent))

        then:
        1 * spyDesktop.isDesktopSupported() >> false
        0 * spyDesktop.isSupported(Desktop.Action.BROWSE)
        0 * spyDesktop.browse(_) >> {}
    }

    def "does nothing if browsing is not supported"() {
        given:
        def spyDesktop = GroovySpy(Desktop, global:true)
        def uri = new URI("http://www.google.de")
        def notificationClickListener = new NotificationClickListener(uri)
        notificationClickListener.desktop = spyDesktop

        when:
        notificationClickListener.mouseClicked(Mock(MouseEvent))

        then:
        1 * spyDesktop.isDesktopSupported() >> true
        1 * spyDesktop.isSupported(Desktop.Action.BROWSE) >> false
        0 * spyDesktop.browse(_) >> {}
    }

}
