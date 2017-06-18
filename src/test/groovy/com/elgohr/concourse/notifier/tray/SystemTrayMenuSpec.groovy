package com.elgohr.concourse.notifier.tray

import org.codehaus.groovy.tools.shell.util.NoExitSecurityManager
import spock.lang.Specification

import java.awt.event.ActionEvent

class SystemTrayMenuSpec extends Specification {

    def "creates menu for visual check"() {
        when:
        new SystemTrayMenu().showMenu()
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
}
