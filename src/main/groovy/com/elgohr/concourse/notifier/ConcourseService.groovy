package com.elgohr.concourse.notifier

import com.elgohr.concourse.notifier.api.Job
import com.elgohr.concourse.notifier.api.Pipeline


interface ConcourseService {

    List<Pipeline> getPipelines()
    List<Job> getJobs(Pipeline pipeline)

}