package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.settings.SettingsView
import spock.lang.Specification

class ConcourseNotifierSpec extends Specification {

    def "sets up Notifier"() {
        given:
        def args = ["-c", "http://url",
                    "-t", "5",
                    "-d", "6"] as String[]

        when:
        def notifier = new ConcourseNotifier(args, new Settings(), new SettingsView(new Settings()))
        then:
        notifier.notificationFactory != null
        notifier.concourseService != null
        notifier.notificationScheduler != null
        notifier.getSettings().getUrl().toString() == "http://url"
        notifier.getSettings().getCheckTimeInSecs() == 5
        notifier.getSettings().getNotificationTimeoutInSecs() == 6
    }

    def "prevents mis-usage of arguments"() {
        given:
        def args = ["-d", "d",
                    "-t", "d",
                    "-c", "NO_URL"] as String[]
        when:
        def notifier = new ConcourseNotifier(args, new Settings(), new SettingsView(new Settings()))
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
        new ConcourseNotifier(args, settingsSpy, settingsViewSpy)
        then:
        1 * settingsViewSpy.showSettings()
        0 * settingsSpy.setUrl(_)
        0 * settingsSpy.setCheckTimeInSecs(_)
        0 * settingsSpy.setNotificationTimeoutInSecs(_)
    }

    def "hides settings view if arguments given"() {
        given:
        def args = ["-c", "http://url"] as String[]
        def settingsSpy = Spy(Settings)
        def settingsViewSpy = Mock(SettingsView, args: [settingsSpy])
        when:
        new ConcourseNotifier(args, settingsSpy, settingsViewSpy)
        then:
        0 * settingsViewSpy.showSettings()
    }

}
