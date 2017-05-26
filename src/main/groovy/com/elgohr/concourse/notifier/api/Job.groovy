package com.elgohr.concourse.notifier.api

class Job {

    def final name, pipeline, url, status

    Job(name, pipeline, url, status) {
        this.name = name
        this.pipeline = (pipeline == null) ? "" : pipeline
        this.url = (url == null) ? "" : url
        this.status = (status == null) ? "" : status
    }

    def getKey() {
        "$pipeline.$name".toString()
    }

    def getName() {
        name
    }

    def getPipeline() {
        pipeline
    }

    def getUrl() {
        url
    }

    def getStatus() {
        status
    }
}
