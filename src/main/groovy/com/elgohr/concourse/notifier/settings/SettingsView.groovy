package com.elgohr.concourse.notifier.settings

import com.elgohr.concourse.notifier.CloseApplicationListener
import com.elgohr.concourse.notifier.Settings
import com.elgohr.concourse.notifier.ViewUtil
import com.elgohr.concourse.notifier.api.Pipeline
import com.elgohr.concourse.notifier.notifications.ConcourseColors
import groovy.util.logging.Slf4j

import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JTextField
import javax.swing.SwingConstants
import javax.swing.WindowConstants
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dimension
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.Point
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowEvent

import static java.util.Map.Entry

@Slf4j
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
        def final screen = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
        def final toolkit = Toolkit.getDefaultToolkit()
        def final numberOfComponents = 1
        def final position = ViewUtil.getPosition(screen, toolkit, frame, numberOfComponents) as Point
        frame.setLocation(position)
        frame.setVisible true
    }

    def getDialog() {
        return dialog
    }

    private def createMainView() {
        def dialog = new JFrame()
        dialog.setDefaultCloseOperation WindowConstants.HIDE_ON_CLOSE
        dialog.setUndecorated true
        dialog.setAlwaysOnTop true
        dialog.setFocusableWindowState true
        def numberOfPipelines = settings.getBuffer().getPipelines().size()
        dialog.setPreferredSize new Dimension(300, 180 + (numberOfPipelines * 30))
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
        content.setLayout new BoxLayout(content, BoxLayout.PAGE_AXIS)
        content.setBorder BorderFactory.createEmptyBorder(10, 20, 20, 20)
        container.add content, BorderLayout.CENTER

        def basicOptions = ["url": settings.getUrl()]
        addOptions(content, basicOptions)

        def pipelines = settings.getBuffer().getPipelines()
        addPipelines(content, pipelines)

        content.add Box.createRigidArea(new Dimension(0, 10))

        content.add getSaveButton()
        content.add Box.createRigidArea(new Dimension(0, 10))
        content.add getQuitButton()
    }

    private static void addPipelines(JPanel content, Collection<Pipeline> pipelines) {
        if (pipelines.size() > 0) {
            for (def pipeline in pipelines) {
                addSeparator(content)
                def pipelineLine = new JPanel()
                pipelineLine.setAlignmentX JPanel.LEFT_ALIGNMENT
                pipelineLine.setLayout new BorderLayout()
                pipelineLine.add createPipelineStateButton(pipeline), BorderLayout.WEST
                pipelineLine.add createPipelineField(pipeline), BorderLayout.CENTER

                content.add pipelineLine
            }
        }
    }

    private static void addSeparator(JPanel content) {
        content.add Box.createRigidArea(new Dimension(0, 5))
        content.add new JSeparator(JSeparator.HORIZONTAL)
        content.add Box.createRigidArea(new Dimension(0, 5))
    }

    private static JTextField createPipelineField(Pipeline pipeline) {
        def pipelineField = new JTextField(pipeline.getName())
        pipelineField.setForeground ConcourseColors.TEXT
        pipelineField.setBackground ConcourseColors.TITLEBAR_BACKGROUND
        pipelineField.setFont new Font(Font.MONOSPACED, Font.PLAIN, 12)
        pipelineField.setBorder BorderFactory.createEmptyBorder(0, 5, 0, 5)
        pipelineField
    }

    static def createPipelineStateButton(Pipeline pipeline) {
        if (pipeline.isPaused()) {
            def resumeListener = new ActionListener() {
                void actionPerformed(ActionEvent e) {
                    log.info "Resuming " + pipeline
                    pipeline.resume()
                }
            }
            return getButton("▶", resumeListener)
        } else {
            def pauseListener = new ActionListener() {
                void actionPerformed(ActionEvent e) {
                    log.info "Pausing " + pipeline
                    pipeline.pause()
                }
            }
            return getButton("||", pauseListener)
        }
    }

    private static JButton getButton(String content, ActionListener actionListener) {
        def button = new JButton(content)
        button.setBorder BorderFactory.createEmptyBorder(5, 5, 5, 5)
        button.setForeground ConcourseColors.TEXT
        button.setBackground ConcourseColors.BACKGROUND
        button.addActionListener(actionListener)
        return button
    }

    private static JButton getQuitButton() {
        return getButton("quit", new CloseApplicationListener())
    }

    private JButton getSaveButton() {
        def saveListener = new ActionListener() {
            void actionPerformed(ActionEvent e) {
                saveSettings()
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING))
            }
        }
        return getButton("save", saveListener)
    }

    private void saveSettings() {
        for (def field in fields) {
            if (field.key == "url") {
                settings.setUrl new URL(field.value.getText())
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
