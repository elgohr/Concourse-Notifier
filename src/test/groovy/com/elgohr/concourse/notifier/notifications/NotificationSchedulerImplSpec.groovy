package com.elgohr.concourse.notifier.notifications

import com.elgohr.concourse.notifier.Buffer
import com.elgohr.concourse.notifier.Settings
import com.elgohr.concourse.notifier.api.ConcourseServiceImpl
import com.elgohr.concourse.notifier.api.HttpClient
import com.elgohr.concourse.notifier.api.Job
import com.elgohr.concourse.notifier.api.Pipeline
import spock.lang.Specification

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class NotificationSchedulerImplSpec extends Specification {

    def scheduler, mockConcourseService, mockNotificationFactory, spyCheckPool, mockBuffer

    void setup() {
        def arguments = [new Settings(), Mock(HttpClient)]
        mockConcourseService = Mock(ConcourseServiceImpl, constructorArgs: arguments)
        mockNotificationFactory = Mock(NotificationFactoryImpl)
        mockBuffer = Mock(Buffer)
        spyCheckPool = Spy(ScheduledThreadPoolExecutor, constructorArgs: [1])
        scheduler = new NotificationSchedulerImpl(
                mockConcourseService,
                mockNotificationFactory,
                spyCheckPool,
                mockBuffer)
    }

    void cleanup() {
        scheduler.stopCheck()
    }

    def "initially loads jobs without notification"() {
        given:
        def pipeline1 = new Pipeline("NAME", "TEAM", new URL("http://URL"))
        def pipeline2 = new Pipeline("NAME1", "TEAM1", new URL("http://URL"))

        when:
        scheduler.doCheck()

        then:
        1 * mockConcourseService.getPipelines() >> [pipeline1, pipeline2]
        1 * mockConcourseService.getJobs(pipeline1) >>
                [new Job("NAME", "PIPELINE1", new URL("http://URL"), "STATUS"),
                 new Job("NAME1", "PIPELINE1", new URL("http://URL1"), "STATUS1")]
        1 * mockConcourseService.getJobs(pipeline2) >>
                [new Job("NAME", "PIPELINE2", new URL("http://URL"), "STATUS"),
                 new Job("NAME1", "PIPELINE2", new URL("http://URL1"), "STATUS1")]
        1 * mockBuffer.getJobs() >> []
        4 * mockBuffer.setJob(_, _)
        0 * mockNotificationFactory.createNotification(_, _, _, _)
    }

    def "creates notifications for jobs when added after initialization"() {
        setup:
        mockBuffer.getJobs() >> ["OLD_NAME": new Job("OLD_NAME", "OLD_PIPELINE", new URL("http://OLD_URL"), "OLD_STATUS")]

        when:
        scheduler.doCheck()

        then:
        1 * mockConcourseService.getPipelines() >> [new Pipeline("NAME", "TEAM", new URL("http://URL"))]
        1 * mockConcourseService.getJobs(_) >>
                [new Job("OLD_NAME", "OLD_PIPELINE", new URL("http://OLD_URL"), "OLD_STATUS"),
                 new Job("NAME", "PIPELINE", new URL("http://URL"), "STATUS")]
        1 * mockNotificationFactory.createNotification("NAME", "PIPELINE", "http://URL", "STATUS")
    }

    def "creates notifications for jobs when changed"() {
        setup:
        mockBuffer.getJobs() >> ["NAME": new Job("NAME", "OLD_PIPELINE", new URL("http://OLD_URL"), "OLD_STATUS")]

        when:
        scheduler.doCheck()

        then:
        1 * mockConcourseService.getPipelines() >> [new Pipeline("NAME", "TEAM", new URL("http://URL"))]
        1 * mockConcourseService.getJobs(_) >>
                [new Job("NAME", "PIPELINE", new URL("http://URL"), "STATUS")]
        1 * mockNotificationFactory.createNotification("NAME", "PIPELINE", "http://URL", "STATUS")
    }

    def "does not create notifications for jobs when not changed"() {
        setup:
        mockBuffer.getJobs() >> ["OLD_PIPELINE.OLD_NAME": new Job("OLD_NAME", "OLD_PIPELINE", new URL("http://OLD_URL"), "OLD_STATUS")]

        when:
        scheduler.doCheck()

        then:
        1 * mockConcourseService.getPipelines() >> [new Pipeline("NAME", "TEAM", new URL("http://URL"))]
        1 * mockConcourseService.getJobs(_) >>
                [new Job("OLD_NAME", "OLD_PIPELINE", new URL("http://OLD_URL"), "OLD_STATUS")]
        0 * mockNotificationFactory.createNotification(_, _, _, _)
    }

    def "checks API every 5 seconds for updates"() {
        when:
        scheduler.startCheck()
        then:
        1 * spyCheckPool.scheduleAtFixedRate(_, 0, 5, TimeUnit.SECONDS)
    }

    def "does not check pipelines which are paused on startup"() {
        setup:
        def unpausedPipeline = new Pipeline("NAME", "TEAM", new URL("http://URL"))
        def pausedPipeline = new Pipeline("NAME", "TEAM", new URL("http://URL"))
        pausedPipeline.pause()
        mockConcourseService.getPipelines() >> [unpausedPipeline, pausedPipeline]

        mockBuffer.getJobs() >> []
        when:
        scheduler.doCheck()
        then:
        1 * mockConcourseService.getJobs(unpausedPipeline)
        0 * mockConcourseService.getJobs(pausedPipeline)
    }

    def "does not check pipelines which are paused when already initialized"() {
        setup:
        def unpausedPipeline = new Pipeline("NAME", "TEAM", new URL("http://URL"))
        def pausedPipeline = new Pipeline("NAME", "TEAM", new URL("http://URL"))
        pausedPipeline.pause()
        mockConcourseService.getPipelines() >> [unpausedPipeline, pausedPipeline]

        mockBuffer.getJobs() >> ["OLD_PIPELINE.OLD_NAME": new Job("OLD_NAME", "OLD_PIPELINE", new URL("http://OLD_URL"), "OLD_STATUS")]
        when:
        scheduler.doCheck()
        then:
        1 * mockConcourseService.getJobs(unpausedPipeline)
        0 * mockConcourseService.getJobs(pausedPipeline)
    }
}
