package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.notifications.ConcourseColors
import spock.lang.Specification

import javax.swing.JDialog
import java.awt.GraphicsConfiguration
import java.awt.GraphicsDevice
import java.awt.Insets
import java.awt.Rectangle
import java.awt.Toolkit

class ViewUtilSpec extends Specification {

    def "getPosition - shows multiple notifications without overlapping"() {
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
        def position = ViewUtil.getPosition(screen, toolkit, component, numberOfComponents)

        then:
        1 * rectangle.getMaxY() >> screenHeight
        1 * rectangle.getMaxX() >> screenWidth
        1 * component.getWidth() >> componentWidth
        1 * component.getHeight() >> componentHeight
        def width = screenWidth - (componentWidth + marginRight)
        def height = screenHeight - taskbarHeight - (componentHeight + marginBottom) * numberOfComponents
        position == [width, height]
    }

    def "getColorByStatus - gets color by status string"() {
        when:
        def result = ViewUtil.getColorByStatus(colorString)
        then:
        result == expectedResult
        where:
        colorString | expectedResult
        "pending"   | ConcourseColors.STATUS_PENDING
        "started"   | ConcourseColors.STATUS_STARTED
        "failed"    | ConcourseColors.STATUS_FAILED
        "errored"   | ConcourseColors.STATUS_ERRORED
        "aborted"   | ConcourseColors.STATUS_ABORTED
        "paused"    | ConcourseColors.STATUS_PAUSED
        "succeeded" | ConcourseColors.STATUS_SUCCEEDED
        ""          | null
    }
}
