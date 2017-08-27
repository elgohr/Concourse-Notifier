package com.elgohr.concourse.notifier.notifications

import com.elgohr.concourse.notifier.Buffer
import com.elgohr.concourse.notifier.ConcourseService
import com.elgohr.concourse.notifier.NotificationFactory
import com.elgohr.concourse.notifier.NotificationScheduler
import com.elgohr.concourse.notifier.api.Job
import com.elgohr.concourse.notifier.api.Pipeline
import groovy.util.logging.Slf4j

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Slf4j
class NotificationSchedulerImpl implements NotificationScheduler {

    def final concourseService, notificationFactory, checker, buffer

    NotificationSchedulerImpl(ConcourseService concourseService,
                              NotificationFactory notificationFactory,
                              ScheduledExecutorService checkPool,
                              Buffer buffer) {
        this.concourseService = concourseService
        this.notificationFactory = notificationFactory
        this.checker = checkPool
        this.buffer = buffer
    }

    def startCheck() {
        checker.scheduleAtFixedRate(
                { -> doCheck() }, 0, 5, TimeUnit.SECONDS)
    }

    def doCheck() {
        final boolean isInitialized = initialized()
        for (Pipeline pipeline in concourseService.getPipelines()) {
            buffer.setPipeline pipeline
            if (!pipeline.isPaused()) {
                for (Job job in concourseService.getJobs(pipeline)) {
                    if (jobHasChanged(job)) {
                        log.info "$job.pipeline - $job.name : Changed status to $job.status"
                        if (isInitialized) {
                            notificationFactory.createNotification(job.name, job.pipeline, job.url.toString(), job.status)
                        }
                        buffer.setJob(job.getKey(), job)
                    }
                }
            }
        }
    }

    def stopCheck() {
        checker.shutdown()
    }

    private boolean initialized() {
        buffer.getJobs().size() > 0
    }

    private boolean jobHasChanged(Job job) {
        !buffer.getJobs().containsKey(job.getKey()) ||
                buffer.getJobs().get(job.getKey()).getStatus() != job.getStatus()
    }

}
