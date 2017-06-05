package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.api.ConcourseServiceImpl
import com.elgohr.concourse.notifier.notifications.NotificationFactoryImpl
import com.elgohr.concourse.notifier.notifications.NotificationSchedulerImpl
import com.elgohr.concourse.notifier.settings.SettingsBuilder
import groovy.util.logging.Slf4j

import java.util.concurrent.Executors

@Slf4j
class ConcourseNotifier {

    static def settings
    def final notificationScheduler, notificationFactory, concourseService

    static void main(String[] args) {
        def (
        URL argUrl,
        Integer argCheckTime,
        Integer argNotificationDelay) = checkArguments(args)
        settings = initializeSettings(argUrl, argCheckTime, argNotificationDelay)
        new ConcourseNotifier(settings)
    }

    private static Settings initializeSettings(URL url,
                                               Integer checkTime,
                                               Integer notificationDelay) {
        def settingsBuilder = new SettingsBuilder()
        if (url != null) {
            settingsBuilder.url url
        } else {
            settingsBuilder.url new URL("https://ci.concourse.ci")
        }
        if (checkTime != null) {
            settingsBuilder.checkTime checkTime
        } else {
            settingsBuilder.checkTime 5
        }
        if (notificationDelay != null) {
            settingsBuilder.notificationDelay notificationDelay
        } else {
            settingsBuilder.notificationDelay 5
        }
        settings = settingsBuilder.build()
        settings
    }

    private static List checkArguments(String[] args) {
        def argUrl = null, argCheckTime = null, argNotificationDelay = null
        for (int i = 0; i < args.size(); i++) {
            if (args[i] == "-c") {
                try {
                    argUrl = new URL(args[i + 1])
                } catch (Exception e) {
                    log.error "URL must be an URL. Using the default (https://ci.concourse.ci)"
                }
            } else if (args[i] == "-t") {
                try {
                    argCheckTime = Integer.parseInt(args[i + 1])
                } catch (Exception e) {
                    log.error "Checktime must be given as an Integer. Using the default (5 seconds)"
                }
            } else if (args[i] == "-d") {
                try {
                    argNotificationDelay = Integer.parseInt(args[i + 1])
                } catch (Exception e) {
                    log.error "Notification delay must be given as an Integer. Using the default (5 seconds)"
                }
            }
        }
        [argUrl, argCheckTime, argNotificationDelay]
    }

    ConcourseNotifier(Settings settings) {
        concourseService = new ConcourseServiceImpl(settings)
        notificationFactory = new NotificationFactoryImpl()
        notificationScheduler = new NotificationSchedulerImpl(
                concourseService,
                notificationFactory,
                Executors.newScheduledThreadPool(1))
        notificationScheduler.startCheck()
    }

    static getSettings() {
        return settings
    }
}
