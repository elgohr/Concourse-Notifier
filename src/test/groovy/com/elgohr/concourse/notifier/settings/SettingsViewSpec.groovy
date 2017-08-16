package com.elgohr.concourse.notifier.settings

import com.elgohr.concourse.notifier.Settings
import com.elgohr.concourse.notifier.tray.SystemTrayMenu
import org.codehaus.groovy.tools.shell.util.NoExitSecurityManager
import spock.lang.Specification

import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JTextField
import java.awt.Component
import java.awt.event.ActionEvent

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

    def "shows url in settings"() {
        given:
        def settings = new Settings()
        def settingsView = new SettingsView(settings)
        when:
        settingsView.showSettings()
        then:
        def content = settingsView.getDialog().getContentPane().getComponents()[1]
        findComponentByText(content.getComponents(), "url") != null
        findComponentByText(content.getComponents(), "https://ci.concourse.ci") != null
    }

    def "shows save button"() {
        given:
        def settings = new Settings()
        def settingsView = new SettingsView(settings)
        when:
        settingsView.showSettings()
        then:
        def content = settingsView.getDialog().getContentPane().getComponents()[1]
        findComponentByText(content.getComponents(), "save") != null
    }

    def "saves settings and hides window with button click"() {
        given:
        def settings = Spy(Settings)
        def settingsView = new SettingsView(settings)
        settingsView.showSettings()
        when:
        def content = settingsView.getDialog().getContentPane().getComponents()[1]
        JButton saveButton = findComponentByText(content.getComponents(), "save")
        saveButton.doClick()
        then:
        1 * settings.setUrl(new URL("https://ci.concourse.ci"))
        !settingsView.getDialog().isShowing()
    }

    def "shows quit button"() {
        given:
        def settings = new Settings()
        def settingsView = new SettingsView(settings)
        when:
        settingsView.showSettings()
        then:
        def content = settingsView.getDialog().getContentPane().getComponents()[1]
        findComponentByText(content.getComponents(), "quit") != null
    }

    def "shuts down the application on quit button click"() {
        given:
        def previousSecurityManager = System.getSecurityManager()
        def noExitSecurityManager = new NoExitSecurityManager()
        System.setSecurityManager noExitSecurityManager

        def settings = Spy(Settings)
        def settingsView = new SettingsView(settings)
        settingsView.showSettings()
        when:
        def content = settingsView.getDialog().getContentPane().getComponents()[1]
        JButton quitButton = findComponentByText(content.getComponents(), "quit")
        quitButton.doClick()
        then:
        thrown SecurityException

        cleanup:
        System.setSecurityManager previousSecurityManager
    }

    private static Component findComponentByText(Component[] components, String text) {
        for (def component in components) {
            if ((component instanceof JTextField
                    || component instanceof JButton
                    || component instanceof JLabel)
                    && component.getText() == text) {
                return component
            }
        }
        return null
    }
}
