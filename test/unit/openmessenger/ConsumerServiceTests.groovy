package openmessenger

import grails.test.*
import org.apache.commons.lang.CharUtils
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class ConsumerServiceTests extends GrailsUnitTestCase {
	def consumerService
	
    protected void setUp() {
		consumerService = new ConsumerService()
		def config = new ConfigObject()
		config.put('sms.gateway', 'path')
		CH.setConfig(config)
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testHandleMessage() {
		def maps = [[uri:'http1', msisdn:'66890242989', content:'Call me RabbitMQ dude'], 
			[uri:'http2', msisdn:'66890242989', content:'Call me RabbitMQ dude'], 
			[uri:'http3', msisdn:'66890242989', content:'Call me RabbitMQ dude']]
		def counter = 0
		consumerService.metaClass.withHttp = {Map map, Closure closure -> counter++}

		maps.each { 
			consumerService.handleMessage(it)
		}
		
		assertEquals 3, counter
    }
	
	void testConvertToUnicode() {		
		String msg = 'ไทย'
		String result = consumerService.convertToUnicode(msg)
		assertEquals '0e440e170e22', result
	}
	
	void testGetConcatinationSize(){		
		assertEquals 1 , consumerService.getConcatinationSize('asdf')		
		assertEquals 2 , consumerService.getConcatinationSize('Call me RabbitMQ Dude ทดสอบไทย ព្រះរាជាណាចក្រកម្ពុជា  tiếng Việt, Việt ngữ')	
	}
}
