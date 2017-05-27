package com.elgohr.concourse.notifier.notifications

import com.elgohr.concourse.notifier.api.ConcourseServiceImpl
import com.elgohr.concourse.notifier.api.Job
import spock.lang.Specification

class NotificationSchedulerImplSpec extends Specification {

    def scheduler
    def mockConcourseService
    def mockNotificationFactory

    void setup() {
        def arguments = [new URL("http://any")]
        mockConcourseService = Mock(ConcourseServiceImpl, constructorArgs: arguments)
        mockNotificationFactory = Mock(NotificationFactoryImpl)
        scheduler = new NotificationSchedulerImpl(mockConcourseService, mockNotificationFactory)
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
        sleep(5001)
        then:
        2 * mockConcourseService.getJobs()
    }
}
