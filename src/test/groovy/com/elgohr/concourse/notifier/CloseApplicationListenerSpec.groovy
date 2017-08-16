package com.elgohr.concourse.notifier

import org.codehaus.groovy.tools.shell.util.NoExitSecurityManager
import spock.lang.Specification

import java.awt.event.ActionEvent

class CloseApplicationListenerSpec extends Specification {

    def "closes the application when called"() {
        given:
        def previousSecurityManager = System.getSecurityManager()
        def noExitSecurityManager = new NoExitSecurityManager()
        System.setSecurityManager noExitSecurityManager

        def closeApplicationListener = new CloseApplicationListener()

        when:
        closeApplicationListener.actionPerformed(Mock(ActionEvent))

        then:
        thrown SecurityException

        cleanup:
        System.setSecurityManager previousSecurityManager
    }

}
