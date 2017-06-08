package com.elgohr.concourse.notifier.notifications

import com.elgohr.concourse.notifier.NotificationFactory
import com.elgohr.concourse.notifier.Settings
import groovy.util.logging.Slf4j

import java.util.concurrent.Executors

@Slf4j
class NotificationFactoryImpl implements NotificationFactory {

    def notificationJobs = Executors.newCachedThreadPool()
    def numberOfOpenNotifications = 0
    def settings

    NotificationFactoryImpl(Settings settings) {
        this.settings = settings
    }

    def createNotification(String name,
                           String pipeline,
                           String url,
                           String status) {
        notificationJobs.submit({ ->
            numberOfOpenNotifications++
            log.debug "Number of open notifications $numberOfOpenNotifications"
            int timeoutInMillis = settings.notificationTimeoutInSecs * 1000
            new NotificationView(pipeline, name, status, numberOfOpenNotifications, timeoutInMillis)
                    .showNotification { numberOfOpenNotifications-- }
        })
    }
}
