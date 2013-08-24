package openmessenger

import groovyx.net.http.*
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.TEXT

class TelkomselSmsService {
    static rabbitQueue = 'openmessenger_telkomsel'
    
    def grailsApplication
    
    def handleMessage(message) {
        sendMessage(message)
    }
    
    def sendMessage(map) {
        println "Telkomsel service : get event callback"
        print "Map data: "
        println map
        try {
            assert map.msisdn != null
            assert map.msisdn != ''
        }
        catch(Throwable e) {
            throw new TelkomselSmsException(message: "MSISDN field is required")
        }
        
        if (!map.content) {
            map.content = map.content
        }
        
        def config = grailsApplication.config.telkomsel.sms.gateway
        def result = withHttp(uri:config.uri) {
            def query = [
                cp_name: config.username,
                pwd: config.password,
                msisdn: map.msisdn,
                sms: map.content
            ]
            
            def html = get(path: config.path, query: query, contentType: TEXT)
            print "HTML from GET : "
            print html
            return html
        }
        result = result.getText()
        print 'Result: '
        println result
        
        def resultMap = parseResult(result)
        println resultMap
        if (resultMap.status != 1) {
            throw new TelkomselSmsException(message: "Status:${resultMap.status}")
        }
        
        null
    }
    
    def parseResult(result) {
        // Trim whitespaces and split
        def parsed = (result - ~/^\s*/ - ~/\s*$/).split(':')
        println parsed
        [
            status: parsed[0] as Integer,
            trx_id: parsed.length == 2 ? parsed[1] : null
        ]
    }
}