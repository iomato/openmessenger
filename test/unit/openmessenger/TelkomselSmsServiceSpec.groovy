package openmessenger

import grails.test.mixin.*

@TestFor(TelkomselSmsService)
class TelkomselSmsServiceSpec extends spock.lang.Specification {
    
    def setupSpec() {
        grailsApplication.config.telkomsel.sms.gateway = [
            uri: 'http://localhost:9002',
            path: '/submit.jsp',
            user: 'opendream',
            password: 'password'
        ]
    }
    
    def "SMS should not send without required parameter"() {
        given: "Empty parameter"
        def map = [:]
        
        when: "Send message"
        def result = service.sendMessage map
        
        then: "Service must shout an error"
        TelkomselSmsException e = thrown()
        e.toString() == 'TelkomselSmsException: MSISDN field is required'
    }
    
    def "SMS must be sent when provide correct parameter"() {
        given: "Corrected parameter"
        def counter = 0
        def returnValue = 0
        
        service.metaClass.withHttp = { uri ->
            counter++
            returnValue
        }
        def map = [
            msisdn: '66841291342',
            content: 'Sample SMS content ^_^'
        ]
        
        when: "Send message"
        def result0 = service.sendMessage map
        returnValue = 5
        def result1 = service.sendMessage map
        returnValue = 4
        def result2 = service.sendMessage map
        
        then: "Service must return some status"
        counter == 1
        result0 == '0'
        result1 == '5'
        result2 == '4'
    }
    
    def "Service should parse result string correctly"() {
        given: "Sample result string"
        def sample = [
            "8",
            "4",
            "  5  ",
            " 1:001122abcABC3344    "
        ]
        def result = [
            [status: 8, trx_id: null],
            [status: 4, trx_id: null],
            [status: 5, trx_id: null],
            [status: 1, trx_id: '001122abcABC3344']
        ]
        
        when: "Parse result"
        def parsed = []
        parsed[0] = service.parseResult sample[0]
        parsed[1] = service.parseResult sample[1]
        parsed[2] = service.parseResult sample[2]
        parsed[3] = service.parseResult sample[3]
        
        then: "Check if it parsed correctly"
        assert parsed[0] == result[0]
        assert parsed[1] == result[1]
        assert parsed[2] == result[2]
        assert parsed[3] == result[3]
    }
    
    def "Service must handle the message and call sendMessage method"() {
        setup:
        def map = [
            msisdn: '66841291342',
            content: 'Sample SMS content ^_^'
        ]
        
        def count = 0, count_result = []
        service.metaClass.sendMessage = { message ->
            ++count
        }
        
        when:
        service.handleMessage(map)
        count_result[0] = count
        service.handleMessage(map)
        count_result[1] = count
        
        then:
        count_result[0] == 1
        count_result[1] == 2
    }
}