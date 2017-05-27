package com.elgohr.concourse.notifier.notifications

import com.elgohr.concourse.notifier.NotificationFactory

import java.util.concurrent.Executors

class NotificationFactoryImpl implements NotificationFactory {

    def notificationJobs = Executors.newCachedThreadPool()

    def createNotification(String name, String pipeline, String url, String status) {
        def title = "$pipeline - $name"
        notificationJobs.submit({ -> new Notification(title, "", status) })
    }
}
