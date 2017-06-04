package com.elgohr.concourse.notifier.notifications

import javax.swing.BorderFactory
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.Window
import java.awt.event.WindowEvent

class NotificationView {

    def static final NOTIFICATION_TIMEOUT = 5000
    def final pipeline, jobName, status

    NotificationView(String pipeline, String jobName, String status) {
        this.pipeline = pipeline
        this.jobName = jobName
        this.status = status
        showNotification()
    }

    def showNotification() {
        def frame = getMainFrame()
        addComponentsToFrame(frame.getContentPane())
        showFrame(frame)
        closeFrameAfterTimeout(frame)
    }

    def addComponentsToFrame(Container container) {
        def contentPanel = new JPanel()
        def backgroundColor = getBackgroundColorByStatus()
        def border = BorderFactory.createMatteBorder(5, 5, 5, 5, backgroundColor)
        contentPanel.setBorder(border)
        contentPanel.setBackground(ConcourseColors.BACKGROUND)
        contentPanel.setLayout(new BorderLayout())
        container.add(contentPanel)

        def contentLabel = new JLabel("$pipeline - $jobName")
        contentLabel.setForeground(ConcourseColors.TEXT)
        contentLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5))
        contentPanel.add(contentLabel, BorderLayout.CENTER)
    }

    def closeFrameAfterTimeout(def frame) {
        sleep(NOTIFICATION_TIMEOUT)
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
    }

    def showFrame(def frame) {
        frame.pack()
        frame.setLocation(getPosition(frame))
        frame.setVisible(true)
    }

    def getMainFrame() {
        def frame = new JDialog()
        frame.setDefaultCloseOperation JFrame.HIDE_ON_CLOSE
        frame.setUndecorated true
        frame.setAlwaysOnTop true
        frame.setFocusableWindowState false
        frame.setType Window.Type.UTILITY
        frame.setPreferredSize(new Dimension(300, 75))
        frame.getContentPane().setBackground(ConcourseColors.TEXT)
        frame
    }

    def getBackgroundColorByStatus() {
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

    def getPosition(def frame) {
        def screen = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
        def screenBounds = screen.getDefaultConfiguration().getBounds()
        final int x = (int) screenBounds.getMaxX() - frame.getWidth() - 5
        final int y = (int) screenBounds.getMaxY() - frame.getHeight() - 30
        [x, y]
    }

}
