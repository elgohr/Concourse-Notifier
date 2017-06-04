package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.api.ConcourseServiceImpl
import com.elgohr.concourse.notifier.notifications.NotificationFactoryImpl
import com.elgohr.concourse.notifier.notifications.NotificationSchedulerImpl
import groovy.util.logging.Slf4j

import java.util.concurrent.Executors

@Slf4j
class ConcourseNotifier {

    static def settings
    def final notificationScheduler, notificationFactory, concourseService

    static void main(String[] args) {
        def settingsBuilder = new Settings.SettingsBuilder()
        settingsBuilder.url new URL("https://ci.concourse.ci")
        settingsBuilder.checkTime 5
        settingsBuilder.notificationDelay 5

        for (int i = 0; i < args.size(); i++) {
            if (args[i] == "-c") {
                try {
                    settingsBuilder.url new URL(args[i + 1])
                } catch (Exception e) {
                    log.error "URL must be an URL. Using the default (https://ci.concourse.ci)"
                }
            } else if (args[i] == "-t") {
                try {
                    settingsBuilder.checkTime Integer.parseInt(args[i + 1])
                } catch (Exception e) {
                    log.error "Checktime must be given as an Integer. Using the default (5 seconds)"
                }
            } else if (args[i] == "-d") {
                try {
                    settingsBuilder.notificationDelay Integer.parseInt(args[i + 1])
                } catch (Exception e) {
                    log.error "Notification delay must be given as an Integer. Using the default (5 seconds)"
                }
            }
        }
        settings = settingsBuilder.build()
        new ConcourseNotifier(settings)
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

}
