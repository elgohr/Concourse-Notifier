package com.elgohr.concourse.notifier.tray

import com.elgohr.concourse.notifier.Settings
import groovy.util.logging.Slf4j

import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

@Slf4j
class SystemTrayMenu {

    def systemTray, trayIcon, settings

    SystemTrayMenu(Settings settings) {
        this.systemTray = SystemTray
        this.settings = settings
    }

    def showIcon() {
        try {
            if (systemTray.isSupported()) {
                loadTrayIcon()
                systemTray.getSystemTray().add(trayIcon)
            } else {
                log.info "No support for tray icons on this system."
            }
        } catch (Exception ignored) {
            log.error "TrayIcon could not be added."
        }
    }

    private void loadTrayIcon() {
        def imageResource = getClass().getResource("/tray_icon.png")
        def image = Toolkit.getDefaultToolkit().getImage(imageResource)
        trayIcon = new TrayIcon(image, "Concourse Notifier")
        trayIcon.addActionListener(new OpenSettingViewListener(settings))
        trayIcon.setImageAutoSize true
    }

    TrayIcon getTrayIcon() {
        return trayIcon
    }

    static class OpenSettingViewListener implements ActionListener {

        Settings settings

        OpenSettingViewListener(Settings settings) {
            this.settings = settings
        }

        @Override
        void actionPerformed(ActionEvent e) {
            settings.getSettingsView().showSettings()
        }
    }

}
