package com.elgohr.concourse.notifier.notifications

import com.elgohr.concourse.notifier.ConcourseService
import com.elgohr.concourse.notifier.NotificationFactory
import com.elgohr.concourse.notifier.NotificationScheduler
import com.elgohr.concourse.notifier.api.Job

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class NotificationSchedulerImpl implements NotificationScheduler {

    def jobBuffer = [] as HashMap<String, Job>
    def final concourseService
    def final notificationFactory
    def checker = Executors.newScheduledThreadPool(1)

    NotificationSchedulerImpl(ConcourseService concourseService,
                              NotificationFactory notificationFactory) {
        this.concourseService = concourseService
        this.notificationFactory = notificationFactory
    }

    def startCheck() {
        checker.scheduleAtFixedRate(
                { -> doCheck() }, 0, 5, TimeUnit.SECONDS)
    }

    def doCheck() {
        def newJobs = concourseService.getJobs()
        if (!initialized()) {
            for (Job job in newJobs) {
                jobBuffer.put(job.getKey(), job)
            }
        } else {
            for (Job job in newJobs) {
                if (jobHasChanged(job)) {
                    notificationFactory.createNotification(job.name, job.pipeline, job.url, job.status)
                    jobBuffer.put(job.getKey(), job)
                }
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
