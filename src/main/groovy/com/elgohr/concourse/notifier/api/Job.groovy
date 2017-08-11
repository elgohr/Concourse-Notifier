package com.elgohr.concourse.notifier.api

class Job {

    private final String name, pipeline, status
    private final URL url

    Job(String name, String pipeline, URL url, String status) {
        this.name = name
        this.pipeline = (pipeline == null) ? "" : pipeline
        this.url = url
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
