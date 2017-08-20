package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.settings.SettingsView

class Settings {

    URL url = new URL("https://ci.concourse.ci")
    Buffer buffer
    int checkTimeInSecs = 5, notificationTimeoutInSecs = 5

    private SettingsView settingsView

    Settings(Buffer buffer) {
        this.buffer = buffer
    }

    def getSettingsView() {
        if (settingsView == null) {
            settingsView = new SettingsView(this)
        }
        settingsView
    }
}
