package com.elgohr.concourse.notifier.settings

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy

class Settings {

    URL url
    int checkTime, notificationDelay

    @Builder(builderStrategy = ExternalStrategy, forClass = Settings)
    static class SettingsBuilder {}

}
