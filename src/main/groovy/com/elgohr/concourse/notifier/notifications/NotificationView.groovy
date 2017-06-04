package com.elgohr.concourse.notifier.notifications

import javax.swing.BorderFactory
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dimension
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.awt.Insets
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.WindowEvent

class NotificationView {

    def static final NOTIFICATION_TIMEOUT = 5000
    def final pipeline, jobName, status, numberOfOpenNotifications

    NotificationView(String pipeline,
                     String jobName,
                     String status,
                     numberOfOpenNotifications) {
        this.pipeline = pipeline
        this.jobName = jobName
        this.status = status
        this.numberOfOpenNotifications = numberOfOpenNotifications
    }

    def showNotification(Closure callback = {}) {
        def dialog = createDialog()
        addComponents(dialog.getContentPane())
        showDialog(dialog)
        closeDialogAfterTimeout(dialog, callback)
    }

    static def createDialog() {
        def dialog = new JDialog()
        dialog.setDefaultCloseOperation JFrame.HIDE_ON_CLOSE
        dialog.setUndecorated true
        dialog.setAlwaysOnTop true
        dialog.setFocusableWindowState false
        dialog.setType Window.Type.POPUP
        dialog.setPreferredSize(new Dimension(300, 75))
        dialog.getContentPane().setBackground(ConcourseColors.TEXT)
        dialog
    }

    def addComponents(Container container) {
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

    def showDialog(def frame) {
        frame.pack()
        def screen = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
        def toolkit = Toolkit.getDefaultToolkit()
        frame.setLocation(
                getPosition(
                        screen,
                        toolkit,
                        frame,
                        numberOfOpenNotifications)
        )
        frame.setVisible(true)
    }

    static def closeDialogAfterTimeout(def frame, Closure callback) {
        new Timer().runAfter(NOTIFICATION_TIMEOUT, {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
            callback.run()
        })
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

}
