package openmessenger

import openmessenger.Event.Type
import java.text.SimpleDateFormat
import grails.converters.JSON

class EventController {

  def eventService
  def springSecurityService

  def view = {
    def targetEvent = eventService.findEventById(Long.valueOf(params.id))
    def errorMessage = params?.errorMessage?:''
    def offset = params.offset?params.int('offset'):0
    def max = params.max?params.int('max'):10
    def total = eventService.getMessagesCount(targetEvent)
    
    def messages = eventService.getMessages(targetEvent, offset, max, params.reply)

    render(view: "view", model:[
      event: targetEvent,
      total:total,
      messages:messages,
      message:errorMessage,
      offset:offset,
      timelineActive: params.reply ? '' : 'active',
      replyActive: params.reply ? 'active' : ''
    ]) //, total:total, messages:messages
  }

    def listAllEvents = {
      def userDetails = springSecurityService.principal
      def user = User.get(userDetails.id)
      def events = eventService.findAllEventByUser(user)
      //def events = Event.list()
      render(view:"listAllEvents", model:[events: events])
    }

  def listEventSubscribers = {
      def targetEvent = eventService.findEventById(Long.valueOf(params.id))
        render(view: "listEventSubscribers", model:[event: targetEvent])
  }

    def create = {
        def eventInstance = new Event()
        return [eventInstance: eventInstance]
    }

    def save = {
        def eventInstance
        def eventType = params.type
    if(params.occuredDate) {
      params.occuredDate = new SimpleDateFormat(message(code:'default.stringdate.format')).parse(params.occuredDate)
    }
        //println "group "+eventType
        //println "params: "+params
        if(eventType==Type.EVENT.toString()){
            eventInstance = new Event(params)
        }else if(eventType==Type.GROUP_CHAT.toString()){
            eventInstance = new GroupChat(params)
        }

        eventInstance.validate()

        if(eventInstance.hasErrors()){
            log.error eventInstance.errors
        }

        if (eventInstance.save(flush: true)){
      def userDetails = springSecurityService.principal
      def user = User.get(userDetails.id)
      UserEvent.create(user, eventInstance)
            redirect(action: "view", id: eventInstance.id)
        }else{
            render(view: "create", model: [eventInstance: eventInstance])
        }
    }

  def edit = {
    def eventInstance = Event.get(params.id)
    if(eventInstance) {
      render(view: "edit", model: [eventInstance: eventInstance])
    } else {
      redirect(controller:"home", action: "main")
    }
  }

  def update = {
    def eventInstance = Event.get(params.id)
    if(params.occuredDate) {
      params.occuredDate = new SimpleDateFormat(message(code:'default.stringdate.format')).parse(params.occuredDate)
    }
    if (eventInstance) {
      if (params.version) {
        def version = params.version.toLong()
        if (eventInstance.version > version) {
          eventInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'event.label', default: 'Event')] as Object[], "Another user has updated this User while you were editing")
          render(view: "edit", model: [eventInstance: eventInstance])
          return
        }
      }
      eventInstance.properties = params
      if (eventInstance.validate()) {
        try {
          eventInstance.save()
          flash.message = "${message(code: 'default.updated.message', args: [message(code: 'event.label', default: 'Event'), eventInstance.id])}"
          redirect(action: "view", id: eventInstance.id)
        } catch (Exception e) {
          log.error(e)
          render(view: "edit", model: [eventInstance: eventInstance])
        }
      }
      else {
        log.error(eventInstance.errors)
        render(view: "edit", model: [eventInstance: eventInstance])
      }
    }
    else {
      flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])}"
      redirect(action: "listAllEvents")
    }
  }

    def unsubscribeFromEvent = {
        def eventId =  params.id
        def msisdn =  params.msisdn

        eventService.unsubscribeFromEvent(Long.valueOf(eventId), msisdn)

        redirect(action: "listEventSubscribers", id: eventId)
    }


    def subscribeToEvent = {
        def eventId =  params.eventId
        def msisdn =  params.msisdn

        eventService.subscribeToEvent(Long.valueOf(eventId), msisdn)

        redirect(action: "listEventSubscribers", id: eventId)
    }

    def sendMessage = {
        def eventId = params.eventId
        def content = params.message
        def messageLimit = grailsApplication.config.openmessenger.message.limit
        def eventIds = []
        def tags

        if(params.containsKey('eventIds')) {
            eventIds += params.list('eventIds')
            eventIds.add(0, eventId)
        }

        if(params.containsKey('tags')) {
            tags = params.list('tags')
        }

		//TODO check subscriber before sent and create msg
		if(eventId && content.size() <= messageLimit) {
			def message = new Message(title:"News from openmessenger", content: content,
                createdDate: new Date())
            println params
            
            if(eventIds) {
                eventService.sendMessageWithMultipleEvents(eventIds, message, tags)
            } else if (tags) {
                eventService.sendMessage(Long.valueOf(eventId), message, tags)
                eventService.sendMessageWithMultipleEvents(eventIds, message)
            } else {
                eventService.sendMessage(Long.valueOf(eventId), message)
            }
			redirect(action: "view", id: eventId)
		} else {
	        redirect(action: "view", id: eventId, params:[errorMessage:content])
    	}
    }

    /*def sendMessageWithMultipleEvents = {
        def eventId = params.eventId
        def content = params.message
        def eventIds
        if(params.containsKey()) {
            eventIds = params.list('eventIds')
        }
        params.list('eventIds')
        def messageLimit = grailsApplication.config.openmessenger.message.limit

        //TODO check subscriber before sent and create msg
        if(eventIds && content.size() <= messageLimit) {
            def message = new Message(title:"News from openmessenger", content: content,
                createdDate: new Date())
            eventService.sendMessageWithMultipleEvents(eventIds, message)
            redirect(action: "view", id: eventId)
        } else {
            redirect(action: "view", id: eventId, params:[errorMessage:content])
        }
    }*/

    def getEvents = {
        def userDetails = springSecurityService.principal
        def user = User.get(userDetails.id)
        def events = eventService.findAllEventByUser(user)
        //def events = Event.list()
        render events as JSON
    }

}

