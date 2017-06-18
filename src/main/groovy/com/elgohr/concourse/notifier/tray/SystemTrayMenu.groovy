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

    def popupMenu

    def showMenu() {
        popupMenu = new PopupMenu()
        popupMenu.setFont new Font(Font.MONOSPACED, Font.PLAIN, 12)

        def imageResource = getClass().getResource("/tray_icon.png")
        def image = Toolkit.getDefaultToolkit().getImage(imageResource)
        def trayIcon = new TrayIcon(image)
        trayIcon.setImageAutoSize true

        def exitItem = new MenuItem("exit")
        exitItem.addActionListener(new ActionListener() {
            @Override
            void actionPerformed(ActionEvent e) {
                System.exit(0)
            }
        })
        popupMenu.add exitItem
        trayIcon.setPopupMenu(popupMenu)
        try {
            if (SystemTray.isSupported()) {
                def tray = SystemTray.getSystemTray()
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
