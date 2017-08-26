package com.elgohr.concourse.notifier.settings

import com.elgohr.concourse.notifier.Buffer
import com.elgohr.concourse.notifier.Settings
import com.elgohr.concourse.notifier.api.Pipeline
import org.codehaus.groovy.tools.shell.util.NoExitSecurityManager
import spock.lang.Specification

import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import java.awt.Component

class SettingsViewSpec extends Specification {

    def bufferMock, settings

    void setup() {
        bufferMock = Mock(Buffer)
        settings = new Settings(bufferMock)
        bufferMock.getPipelines() >>
                [new Pipeline("PIPELINE1", "TEAM", new URL("http://URL")),
                 new Pipeline("PIPELINE2", "TEAM1", new URL("http://URL")),
                 new Pipeline("PIPELINE2", "TEAM1", new URL("http://URL"))]
    }

    def "show settings for visual check"() {
        when:
        settings.getSettingsView().showSettings()
        then:
        sleep 3000
    }

    def "is shown with the right label"() {
        when:
        settings.getSettingsView().showSettings()
        then:
        def header = settings.getSettingsView()
                .getDialog().getContentPane().getComponents()[0]
        def settingsHeaderLabel = header.getComponents()[0]
        settingsHeaderLabel.getText() == "settings"
    }

    def "shows url in settings"() {
        when:
        settings.getSettingsView().showSettings()
        then:
        def content = getContent()
        findComponentByText(content.getComponents(), "url") != null
        findComponentByText(content.getComponents(), "https://ci.concourse.ci") != null
    }

    def "shows save button"() {
        when:
        settings.getSettingsView().showSettings()
        then:
        def content = settings.getSettingsView().getDialog()
                .getContentPane().getComponents()[1]
        findComponentByText(content.getComponents(), "save") != null
    }

    def "saves settings and hides window with button click"() {
        given:
        def settings = Spy(Settings, constructorArgs: [bufferMock])
        def settingsView = settings.getSettingsView()
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
        when:
        settings.getSettingsView().showSettings()
        then:
        def content = getContent()
        findComponentByText(content.getComponents(), "quit") != null
    }

    def "shuts down the application on quit button click"() {
        given:
        def previousSecurityManager = System.getSecurityManager()
        def noExitSecurityManager = new NoExitSecurityManager()
        System.setSecurityManager noExitSecurityManager

        def settings = Spy(Settings, constructorArgs: [bufferMock])
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

    def "shows pipelines"() {
        when:
        settings.getSettingsView().showSettings()
        then:
        2 * bufferMock.getPipelines() >>
                [new Pipeline("PIPELINE1", "TEAM", new URL("http://URL")),
                 new Pipeline("PIPELINE2", "TEAM1", new URL("http://URL"))]
        findComponentByText(getContent().getComponents(), "PIPELINE1") != null
        findComponentByText(getContent().getComponents(), "PIPELINE2") != null
    }

    def "shows button to pause pipeline when pipeline is unpaused"() {
        setup:
        def unpausedPipeline = new Pipeline("PIPELINE1", "TEAM", new URL("http://URL"))

        when:
        settings.getSettingsView().showSettings()

        then:
        bufferMock.getPipelines() >> [unpausedPipeline]
        findComponentByText(getContent().getComponents(), "||") != null
    }

    def "does not show button to unpause pipeline when pipeline is unpaused"() {
        setup:
        def unpausedPipeline = new Pipeline("PIPELINE1", "TEAM", new URL("http://URL"))

        when:
        settings.getSettingsView().showSettings()

        then:
        bufferMock.getPipelines() >> [unpausedPipeline]
        findComponentByText(getContent().getComponents(), "▶") == null
    }

    def "shows button to unpause pipelines when pipeline is paused"() {
        setup:
        def pausedPipeline = new Pipeline("PIPELINE1", "TEAM", new URL("http://URL"))
        pausedPipeline.pause()

        when:
        settings.getSettingsView().showSettings()

        then:
        bufferMock.getPipelines() >> [pausedPipeline]
        findComponentByText(getContent().getComponents(), "▶") != null
    }

    def "does not show button to pause pipeline when pipeline is paused"() {
        setup:
        def pausedPipeline = new Pipeline("PIPELINE1", "TEAM", new URL("http://URL"))
        pausedPipeline.pause()

        when:
        settings.getSettingsView().showSettings()

        then:
        bufferMock.getPipelines() >> [pausedPipeline]
        findComponentByText(getContent().getComponents(), "||") == null
    }

    private Object getContent() {
        settings.getSettingsView().getDialog().getContentPane().getComponents()[1]
    }

    def "handles view with no pipelines pipelines"() {
        when:
        settings.getSettingsView().showSettings()
        then:
        2 * bufferMock.getPipelines() >> []
        settings.getSettingsView()
                .getDialog().getContentPane().getComponents()[1] != null
    }

    private static Component findComponentByText(Component[] components, String text) {
        for (def component in components) {
            if ((component instanceof JTextField
                    || component instanceof JButton
                    || component instanceof JLabel)
                    && component.getText() == text) {
                return component
            } else if (component instanceof JPanel) {
                def nestedComponent = findComponentByText(component.getComponents(), text)
                if (nestedComponent != null) {
                    return nestedComponent
                }
            }
        }
        return null
    }
}
