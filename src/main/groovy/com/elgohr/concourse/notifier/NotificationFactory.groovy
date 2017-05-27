package com.elgohr.concourse.notifier

interface NotificationFactory {

    def createNotification(String name, String pipeline, String url, String status)

}