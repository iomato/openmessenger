package openmessenger

import grails.test.*
import java.text.SimpleDateFormat
import openmessenger.Event.Status
import openmessenger.Event.Type

class EventControllerTests extends ControllerUnitTestCase {

    def eventService
    def springSecurityService

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testListAllEvents() {
/*
        def firstEvent = new Event(name: 'The Championships, Wimbledon',
            description: 'The oldest tennis tournament in the world, considered by many to be the most prestigious',
            occuredDate: new SimpleDateFormat("yyyy-MMM-dd").parse("20011-DEC-25"),
            type:Type.GROUP_CHAT,
            status: Status.NORMAL)
			
        def secondEvent = new Event(name: 'The Australian Open',
            description: 'The tournament is held in the middle of the Australian summer, in the last fortnight of the month of January; thus an extreme-heat policy is put into play when temperatures reach dangerous levels.',
            occuredDate: new SimpleDateFormat("yyyy-MMM-dd").parse("2008-DEC-25"),
            type:Type.GROUP_CHAT,
            status: Status.NORMAL)
			
        mockDomain(Event, [firstEvent, secondEvent])		
        def events =  controller.listAllEvents()
        assertNotNull events
		
        assertEquals "listAllEvents", controller.renderArgs.view
        */
    }

    void testViewEvent(){      
		def eventControl = mockFor(EventService)          
		eventControl.demand.findEventById(1..1) {->true}   
		this.controller.eventService = eventControl.createMock() 
		
        def firstEvent = new Event(name: 'The Championships, Wimbledon',
            description: 'The oldest tennis tournament in the world, considered by many to be the most prestigious',
            occuredDate: new SimpleDateFormat("yyyy-MMM-dd").parse("20011-DEC-25"),
            type:Type.GROUP_CHAT,
            status: Status.NORMAL)
			
        def secondEvent = new Event(name: 'The Australian Open',
            description: 'The tournament is held in the middle of the Australian summer, in the last fortnight of the month of January; thus an extreme-heat policy is put into play when temperatures reach dangerous levels.',
            occuredDate: new SimpleDateFormat("yyyy-MMM-dd").parse("2008-DEC-25"),
             type:Type.GROUP_CHAT,
            status: Status.NORMAL)
			
        mockDomain(Event, [firstEvent, secondEvent])
		controller.eventService = eventControl.createMock() 

        controller.params.id = "1"
        controller.view()
        assertEquals "view", controller.renderArgs.view	  

        eventControl.verify()    
    }         

	void testListEventSubscriber(){
  		def eventControl = mockFor(EventService)          
		eventControl.demand.findEventById(1..1) {->true}   
		this.controller.eventService = eventControl.createMock() 
		
        def firstEvent = new Event(name: 'The Championships, Wimbledon',
            description: 'The oldest tennis tournament in the world, considered by many to be the most prestigious',
            occuredDate: new SimpleDateFormat("yyyy-MMM-dd").parse("20011-DEC-25"),
            type:Type.GROUP_CHAT,
            status: Status.NORMAL)
			
        def secondEvent = new Event(name: 'The Australian Open',
            description: 'The tournament is held in the middle of the Australian summer, in the last fortnight of the month of January; thus an extreme-heat policy is put into play when temperatures reach dangerous levels.',
            occuredDate: new SimpleDateFormat("yyyy-MMM-dd").parse("2008-DEC-25"),
            type:Type.GROUP_CHAT,
            status: Status.NORMAL)
			
        mockDomain(Event, [firstEvent, secondEvent])
		controller.eventService = eventControl.createMock() 

        controller.params.id = "1"
        controller.listEventSubscribers()
        assertEquals "listEventSubscribers", controller.renderArgs.view	  

        eventControl.verify()  	
		
	}
               
    void testCreateEvent(){
		def eventInstance = controller.create()
		assertNotNull eventInstance
	
	}
	
    void testSaveEvent(){
        def eventSaved = []
        mockDomain(Event, eventSaved)
        controller.params.name = "dude"
        controller.params.description = "description"
        controller.params.occuredDate = new Date()
        controller.params.type = "GROUP_CHAT"
        controller.params.status = "NORMAL"
        controller.save()
        
        assertEquals "view", controller.redirectArgs["action"]
    }
    
    void testSaveEvilEventRedirect(){
        def eventSaved = []
        mockDomain(Event, eventSaved)
        controller.params.name = "dude"
        controller.params.description = "description"
        controller.params.occuredDate = new Date()
        controller.params.status = "EVIL" //Wrong status for Event class 
        controller.save()
        assertEquals "create", controller.renderArgs.view
    }   

    void testSubscribeToEvent(){     
        def eventControl = mockFor(EventService) 
        eventControl.demand.subscribeToEvent(1..1) {->true}
        controller.params.eventId = "2"
        controller.params.msisdn = "1234567890"
        this.controller.eventService = eventControl.createMock()
        
        controller.subscribeToEvent()
        
        assertEquals "listEventSubscribers", controller.redirectArgs["action"]  
        eventControl.verify()
    }

    void testSendMessage(){
        def eventService = mockFor(EventService)
        eventService.demand.sendMessage(1..1) {->true}
        controller.params.eventId = "2"
        controller.params.message = "test message"

        this.controller.eventService = eventService.createMock()

        controller.sendMessage()

        assertEquals "view", controller.redirectArgs["action"]
        eventService.verify()
    }
}
