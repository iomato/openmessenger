package openmessenger

import grails.test.*
import grails.test.mixin.*
import java.text.SimpleDateFormat
import openmessenger.Event.Status
import openmessenger.Event.Type
import openmessenger.User
import grails.converters.JSON
import org.junit.Before

@TestFor(EventController)
@Mock([Event, User, EventService, Message])
class EventControllerTests {

    def eventService
    def springSecurityService

    @Before
    void setUp() {
        def mockConfig = new ConfigObject();
        mockConfig.openmessenger.eventCallback='eventCallback'
        mockConfig.openmessenger.message.limit=140

        controller.metaClass.grailsApplication = [config:[openmessenger:[message:[limit:140]]]]

        springSecurityService = new HashMap()
        springSecurityService.principal = new HashMap()
        springSecurityService.principal.username = 'default username'
        springSecurityService.principal.id = 1

        def firstEvent = new Event(name: 'The Championships, Wimbledon',
            description: 'The oldest tennis tournament in the world, considered by many to be the most prestigious',
            occuredDate: Date.parse("yyyy-MMM-dd","20011-DEC-25"),
            type:Type.GROUP_CHAT,
            status: Status.NORMAL)

        def secondEvent = new Event(name: 'The Australian Open',
            description: 'The tournament is held in the middle of the Australian summer, in the last fortnight of the month of January; thus an extreme-heat policy is put into play when temperatures reach dangerous levels.',
            occuredDate: Date.parse("yyyy-MMM-dd","2008-DEC-25"),
            type:Type.GROUP_CHAT,
            status: Status.NORMAL)

        mockDomain(Event, [firstEvent, secondEvent])
        User.metaClass.encodePassword = { -> }
    }

    void testListAllEvents() {
      def user = new User(username:'user', password:'password', firstname:'firstname'
        , lastname:'lastname', email:'email@email.com', enabled:true
        , accountExpired:false, accountLocked:false, passwordExpired:false)

      mockDomain(User, [user])

      def eventControl = mockFor(EventService)
      eventControl.demand.findAllEventByUser(1..1) { param ->
        Event.list()
      }
      controller.eventService = eventControl.createMock()
      controller.springSecurityService = springSecurityService

      controller.listAllEvents()
      assert model != null

      assert "/event/listAllEvents" == view

    }

    void testViewEmptyEvent(){
    mockDomain(Message)

    def eventControl = mockFor(EventService)
    eventControl.demand.findEventById(1..1) { id -> null }
    eventControl.demand.getEventMessages(0..0) { messages, offset, max -> messages?.subList(offset, offset+max) }
    controller.eventService = eventControl.createMock()

    //controller.eventService = eventControl.createMock()

        controller.params.id = 1
        controller.view()
        assert "/event/view" == view

        eventControl.verify()
    }

  void testViewEvent(){
    def messages = [new Message(title: "the title1", content: "the content1", createdDate: new Date()),
      new Message(title: "the title2", content: "the content2", createdDate: new Date())
      ]

    mockDomain(Message, messages)

    def firstEvent = Event.findByName('The Championships, Wimbledon')
    messages.each {
      firstEvent.addToMessages(it)
      }

    def eventControl = mockFor(EventService)
    eventControl.demand.findEventById(1..1) { id -> firstEvent }
    eventControl.demand.getEventMessages(1..1) { messagelist, offset, max -> messages?.subList(offset, offset+max) }
    controller.eventService = eventControl.createMock()

    //controller.eventService = eventControl.createMock()

    controller.params.id = 1
    controller.view()
    assertEquals "/event/view", view
    assertEquals 2, model.messages.size()

    eventControl.verify()
  }

  void testListEventSubscriber(){
      def eventControl = mockFor(EventService)
    eventControl.demand.findEventById(1..1) {->true}
    this.controller.eventService = eventControl.createMock()
    controller.eventService = eventControl.createMock()

        controller.params.id = "1"
        controller.listEventSubscribers()
        assert  "/event/listEventSubscribers" == view

        eventControl.verify()

  }

    void testCreateEvent(){
    def eventInstance = controller.create()
    assertNotNull eventInstance

  }


    void testSubscribeToEvent(){
        def eventControl = mockFor(EventService)
        eventControl.demand.subscribeToEvent(1..1) {->true}
        controller.params.eventId = "2"
        controller.params.msisdn = "1234567890"
        this.controller.eventService = eventControl.createMock()

        controller.subscribeToEvent()

        assert "/event/listEventSubscribers/2" == response.redirectedUrl
        eventControl.verify()
    }

    void testSendMessage(){
        def eventService = mockFor(EventService)
        eventService.demand.sendMessage(1..1) {->true}
        controller.params.eventId = "2"
        controller.params.message = "test message"

        this.controller.eventService = eventService.createMock()

        controller.sendMessage()

        assert "/event/view/2" == response.redirectedUrl
        eventService.verify()
    }

    void testSendMessageWithLongMessage() {
      def message = 'test 0123456789' * 10
      controller.params.eventId = "2"
      controller.params.message = message

      controller.sendMessage()

      assert response.redirectedUrl =~ /^\/event\/view/
    }

  void testEditEvent() {
    def firstEvent = Event.findByName('The Championships, Wimbledon')
    controller.params.id = firstEvent.id
    controller.edit()
    def modelAndView = controller.modelAndView
    assert "/event/edit" == view
    assert firstEvent.name == model.eventInstance.name

    controller.params.id = "5"
    controller.edit()
    assert "/home/main" == response.redirectedUrl
  }

    /*
    void testSendMessageWithMultipleEvents() {
        new Event(name: 'The Roland Garros',
                description: 'The oldest tennis tournament in the world, considered by many to be the most prestigious',
                occuredDate: Date.parse("yyyy-MMM-dd","20011-DEC-25"),
                status:Status.STABLE, type:Type.GROUP_CHAT).save(flush:true)

        assert 3 == Event.count()

        def eventIds = Event.list()*.id
        def eventId = eventIds[0]
        eventIds -= eventId

        def eventService = mockFor(EventService)
        eventService.demand.sendMessageWithMultipleEvents(1..1) {->true}
        controller.params.eventId = eventId
        controller.params.eventIds = eventIds
        controller.params.message = "test message"

        this.controller.eventService = eventService.createMock()

        controller.sendMessage()

        assertEquals "/event/view/${eventId}", view
        assert eventId == controller.view
        eventService.verify()
    }
    */

    void testGetEvents() {
        def user = new User(username:'user', password:'password', firstname:'firstname'
            , lastname:'lastname', email:'email@email.com', enabled:true
            , accountExpired:false, accountLocked:false, passwordExpired:false)
        mockDomain(User, [user])

        def eventControl = mockFor(EventService)
        eventControl.demand.findAllEventByUser(1..1) { param -> Event.list() }
        controller.eventService = eventControl.createMock()
        controller.springSecurityService = springSecurityService

        controller.getEvents()
        def json = JSON.parse(controller.response.contentAsString)
        assert 2 == json.size()
        assert 'The Championships, Wimbledon' == json[0].name
        assert 'The Australian Open' == json[1].name
    }
}

