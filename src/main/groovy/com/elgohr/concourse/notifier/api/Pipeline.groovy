package com.elgohr.concourse.notifier.api

import javax.annotation.Generated

class Pipeline {

    private final String name, team
    private final URL url
    private boolean paused = false

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

    def pause(){
        paused = true
    }

    def resume(){
        paused = false
    }

    boolean isPaused() {
        return paused
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Pipeline pipeline = (Pipeline) o

        if (name != pipeline.name) return false
        if (team != pipeline.team) return false

        return true
    }

    int hashCode() {
        int result
        result = name.hashCode()
        result = 31 * result + team.hashCode()
        return result
    }

    @Override
    String toString() {
        return "Pipeline{" +
                "name='" + name + '\'' +
                ", team='" + team + '\'' +
                ", url=" + url +
                ", paused=" + paused +
                '}';
    }
}
