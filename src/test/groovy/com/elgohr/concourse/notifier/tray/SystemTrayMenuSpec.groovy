package com.elgohr.concourse.notifier.tray

import org.codehaus.groovy.tools.shell.util.NoExitSecurityManager
import spock.lang.Specification

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

    def "contains exit button"() {
        given:
        def previousSecurityManager = System.getSecurityManager()
        def noExitSecurityManager = new NoExitSecurityManager()
        System.setSecurityManager noExitSecurityManager

        def systemTray = new SystemTrayMenu()

        when:
        systemTray.showMenu()
        def exitButton = systemTray.getPopupMenu().getItem(0)
        exitButton.processActionEvent(new ActionEvent(new Object(), 1, ""))

        then:
        exitButton.getLabel() == "exit"
        thrown SecurityException

        cleanup:
        System.setSecurityManager previousSecurityManager
    }

    def "is okay with systems which do not have a system tray"() {
        given:
        SystemTray.class.metaClass.static.getSystemTray = {
            throw new UnsupportedOperationException()
        }
        def trayMenu = new SystemTrayMenu()
        when:
        trayMenu.showMenu()
        then:
        trayMenu.getPopupMenu() != null
    }
}
