package com.elgohr.concourse.notifier.settings

import com.elgohr.concourse.notifier.Settings
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy

@Builder(builderStrategy = ExternalStrategy, forClass = Settings)
class SettingsBuilder {
}
