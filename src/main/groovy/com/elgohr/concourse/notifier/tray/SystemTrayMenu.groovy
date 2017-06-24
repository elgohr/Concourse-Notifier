package com.elgohr.concourse.notifier.tray

import groovy.util.logging.Slf4j

import java.awt.Font
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

@Slf4j
class SystemTrayMenu {

    def popupMenu, systemTray

    SystemTrayMenu() {
        systemTray = SystemTray
    }

    def showMenu() {
        popupMenu = new PopupMenu()
        popupMenu.setFont new Font(Font.MONOSPACED, Font.PLAIN, 12)

        def imageResource = getClass().getResource("/tray_icon.png")
        def image = Toolkit.getDefaultToolkit().getImage(imageResource)

        def exitItem = new MenuItem("exit")
        exitItem.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                System.exit(0)
            }
        })
        popupMenu.add exitItem

        try {
            if (systemTray.isSupported()) {
                def trayIcon = new TrayIcon(image)
                trayIcon.setImageAutoSize true
                trayIcon.setPopupMenu(popupMenu)

                def tray = systemTray.getSystemTray()
                tray.add(trayIcon)
            } else {
                log.info "No support for tray icons on this system."
            }
        } catch (Exception e) {
            log.error "TrayIcon could not be added."
        }
    }

    def getPopupMenu() {
        return popupMenu
    }
}
