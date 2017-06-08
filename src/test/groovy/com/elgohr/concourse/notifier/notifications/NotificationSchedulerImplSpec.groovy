package com.elgohr.concourse.notifier.notifications

import com.elgohr.concourse.notifier.Settings
import com.elgohr.concourse.notifier.api.ConcourseServiceImpl
import com.elgohr.concourse.notifier.api.Job
import spock.lang.Specification

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class NotificationSchedulerImplSpec extends Specification {

    def scheduler, mockConcourseService, mockNotificationFactory, spyCheckPool

    void setup() {
        def arguments = [new Settings()]
        mockConcourseService = Mock(ConcourseServiceImpl, constructorArgs: arguments)
        mockNotificationFactory = Mock(NotificationFactoryImpl)
        spyCheckPool = Spy(ScheduledThreadPoolExecutor, constructorArgs: [1])
        scheduler = new NotificationSchedulerImpl(
                mockConcourseService,
                mockNotificationFactory,
                spyCheckPool)
    }

    void cleanup() {
        scheduler.stopCheck()
    }

    def "initially loads jobs without notification"() {
        given:
        def jobs = [new Job("NAME", "PIPELINE", "URL", "STATUS"),
                    new Job("NAME1", "PIPELINE1", "URL1", "STATUS1"),
                    new Job("NAME2", "PIPELINE2", "URL2", "STATUS2")]
        when:
        scheduler.doCheck()
        then:
        1 * mockConcourseService.getJobs() >> jobs
        scheduler.jobBuffer.containsKey("PIPELINE.NAME") == true
        scheduler.jobBuffer.get("PIPELINE.NAME") == jobs[0]
        0 * mockNotificationFactory.createNotification(_, _, _, _)
    }

    def "creates notifications for jobs when added after initialization"() {
        given:
        scheduler.jobBuffer = ["OLD_NAME": new Job("OLD_NAME", "OLD_PIPELINE", "OLD_URL", "OLD_STATUS")]
        when:
        scheduler.doCheck()
        then:
        1 * mockConcourseService.getJobs() >>
                [new Job("OLD_NAME", "OLD_PIPELINE", "OLD_URL", "OLD_STATUS"),
                 new Job("NAME", "PIPELINE", "URL", "STATUS")]
        1 * mockNotificationFactory.createNotification("NAME", "PIPELINE", "URL", "STATUS")
    }

    def "creates notifications for jobs when changed"() {
        given:
        scheduler.jobBuffer = ["NAME": new Job("NAME", "OLD_PIPELINE", "OLD_URL", "OLD_STATUS")]
        when:
        scheduler.doCheck()
        then:
        1 * mockConcourseService.getJobs() >>
                [new Job("NAME", "PIPELINE", "URL", "STATUS")]
        1 * mockNotificationFactory.createNotification("NAME", "PIPELINE", "URL", "STATUS")
    }

    def "does not create notifications for jobs when not changed"() {
        given:
        scheduler.jobBuffer = ["OLD_PIPELINE.OLD_NAME": new Job("OLD_NAME", "OLD_PIPELINE", "OLD_URL", "OLD_STATUS")]
        when:
        scheduler.doCheck()
        then:
        1 * mockConcourseService.getJobs() >>
                [new Job("OLD_NAME", "OLD_PIPELINE", "OLD_URL", "OLD_STATUS")]
        0 * mockNotificationFactory.createNotification(_, _, _, _)
    }

    def "checks API every 5 seconds for updates"() {
        when:
        scheduler.startCheck()
        then:
        1 * spyCheckPool.scheduleAtFixedRate(_, 0, 5, TimeUnit.SECONDS)
    }
}
