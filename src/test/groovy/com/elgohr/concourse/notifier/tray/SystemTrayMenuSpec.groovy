package com.elgohr.concourse.notifier.tray

import org.codehaus.groovy.tools.shell.util.NoExitSecurityManager
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
                .contains("CloseApplicationListener")
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

    def "CloseApplicationListener - closes the application when called"() {
        given:
        def previousSecurityManager = System.getSecurityManager()
        def noExitSecurityManager = new NoExitSecurityManager()
        System.setSecurityManager noExitSecurityManager

        def closeApplicationListener = new SystemTrayMenu.CloseApplicationListener()

        when:
        closeApplicationListener.actionPerformed(Mock(ActionEvent))

        then:
        thrown SecurityException

        cleanup:
        System.setSecurityManager previousSecurityManager
    }
}
