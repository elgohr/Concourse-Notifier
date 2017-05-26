package com.elgohr.concourse.notifier.notifications

import java.awt.Color

class ConcourseColors {

    def static getBackgroundColor() {
        new Color(39, 55, 71)
    }

    def static getTextColor() {
        new Color(230, 231, 232)
    }

    def static getTitleBarColor() {
        new Color(26, 37, 47)
    }

    def static getSucceededColor() {
        new Color(46, 204, 113)
    }

    def static getPendingColor() {
        new Color(189, 195, 199)
    }

    def static getStartedColor() {
        new Color(241, 196, 15)
    }

    def static getFailedColor() {
        new Color(231, 76, 60)
    }

    def static getErroredColor() {
        new Color(230, 126, 34)
    }

    def static getAbortedColor() {
        new Color(143, 75, 45)
    }

    def static getPausedColor() {
        new Color(52, 152, 219)
    }
}
