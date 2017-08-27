package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.settings.SettingsView
import com.elgohr.concourse.notifier.tray.SystemTrayMenu
import spock.lang.Specification

class ConcourseNotifierSpec extends Specification {

    def "sets up Notifier"() {
        given:
        def args = ["-c", "http://url",
                    "-t", "5",
                    "-d", "6"] as String[]
        def systemTrayMock = Mock(SystemTrayMenu)
        def buffer = new Buffer()

        when:
        def notifier = new ConcourseNotifier(
                args,
                new Settings(buffer),
                systemTrayMock
        )
        then:
        notifier.notificationFactory != null
        notifier.concourseService != null
        notifier.notificationScheduler != null
        notifier.systemTrayMenu != null
        notifier.getSettings().getUrl().toString() == "http://url"
        notifier.getSettings().getCheckTimeInSecs() == 5
        notifier.getSettings().getNotificationTimeoutInSecs() == 6
        notifier.notificationScheduler.buffer == buffer

        1 * systemTrayMock.showIcon()
    }

    def "prevents mis-usage of arguments"() {
        given:
        def args = ["-d", "d",
                    "-t", "d",
                    "-c", "NO_URL"] as String[]

        when:
        def notifier = new ConcourseNotifier(args,
                new Settings(new Buffer()),
                Mock(SystemTrayMenu))

        then:
        notifier.getSettings().getUrl().toString() == "https://ci.concourse.ci"
        notifier.getSettings().getCheckTimeInSecs() == 5
        notifier.getSettings().getNotificationTimeoutInSecs() == 5
    }

    def "shows settings view if no arguments given"() {
        given:
        def args = [] as String[]
        def settingsSpy = Spy(Settings)
        def settingsViewSpy = Mock(SettingsView, args: [settingsSpy])
        when:
        new ConcourseNotifier(
                args,
                settingsSpy,
                Mock(SystemTrayMenu))
        then:
        1 * settingsSpy.getSettingsView() >> settingsViewSpy
        1 * settingsViewSpy.showSettings()
        0 * settingsSpy.setUrl(_)
        0 * settingsSpy.setCheckTimeInSecs(_)
        0 * settingsSpy.setNotificationTimeoutInSecs(_)
    }

    def "hides settings view if arguments given"() {
        given:
        def args = ["-c", "http://url"] as String[]
        def settingsSpy = Spy(Settings)
        when:
        new ConcourseNotifier(
                args,
                settingsSpy,
                Mock(SystemTrayMenu))
        then:
        0 * settingsSpy.getSettingsView()
    }

    def "shows settings view arguments given but no base-url (-c) present"() {
        given:
        def args = ["-x"] as String[]
        def settingsSpy = Spy(Settings)
        def settingsViewSpy = Mock(SettingsView, args: [settingsSpy])
        when:
        new ConcourseNotifier(
                args,
                settingsSpy,
                Mock(SystemTrayMenu))
        then:
        1 * settingsSpy.getSettingsView() >> settingsViewSpy
        1 * settingsViewSpy.showSettings()
    }

}
