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
}
