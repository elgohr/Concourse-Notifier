package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.api.Job


interface ConcourseService {

    List<Job> getJobs()

}