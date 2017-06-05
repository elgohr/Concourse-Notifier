package com.elgohr.concourse.notifier.settings

import spock.lang.Specification

class SettingsBuilderSpec extends Specification {

    def "contains url"() {
        given:
        def settings = new SettingsBuilder()
                .url(new URL("http://URL"))
                .build()
        when:
        def url = settings.getUrl()
        then:
        url == new URL("http://URL")
    }

    def "contains checkTime"() {
        given:
        def settings = new SettingsBuilder()
                .checkTime(5)
                .build()
        when:
        def checkTime = settings.getCheckTime()
        then:
        checkTime == 5
    }

    def "contains notification delay"() {
        given:
        def settings = new SettingsBuilder()
                .notificationDelay(5)
                .build()
        when:
        def delay = settings.getNotificationDelay()
        then:
        delay == 5
    }
}
