package com.elgohr.concourse.notifier.notifications

import java.awt.Desktop
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class NotificationClickListener implements MouseListener {

    def desktop, uri

    NotificationClickListener(URI uri) {
        this.desktop = Desktop
        this.uri = uri
    }

    @Override
    void mouseClicked(MouseEvent e) {
        if (environmentSupportsBrowsing()) {
            desktop.getDesktop().browse(uri)
        }
    }

    private boolean environmentSupportsBrowsing() {
        desktop.isDesktopSupported() && desktop.getDesktop().isSupported(Desktop.Action.BROWSE)
    }

    @Override
    void mousePressed(MouseEvent e) {

    }

    @Override
    void mouseReleased(MouseEvent e) {

    }

    @Override
    void mouseEntered(MouseEvent e) {

    }

    @Override
    void mouseExited(MouseEvent e) {

    }
}
