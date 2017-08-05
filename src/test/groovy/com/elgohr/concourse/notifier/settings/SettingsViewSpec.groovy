package com.elgohr.concourse.notifier.settings

import com.elgohr.concourse.notifier.Settings
import spock.lang.Specification

import javax.swing.JButton

class SettingsViewSpec extends Specification {

    def "show settings for visual check"() {
        given:
        def settings = new Settings()
        def settingsView = new SettingsView(settings)
        when:
        settingsView.showSettings()
        then:
        sleep 2000
    }

    def "is shown with the right label"() {
        given:
        def settings = new Settings()
        def settingsView = new SettingsView(settings)
        when:
        settingsView.showSettings()
        then:
        def header = settingsView.getDialog().getContentPane().getComponents()[0]
        def settingsHeaderLabel = header.getComponents()[0]
        settingsHeaderLabel.getText() == "settings"
    }

    def "shows values of settings"() {
        given:
        def settings = new Settings()
        def settingsView = new SettingsView(settings)
        when:
        settingsView.showSettings()
        then:
        def content = settingsView.getDialog().getContentPane().getComponents()[1]
        content.getComponents()[0].getText() == "url"
        content.getComponents()[1].getText().toString() == "https://ci.concourse.ci"
        content.getComponents()[3].getText() == "interval"
        content.getComponents()[4].getText().toString() == "5"
        content.getComponents()[6].getText() == "timeout"
        content.getComponents()[7].getText().toString() == "5"
    }

    def "shows save button"() {
        given:
        def settings = new Settings()
        def settingsView = new SettingsView(settings)
        when:
        settingsView.showSettings()
        then:
        def content = settingsView.getDialog().getContentPane().getComponents()[1]
        content.getComponents()[9].getText().toString() == "save"
    }

    def "saves settings and hides window with button click"() {
        given:
        def settings = Spy(Settings)
        def settingsView = new SettingsView(settings)
        settingsView.showSettings()
        when:
        def content = settingsView.getDialog().getContentPane().getComponents()[1]
        JButton saveButton = content.getComponents()[9]
        saveButton.doClick()
        then:
        1 * settings.setUrl(new URL("https://ci.concourse.ci"))
        1 * settings.setCheckTimeInSecs(5)
        1 * settings.setNotificationTimeoutInSecs(5)
        !settingsView.getDialog().isShowing()
    }
}
