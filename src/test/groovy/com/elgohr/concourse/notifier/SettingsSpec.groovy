package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.Settings
import spock.lang.Specification

class SettingsSpec extends Specification {

    def "contains url" () {
        given:
        def settings = new Settings.SettingsBuilder().url(new URL("http://URL")).build()
        when:
        def url = settings.getUrl()
        then:
        url == new URL("http://URL")
    }

    def "contains checkTime" () {
        given:
        def settings = new Settings.SettingsBuilder().checkTime(5).build()
        when:
        def checkTime = settings.getCheckTime()
        then:
        checkTime == 5
    }

    def "contains notification delay" () {
        given:
        def settings = new Settings.SettingsBuilder().notificationDelay(5).build()
        when:
        def delay = settings.getNotificationDelay()
        then:
        delay == 5
    }
}
