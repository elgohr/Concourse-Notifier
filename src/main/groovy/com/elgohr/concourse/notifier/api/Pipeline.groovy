package com.elgohr.concourse.notifier.api

class Pipeline {

    private final String name, team
    private final URL url

    Pipeline(String name, String team, URL url) {
        this.name = name
        this.team = team
        this.url = url
    }

    def getName() {
        return name
    }

    def getTeam() {
        return team
    }

    def getUrl() {
        return url
    }
}
