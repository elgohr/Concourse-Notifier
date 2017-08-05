package com.elgohr.concourse.notifier.settings

import com.elgohr.concourse.notifier.Settings
import com.elgohr.concourse.notifier.ViewUtil
import com.elgohr.concourse.notifier.notifications.ConcourseColors

import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingConstants
import javax.swing.WindowConstants
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dimension
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowEvent

import static java.util.Map.Entry

class SettingsView {

    private Settings settings
    private JFrame dialog
    private HashMap<String, JTextField> fields = [] as HashMap

    SettingsView(Settings settings) {
        this.settings = settings
    }

    def showSettings() {
        dialog = createMainView()
        addComponents(dialog.getContentPane())
        showDialog(dialog)
    }

    def addComponents(Container container) {
        addHeader(container)
        addContent(container)
    }

    static def showDialog(JFrame frame) {
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
                        1)
        )
        frame.setVisible true
    }

    def getDialog() {
        return dialog
    }

    private static def createMainView() {
        def dialog = new JFrame()
        dialog.setDefaultCloseOperation WindowConstants.HIDE_ON_CLOSE
        dialog.setUndecorated true
        dialog.setAlwaysOnTop true
        dialog.setFocusableWindowState true
        dialog.setPreferredSize new Dimension(300, 200)
        dialog.getContentPane().setBackground ConcourseColors.TITLEBAR_BACKGROUND
        dialog
    }

    private static void addHeader(Container container) {
        def header = new JPanel()
        header.setBackground ConcourseColors.TITLEBAR_BACKGROUND
        header.setLayout new BorderLayout()
        container.add header, BorderLayout.PAGE_START

        def headerLabel = new JLabel("settings", SwingConstants.CENTER)
        headerLabel.setForeground ConcourseColors.TEXT
        headerLabel.setFont new Font(Font.MONOSPACED, Font.PLAIN, 14)
        headerLabel.setBorder BorderFactory.createEmptyBorder(5, 5, 5, 5)
        header.add headerLabel, BorderLayout.CENTER
    }

    private void addContent(Container container) {
        def content = new JPanel()
        content.setBackground ConcourseColors.TITLEBAR_BACKGROUND
        content.setLayout new BoxLayout(content, BoxLayout.Y_AXIS)
        content.setBorder BorderFactory.createEmptyBorder(10, 20, 20, 20)
        container.add content, BorderLayout.CENTER

        def basicOptions = ["url": settings.getUrl()]
        addOptions(content, basicOptions)

        def advancedOptions = ["interval": settings.getCheckTimeInSecs(),
                               "timeout" : settings.getNotificationTimeoutInSecs()]
        addOptions(content, advancedOptions)

        addSaveButton(content)
    }

    private void addSaveButton(JPanel content) {
        def saveButton = new JButton("save")
        saveButton.setBorder BorderFactory.createEmptyBorder(5, 5, 5, 5)
        saveButton.setForeground ConcourseColors.TEXT
        saveButton.setBackground ConcourseColors.BACKGROUND
        saveButton.addActionListener(new ActionListener() {
            void actionPerformed(ActionEvent e) {
                saveSettings()
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING))
            }
        })
        content.add saveButton
    }

    private void saveSettings() {
        for (def field in fields) {
            if (field.key == "url") {
                settings.setUrl new URL(field.value.getText())
            } else if (field.key == "interval") {
                settings.setCheckTimeInSecs Integer.parseInt(field.value.getText())
            } else if (field.key == "timeout") {
                settings.setNotificationTimeoutInSecs Integer.parseInt(field.value.getText())
            }
        }
    }

    private void addOptions(JPanel content, Map<String, Serializable> options) {
        for (def option in options) {
            def textField = createContentField(option)
            def labelField = createLabelField(option, textField)
            addOption(content, labelField, textField)
        }
    }

    private static void addOption(JPanel content,
                                  JLabel labelField,
                                  JTextField textField) {
        content.add labelField
        content.add textField
        content.add Box.createRigidArea(new Dimension(0, 10))
    }

    private JTextField createContentField(Entry<String, Serializable> option) {
        def textField = new JTextField(option.getValue().toString())
        textField.setForeground ConcourseColors.TEXT
        textField.setBackground ConcourseColors.BACKGROUND
        textField.setFont new Font(Font.MONOSPACED, Font.PLAIN, 12)
        textField.setBorder BorderFactory.createEmptyBorder()
        rememberTextFieldForSaving(option, textField)
        textField
    }

    private JTextField rememberTextFieldForSaving(Entry<String, Serializable> option, JTextField textField) {
        fields.put(option.getKey(), textField)
    }

    private static JLabel createLabelField(Entry<String, Serializable> option,
                                           JTextField textField) {
        def labelField = new JLabel(option.getKey(), SwingConstants.LEFT)
        labelField.setForeground ConcourseColors.TEXT
        labelField.setFont new Font(Font.MONOSPACED, Font.PLAIN, 12)
        labelField.setLabelFor textField
        labelField
    }

}
