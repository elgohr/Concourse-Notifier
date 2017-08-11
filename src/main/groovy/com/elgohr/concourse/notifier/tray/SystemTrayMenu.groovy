package com.elgohr.concourse.notifier.tray

import groovy.util.logging.Slf4j

import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

@Slf4j
class SystemTrayMenu {

    def systemTray, trayIcon

    SystemTrayMenu() {
        this.systemTray = SystemTray
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
        trayIcon = new TrayIcon(image, "Close application")
        trayIcon.addActionListener(new CloseApplicationListener())
        trayIcon.setImageAutoSize true
    }

    TrayIcon getTrayIcon() {
        return trayIcon
    }

    static class CloseApplicationListener implements ActionListener {
        @Override
        void actionPerformed(ActionEvent e) {
            System.exit(0)
        }
    }
}
