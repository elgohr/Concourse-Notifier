package com.elgohr.concourse.notifier.notifications

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

    def jobBuffer = [] as HashMap<String, Job>
    def final concourseService, notificationFactory, checker

    NotificationSchedulerImpl(ConcourseService concourseService,
                              NotificationFactory notificationFactory,
                              ScheduledExecutorService checkPool) {
        this.concourseService = concourseService
        this.notificationFactory = notificationFactory
        this.checker = checkPool
    }

    def startCheck() {
        checker.scheduleAtFixedRate(
                { -> doCheck() }, 0, 5, TimeUnit.SECONDS)
    }

    def doCheck() {
        if (!initialized()) {
            initializeJobBuffer()
        } else {
            updateJobs()
        }
    }

    private void updateJobs() {
        for (Pipeline pipeline in concourseService.getPipelines()) {
            for (Job job in concourseService.getJobs(pipeline)) {
                if (jobHasChanged(job)) {
                    log.info "$job.pipeline - $job.name : Changed status to $job.status"
                    notificationFactory.createNotification(job.name, job.pipeline, job.url.toString(), job.status)
                    jobBuffer.put(job.getKey(), job)
                }
            }
        }
    }

    private void initializeJobBuffer() {
        for (Pipeline pipeline in concourseService.getPipelines()) {
            for (Job job in concourseService.getJobs(pipeline)) {
                jobBuffer.put(job.getKey(), job)
                log.debug "$job.pipeline - $job.name : Added with status $job.status"
            }
        }
    }

    def stopCheck() {
        checker.shutdown()
    }

    private boolean initialized() {
        jobBuffer.size() > 0
    }

    private boolean jobHasChanged(Job job) {
        !jobBuffer.containsKey(job.getKey()) ||
                jobBuffer.get(job.getKey()).getStatus() != job.getStatus()
    }

}
