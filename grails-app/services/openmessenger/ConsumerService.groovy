package openmessenger

import com.rabbitmq.client.*

import groovyx.net.http.*
import groovyx.net.http.ContentType.*
import groovyx.net.http.Method.*
import org.apache.commons.lang.CharUtils
import org.apache.commons.lang.StringUtils;


import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class ConsumerService {

    static transactional = true
	static rabbitQueue = 'openmessenger'
	def sessionId

    void handleMessage(String msg) {
		println "Received String Message: ${msg}"
    }
	
	void handleMessage(Map map) {
		
			/*if(!sessionId) {
			def clickatell = withHttp(uri: "http://api.clickatell.com") {
				def html = get(path : '/http/auth', query : [api_id:'3312346',user:'opendream',password:'Tdb5vzt6zuMAhG'])
			}*/		
				 
		try{	
			sendMessage(map)	
		}catch (Exception e) {
			log.error(e.toString(),e)
			//throw e
		}		
	}
	
	def sendMessage(Map map){
		def result = withHttp(uri:CH.config.sms.gateway.uri) {
			def html = get(path :CH.config.sms.gateway.path,
								query : [api_id:CH.config.sms.gateway.apiId, user:CH.config.sms.gateway.user,
										password:CH.config.sms.gateway.password, to:map.msisdn,
										text:convertToUnicode(map.content), from:CH.config.sms.gateway.senderId, unicode:1, concat:getConcatinationSize(map.content)])
			}
		
		if(!result.toString().contains('ID:'))
			throw new ConsumerServiceException(result.toString(), CH.config.sms.gateway.senderId, map.msisdn)	
		return result	
	}
	
	def convertToUnicode(String msg){
		StringBuffer str = new StringBuffer()
		msg.toCharArray().each {
			str.append( CharUtils.unicodeEscaped(it) )
		}
		StringUtils.remove(str.toString(), "\\u")
	}
	
	def getConcatinationSize(String msg){
		msg.size().div(70).plus(msg.size()%70?1:0).intValue()			
	}
}
