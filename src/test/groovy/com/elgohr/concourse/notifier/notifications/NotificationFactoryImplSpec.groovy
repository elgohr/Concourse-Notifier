package com.elgohr.concourse.notifier.notifications

import spock.lang.Specification

import java.util.concurrent.ExecutorService

class NotificationFactoryImplSpec extends Specification {

    def "constructs Notification"() {
        given:
        def mockThreadPool = Mock(ExecutorService)
        NotificationFactoryImpl notificationFactory = new NotificationFactoryImpl()
        notificationFactory.notificationJobs = mockThreadPool
        when:
        notificationFactory.createNotification("NAME", "PIPELINE", "URL", "STATUS")
        then:
        1 * mockThreadPool.submit(_)
    }
}
