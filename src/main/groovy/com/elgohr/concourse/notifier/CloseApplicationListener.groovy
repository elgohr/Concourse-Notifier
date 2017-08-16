package com.elgohr.concourse.notifier

import java.awt.event.ActionEvent
import java.awt.event.ActionListener

 class CloseApplicationListener implements ActionListener {
    @Override
    void actionPerformed(ActionEvent e) {
        System.exit(0)
    }
}
