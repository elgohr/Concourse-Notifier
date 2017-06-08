package com.elgohr.concourse.notifier.notifications

import com.elgohr.concourse.notifier.Settings
import spock.lang.Specification

import java.util.concurrent.ExecutorService

class NotificationFactoryImplSpec extends Specification {

    private mockThreadPool
    private NotificationFactoryImpl notificationFactory

    void setup() {
        mockThreadPool = Mock(ExecutorService)
        notificationFactory = new NotificationFactoryImpl(new Settings())
        this.notificationFactory.notificationJobs = this.mockThreadPool
    }

    def "constructs notification async"() {
        when:
        notificationFactory.createNotification("NAME", "PIPELINE", "URL", "STATUS")
        then:
        1 * this.mockThreadPool.submit(_)
    }
}
