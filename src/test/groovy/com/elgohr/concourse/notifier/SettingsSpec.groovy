package com.elgohr.concourse.notifier

import spock.lang.Specification


class SettingsSpec extends Specification {

    def "contains default values"() {
        when:
        def settings = new Settings()
        then:
        settings.url == new URL("https://ci.concourse.ci")
        settings.checkTimeInSecs == 5
        settings.notificationTimeoutInSecs == 5
    }

    def "returns view for current settings"() {
        given:
        def settings = new Settings()
        when:
        def settingsView = settings.getSettingsView()
        then:
        settingsView != null
    }

    def "reuses the settings view"() {
        given:
        def settings = new Settings()
        when:
        def settingsView1 = settings.getSettingsView()
        def settingsView2 = settings.getSettingsView()
        then:
        settingsView1 == settingsView2
    }
}
