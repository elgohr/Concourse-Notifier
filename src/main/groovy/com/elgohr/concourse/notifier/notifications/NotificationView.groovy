package com.elgohr.concourse.notifier.notifications

import com.elgohr.concourse.notifier.ViewUtil

import javax.swing.BorderFactory
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dimension
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.WindowEvent

class NotificationView {

    def final pipeline, jobName,
              status, numberOfOpenNotifications,
              timeoutInMillis

    NotificationView(String pipeline,
                     String jobName,
                     String status,
                     int numberOfOpenNotifications,
                     int timeoutInMillis) {
        this.pipeline = pipeline
        this.jobName = jobName
        this.status = status
        this.numberOfOpenNotifications = numberOfOpenNotifications
        this.timeoutInMillis = timeoutInMillis
    }

    def showNotification(Closure callback = {}) {
        def dialog = createDialog()
        addComponents(dialog.getContentPane())
        showDialog(dialog)
        closeDialogAfterTimeout(dialog, callback)
    }

    static def createDialog() {
        def dialog = new JDialog()
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
        def backgroundColor = ViewUtil.getColorByStatus(status)
        def border = BorderFactory.createMatteBorder(5, 5, 5, 5, backgroundColor)
        contentPanel.setBorder border
        contentPanel.setBackground ConcourseColors.BACKGROUND
        contentPanel.setLayout new BorderLayout()
        container.add contentPanel

        def contentLabel = new JLabel("$pipeline - $jobName")
        contentLabel.setForeground ConcourseColors.TEXT
        contentLabel.setFont new Font(Font.MONOSPACED, Font.PLAIN, 12)
        contentLabel.setBorder BorderFactory.createEmptyBorder(5, 5, 5, 5)
        contentPanel.add contentLabel, BorderLayout.CENTER
    }

    def showDialog(def frame) {
        frame.pack()
        def screen = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
        def toolkit = Toolkit.getDefaultToolkit()
        frame.setLocation(
                ViewUtil.getPosition(
                        screen,
                        toolkit,
                        frame,
                        numberOfOpenNotifications)
        )
        frame.setVisible(true)
    }

    def closeDialogAfterTimeout(def frame, Closure callback) {
        new Timer().runAfter(timeoutInMillis, {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
            callback.run()
        })
    }

}
