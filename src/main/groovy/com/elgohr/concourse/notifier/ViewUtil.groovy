package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.notifications.ConcourseColors

import java.awt.GraphicsDevice
import java.awt.Insets
import java.awt.Toolkit

class ViewUtil {

    static def getPosition(GraphicsDevice screen,
                           Toolkit toolkit,
                           def frame,
                           int numberOfComponents) {
        def screenBounds = screen.getDefaultConfiguration().getBounds()

        Insets insets = toolkit.getScreenInsets(screen.getDefaultConfiguration())
        int taskBarSize = insets.bottom

        final int x = (int) screenBounds.getMaxX() - frame.getWidth() - 5
        final int y = (int) screenBounds.getMaxY() - taskBarSize - (frame.getHeight() + 5) * numberOfComponents
        [x, y]
    }

    static def getColorByStatus(String status) {
        switch (status) {
            case "pending":
                return ConcourseColors.STATUS_PENDING
                break
            case "started":
                return ConcourseColors.STATUS_STARTED
                break
            case "failed":
                return ConcourseColors.STATUS_FAILED
                break
            case "errored":
                return ConcourseColors.STATUS_ERRORED
                break
            case "aborted":
                return ConcourseColors.STATUS_ABORTED
                break
            case "paused":
                return ConcourseColors.STATUS_PAUSED
                break
            case "succeeded":
                return ConcourseColors.STATUS_SUCCEEDED
                break
        }
    }
}
