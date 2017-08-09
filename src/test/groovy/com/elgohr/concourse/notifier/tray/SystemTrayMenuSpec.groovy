package com.elgohr.concourse.notifier.tray

import org.codehaus.groovy.tools.shell.util.NoExitSecurityManager
import spock.lang.Specification

import java.awt.Desktop
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.ActionEvent

class SystemTrayMenuSpec extends Specification {

    def "creates menu for visual check"() {
        given:
        def systemTrayMenu = new SystemTrayMenu()
        when:
        systemTrayMenu.showMenu()
        sleep 3000
        then:
        true
    }

    def "contains exit button when systemtray is supported"() {
        given:
        def systemTraySpy = GroovySpy(SystemTray, global:true)

        def previousSecurityManager = System.getSecurityManager()
        def noExitSecurityManager = new NoExitSecurityManager()
        System.setSecurityManager noExitSecurityManager

        def systemTray = new SystemTrayMenu()
        systemTray.systemTray = systemTraySpy

        when:
        systemTray.showMenu()
        def exitButton = systemTray.getPopupMenu().getItem(0)
        exitButton.processActionEvent(new ActionEvent(new Object(), 1, ""))

        then:
        exitButton.getLabel() == "exit"
        thrown SecurityException

        1 * systemTraySpy.isSupported() >> true

        cleanup:
        System.setSecurityManager previousSecurityManager
    }

    def "contains no pop up menu when systemtray is not supported"() {
        given:
        def systemTraySpy = GroovySpy(SystemTray, global:true)
        def systemTray = new SystemTrayMenu()
        systemTray.systemTray = systemTraySpy

        when:
        systemTray.showMenu()

        then:
        systemTray.getPopupMenu() == null
        1 * systemTraySpy.isSupported() >> false
    }

}
