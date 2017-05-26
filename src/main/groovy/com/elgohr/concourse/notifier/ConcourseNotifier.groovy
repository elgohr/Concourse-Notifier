package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.api.ConcourseService
import com.elgohr.concourse.notifier.notifications.NotificationFactory
import com.elgohr.concourse.notifier.notifications.NotificationScheduler

class ConcourseNotifier {

    def final notificationScheduler
    def final notificationFactory
    def final concourseService

    static void main(String[] args) {
        new ConcourseNotifier(new URL(args[0]))
    }

    ConcourseNotifier(URL url) {
        concourseService = new ConcourseService(url)
        notificationFactory = new NotificationFactory()
        notificationScheduler = new NotificationScheduler(concourseService, notificationFactory)
        notificationScheduler.startCheck()
    }

}
