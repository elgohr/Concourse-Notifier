package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.api.ConcourseServiceImpl
import com.elgohr.concourse.notifier.api.HttpClient
import com.elgohr.concourse.notifier.notifications.NotificationFactoryImpl
import com.elgohr.concourse.notifier.notifications.NotificationSchedulerImpl
import com.elgohr.concourse.notifier.settings.SettingsView
import com.elgohr.concourse.notifier.tray.SystemTrayMenu
import groovy.util.logging.Slf4j

import java.util.concurrent.Executors

@Slf4j
class ConcourseNotifier {

    def final notificationScheduler,
              notificationFactory,
              concourseService,
              systemTrayMenu,
              settings

    static void main(String... args) {
        def settings = new Settings(new Buffer())
        new ConcourseNotifier(
                args,
                settings,
                new SystemTrayMenu(settings))
    }

    ConcourseNotifier(String[] args,
                      Settings settings,
                      SystemTrayMenu systemTrayMenu) {
        this.settings = settings
        checkArguments(args)
        if (hasNoBaseUrl(args) || hasNoArguments(args)) {
            settings.getSettingsView().showSettings()
        }

        this.systemTrayMenu = systemTrayMenu
        systemTrayMenu.showIcon()
        concourseService = new ConcourseServiceImpl(settings, new HttpClient())
        notificationFactory = new NotificationFactoryImpl(settings)
        notificationScheduler = new NotificationSchedulerImpl(
                concourseService,
                notificationFactory,
                Executors.newScheduledThreadPool(1),
                new Buffer()
        )
        notificationScheduler.startCheck()
    }

    def checkArguments(String[] args) {
        for (int i = 0; i < args.size(); i++) {
            if (args[i] == "-c") {
                try {
                    settings.url = new URL(args[i + 1])
                } catch (Exception ignored) {
                    log.error "URL must be an URL. Using the default (https://ci.concourse.ci)"
                }
            } else if (args[i] == "-t") {
                try {
                    settings.checkTimeInSecs = Integer.parseInt(args[i + 1])
                } catch (Exception ignored) {
                    log.error "Checktime must be given as an Integer. Using the default (5 seconds)"
                }
            } else if (args[i] == "-d") {
                try {
                    settings.notificationTimeoutInSecs = Integer.parseInt(args[i + 1])
                } catch (Exception ignored) {
                    log.error "Notification delay must be given as an Integer. Using the default (5 seconds)"
                }
            }
        }
    }

    private static hasNoArguments(String[] args) {
        return args.length == 0
    }

    private static hasNoBaseUrl(String[] args) {
        return args.length > 0 && !args.contains("-c")
    }

}
