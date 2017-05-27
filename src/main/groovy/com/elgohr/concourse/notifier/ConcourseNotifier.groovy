package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.api.ConcourseServiceImpl
import com.elgohr.concourse.notifier.notifications.NotificationFactoryImpl
import com.elgohr.concourse.notifier.notifications.NotificationSchedulerImpl

class ConcourseNotifier {

    def final notificationScheduler
    def final notificationFactory
    def final concourseService

    static void main(String[] args) {
        new ConcourseNotifier(new URL(args[0]))
    }

    ConcourseNotifier(URL url) {
        concourseService = new ConcourseServiceImpl(url)
        notificationFactory = new NotificationFactoryImpl()
        notificationScheduler = new NotificationSchedulerImpl(concourseService, notificationFactory)
        notificationScheduler.startCheck()
    }

}
