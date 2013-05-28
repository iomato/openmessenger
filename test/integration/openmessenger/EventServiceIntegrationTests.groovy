package openmessenger

import grails.test.*

import org.grails.taggable.Tag
import openmessenger.Event.Status
import openmessenger.Event.Type

class EventServiceIntegrationTests extends GroovyTestCase {
    def eventService
    def rabbitSent

    protected void setUp() {
        super.setUp()

        def springSecurityService = [:]
        springSecurityService.principal = [username: 'admin']

        eventService = new EventService()
        eventService.springSecurityService = springSecurityService
        rabbitSent=0
        eventService.metaClass.rabbitSend = {queue, msg -> rabbitSent++; println queue}       

        def warningGroup = new Event(name: 'Warning Group',
                description: 'Warning Group',
                occuredDate: Date.parse("yyyy-MMM-dd","2012-NOV-07"),
                status:Status.NORMAL, type:Type.EVENT).save()

        def subscribers = [ '66809737791', '66809737792',
                            '66809737793', '66809737794',
                            '66809737795']

        subscribers.each {
            eventService.subscribeToEvent(warningGroup.id, it)
        }        
        
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSendMessageWithTags() {
        def message = new Message(title:"H5N1 warning", content:"Warning: H5N1 outbreak in the winter", createdDate:new Date())
        def tags = ['info', 'weather', 'infection']
        //message.addTags(tags)
        def soccernet = Event.findByName('Warning Group')
        eventService.sendMessage(soccernet.id, message, tags)

        def targetMessage = Message.get(message.id)

        assert Tag.count() == 3
        assert MessageLog.count() == 5
        assert 3 == targetMessage.tags.size()
        println targetMessage.tags
    }

    void testSendMultipleMessageWithTags() {
        def cl = new Event(name: 'Chicken Little',
                description: 'Chicken Little Group',
                occuredDate: Date.parse("yyyy-MMM-dd","2012-NOV-07"),
                status:Status.NORMAL, type:Type.EVENT).save()
        def subscribers = [ '66809737791', '66809737797',
                            '66809737798']

        subscribers.each {
            eventService.subscribeToEvent(cl.id, it)
        }
        def previousMsg = Message.count()

        def message = new Message(title:"Red Alert", content:"Alert: H5N1 outbreak in the northen", createdDate:new Date())
        def tags = ['checken', 'infection']
        def soccernet = Event.findByName('Warning Group')

        def eventIds = [cl.id, soccernet.id]

        eventService.sendMessageWithMultipleEvents(eventIds, message, tags)

        
        MessageLog.list().each {
            println "${it.id} event ${it.event.name} ${it.msisdn} ${it.msg}"
        }
        assert Tag.count() == 2        
        assert MessageLog.count() == 7
        assert 2 == Message.count() - previousMsg
        def targetMessages = Message.findByTitle('Red Alert')
        targetMessages.each {
            assert 2 == it.tags.size()
        }
        
    }
}
