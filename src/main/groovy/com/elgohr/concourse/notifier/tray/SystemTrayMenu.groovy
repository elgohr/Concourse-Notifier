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

    def popupMenu, systemTray, trayIcon

    SystemTrayMenu() {
        systemTray = SystemTray
        def popUpMenu = loadPopUpMenu()
        loadTrayIcon(popUpMenu)
    }

    private PopupMenu loadPopUpMenu() {
        popupMenu = new PopupMenu()
        popupMenu.setFont new Font(Font.MONOSPACED, Font.PLAIN, 12)

        def exitItem = new MenuItem("exit")
        exitItem.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                System.exit(0)
            }
        })
        popupMenu.add exitItem
        popupMenu
    }

    private void loadTrayIcon(PopupMenu popupMenu) {
        def imageResource = getClass().getResource("/tray_icon.png")
        def image = Toolkit.getDefaultToolkit().getImage(imageResource)
        trayIcon = new TrayIcon(image, "Settings", popupMenu)
        trayIcon.setImageAutoSize true
    }

    def showMenu() {
        try {
            if (systemTray.isSupported()) {
                systemTray.getSystemTray().add(trayIcon)
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
