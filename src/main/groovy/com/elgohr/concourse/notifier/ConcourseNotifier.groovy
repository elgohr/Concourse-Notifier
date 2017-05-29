package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.api.ConcourseServiceImpl
import com.elgohr.concourse.notifier.notifications.NotificationFactoryImpl
import com.elgohr.concourse.notifier.notifications.NotificationSchedulerImpl
import groovy.util.logging.Slf4j

@Slf4j
class ConcourseNotifier {

    def static url, checkTime, notificationDelay
    def final notificationScheduler, notificationFactory, concourseService

    static void main(String[] args) {
        url = new URL("https://ci.concourse.ci")
        checkTime = 5
        notificationDelay = 5
        for (int i = 0; i < args.size(); i++) {
            if (args[i] == "-c") {
                try {
                    url = new URL(args[i + 1])
                } catch (Exception e) {
                    log.error "URL must be an URL. Using the default (https://ci.concourse.ci)"
                }
            } else if (args[i] == "-t") {
                try {
                    checkTime = Integer.parseInt(args[i + 1])
                } catch (Exception e) {
                    log.error "Checktime must be given as an Integer. Using the default (5 seconds)"
                }
            } else if (args[i] == "-d") {
                try {
                    notificationDelay = Integer.parseInt(args[i + 1])
                } catch (Exception e) {
                    log.error "Notification delay must be given as an Integer. Using the default (5 seconds)"
                }
            }
        }
        new ConcourseNotifier(url)
    }

    ConcourseNotifier(URL url) {
        concourseService = new ConcourseServiceImpl(url)
        notificationFactory = new NotificationFactoryImpl()
        notificationScheduler = new NotificationSchedulerImpl(concourseService, notificationFactory)
        notificationScheduler.startCheck()
    }

}
