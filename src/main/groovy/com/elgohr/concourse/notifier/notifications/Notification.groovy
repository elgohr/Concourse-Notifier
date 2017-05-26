package com.elgohr.concourse.notifier.notifications

import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.event.WindowEvent

class Notification {

    def static final NOTIFICATION_TIMEOUT = 5000
    def final title, content, status

    Notification(String title, String content, String status) {
        this.title = title
        this.content = content
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
        def border = BorderFactory.createMatteBorder(1, 1, 1, 1, ConcourseColors.getTitleBarColor())
        contentPanel.setBorder(border)
        contentPanel.setBackground(ConcourseColors.getBackgroundColor())
        contentPanel.setLayout(new BorderLayout())
        container.add(contentPanel)

        def titleLabel = new JLabel(title)
        titleLabel.setForeground(ConcourseColors.getTextColor())
        titleLabel.setBackground(ConcourseColors.getTitleBarColor())
        titleLabel.setOpaque(true)
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5))
        contentPanel.add(titleLabel, BorderLayout.PAGE_START)

        def contentLabel = new JLabel(content)
        contentLabel.setForeground(ConcourseColors.getTextColor())
        setBackgroundColorByStatus(contentLabel)
        contentPanel.add(contentLabel, BorderLayout.CENTER)
    }

    def closeFrameAfterTimeout(JFrame frame) {
        sleep(NOTIFICATION_TIMEOUT)
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
    }

    def showFrame(JFrame frame) {
        frame.pack()
        frame.setLocation(getPosition(frame))
        frame.setVisible(true)
    }

    def getMainFrame() {
        def frame = new JFrame()
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)
        frame.setUndecorated(true)
        frame.setPreferredSize(new Dimension(300, 150))
        frame.getContentPane().setBackground(ConcourseColors.getTextColor())
        frame
    }

    def setBackgroundColorByStatus(JLabel label) {
        label.setOpaque(true)
        switch (status) {
            case "pending":
                label.setBackground(ConcourseColors.getPendingColor())
                break
            case "started":
                label.setBackground(ConcourseColors.getStartedColor())
                break
            case "failed":
                label.setBackground(ConcourseColors.getFailedColor())
                break
            case "errored":
                label.setBackground(ConcourseColors.getErroredColor())
                break
            case "aborted":
                label.setBackground(ConcourseColors.getAbortedColor())
                break
            case "paused":
                label.setBackground(ConcourseColors.getPausedColor())
                break
            case "succeeded":
                label.setBackground(ConcourseColors.getSucceededColor())
                break
        }
    }

    def getPosition(JFrame frame) {
        def screen = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
        def screenBounds = screen.getDefaultConfiguration().getBounds()
        final int x = (int) screenBounds.getMaxX() - frame.getWidth()
        final int y = (int) screenBounds.getMaxY() - frame.getHeight()
        [x, y]
    }


}
