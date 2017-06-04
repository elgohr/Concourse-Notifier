package com.elgohr.concourse.notifier.notifications

import spock.lang.Specification

import javax.swing.JDialog
import java.awt.GraphicsConfiguration
import java.awt.GraphicsDevice
import java.awt.Insets
import java.awt.Rectangle
import java.awt.Toolkit

class NotificationViewSpec extends Specification {

    def "creates Notification"() {

        given:
        new NotificationView("PIPELINE", "JOB_NAME", "succeeded", 1)
                .showNotification()
        new NotificationView("PIPELINE", "JOB_NAME", "succeeded", 2)
                .showNotification()
        sleep(6000)
    }

    def "shows multiple notifications without overlapping"() {
        given:
        def rectangle = Mock(Rectangle)
        def configuration = Mock(GraphicsConfiguration)
        configuration.getBounds() >> rectangle
        def screen = Mock(GraphicsDevice)
        screen.getDefaultConfiguration() >> configuration

        def toolkit = Mock(Toolkit)
        def component = Mock(JDialog)

        def screenWidth = 1024
        def screenHeight = 768

        def componentWidth = 1
        def marginRight = 5

        def componentHeight = 10
        def taskbarHeight = 75
        toolkit.getScreenInsets(_) >> new Insets(0, 0, taskbarHeight, 0)

        def marginBottom = 5
        def numberOfComponents = 3

        when:
        def position = NotificationView.getPosition(screen, toolkit, component, numberOfComponents)

        then:
        1 * rectangle.getMaxY() >> screenHeight
        1 * rectangle.getMaxX() >> screenWidth
        1 * component.getWidth() >> componentWidth
        1 * component.getHeight() >> componentHeight
        def width = screenWidth - (componentWidth + marginRight)
        def height = screenHeight - taskbarHeight - (componentHeight + marginBottom) * numberOfComponents
        position == [width, height]
    }

}
