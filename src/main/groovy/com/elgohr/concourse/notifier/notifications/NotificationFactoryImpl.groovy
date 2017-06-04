package com.elgohr.concourse.notifier.notifications

import com.elgohr.concourse.notifier.NotificationFactory
import groovy.util.logging.Slf4j

import java.util.concurrent.Executors

@Slf4j
class NotificationFactoryImpl implements NotificationFactory {

    def notificationJobs = Executors.newCachedThreadPool()
    def numberOfOpenNotifications = 0

    def createNotification(String name, String pipeline, String url, String status) {
        notificationJobs.submit({ ->
            numberOfOpenNotifications++
            log.debug "Number of open notifications $numberOfOpenNotifications"
            new NotificationView(pipeline, name, status, numberOfOpenNotifications)
                    .showNotification { numberOfOpenNotifications-- }
        })
    }
}
