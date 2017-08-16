package com.elgohr.concourse.notifier.tray

import com.elgohr.concourse.notifier.Settings
import com.elgohr.concourse.notifier.settings.SettingsView
import spock.lang.Specification

import java.awt.SystemTray
import java.awt.event.ActionEvent

class SystemTrayMenuSpec extends Specification {

    def "creates a tray icon for visual check"() {
        given:
        def systemTrayMenu = new SystemTrayMenu()
        when:
        systemTrayMenu.showIcon()
        sleep 2000
        then:
        true
    }

    def "tray icon closes the application when clicked"() {
        given:
        def systemTraySpy = GroovySpy(SystemTray, global: true)
        def systemTray = new SystemTrayMenu()
        systemTray.systemTray = systemTraySpy

        when:
        systemTray.showIcon()
        then:
        1 * systemTraySpy.isSupported() >> true

        def trayIcon = systemTray.getTrayIcon()
        trayIcon.getActionListeners().toArrayString()
                .contains("OpenSettingViewListener")
    }

    def "contains no tray icon when systemtray is not supported"() {
        given:
        def systemTraySpy = GroovySpy(SystemTray, global: true)
        def systemTray = new SystemTrayMenu()
        systemTray.systemTray = systemTraySpy

        when:
        systemTray.showIcon()

        then:
        1 * systemTraySpy.isSupported() >> false
        systemTray.getTrayIcon() == null
    }

    def "OpenSettingViewListener - opens settings on click"() {
        given:
        def settings = Mock(Settings)
        def settingsView = Mock(SettingsView)
        def closeApplicationListener = new SystemTrayMenu.OpenSettingViewListener(settings)

        when:
        closeApplicationListener.actionPerformed(Mock(ActionEvent))

        then:
        1 * settings.getSettingsView() >> settingsView
        1 * settingsView.showSettings()
    }

}
