package com.elgohr.concourse.notifier.api

import spock.lang.Specification


class HttpClientSpec extends Specification {

    def "loads external content from url"() {
        given:
        URL.metaClass.text = "RESPONSE_MOCK"
        def httpClient = new HttpClient()
        when:
        def response = httpClient.get(new URL("http://target.url"))
        then:
        response == "RESPONSE_MOCK"
    }
}
