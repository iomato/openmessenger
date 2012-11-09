package openmessenger

import grails.test.*
import java.text.SimpleDateFormat
import openmessenger.Event.Status
import openmessenger.Event.Type

class ReportServiceIntegrationTests extends GroovyTestCase {
    protected void setUp() {            
        super.setUp()
        // setup uses, events, messages
        def au = new Event(name: 'The Australian Open',
                description: 'The tournament is held in the middle of the Australian summer, in the last fortnight of the month of January; thus an extreme-heat policy is put into play when temperatures reach dangerous levels.',
                occuredDate: Date.parse("yyyy-MMM-dd","2008-DEC-25"),
                status:Status.NORMAL, type:Type.GROUP_CHAT).save(flush:true)
        def uk = new Event(name: 'The Championships, Wimbledon',
                description: 'The oldest tennis tournament in the world, considered by many to be the most prestigious',
                occuredDate: Date.parse("yyyy-MMM-dd","2011-DEC-25"),
                status:Status.NORMAL, type:Type.GROUP_CHAT).save(flush:true)        
              
        def newSubscribers = [new Subscriber(msisdn: '66809737798', active: 'Y'),
                                new Subscriber(msisdn: '85509737798', active: 'Y'),
                                new Subscriber(msisdn: '85209737798', active: 'Y')]

        def defaultGateway = new Gateway(prefix:'00', name:'inter_clickatell', queueName:'openmessenger', createdBy:'admin')
        def dtacGateway = new Gateway(prefix:'66', name:'th_dtac', queueName:'openmessenger_dtac', createdBy:'admin')
        def helloGateway = new Gateway(prefix:'855', name:'kh_hello', queueName:'openmessenger_hello', createdBy:'admin')
        def hongkongGateway  = new Gateway(prefix:'852', name:'hk_hongkong', queueName:'openmessenger_hongkong', createdBy:'admin')

        def user1 = new User(username:'one',
            password:'password',
            firstname:'firstname',
            lastname:'lastname',
            email:'email@email.com',
            enabled:true,
            accountExpired:false,
            accountLocked:false,
            passwordExpired:false
            ).save(failOnError:true, flush:true)
        def user2 = new User(username:'two',
            password:'password',
            firstname:'boytwo',
            lastname:'lastname',
            email:'email@email.com',
            enabled:true,
            accountExpired:false,
            accountLocked:false,
            passwordExpired:false
            ).save(failOnError:true, flush:true)
        
        
        /*mockDomain(Event, [uk, au])
        mockDomain(Subscriber, newSubscribers)
        mockDomain(Gateway, [defaultGateway,dtacGateway,helloGateway,hongkongGateway])
        mockDomain(Message)
        mockDomain(User, [user1, user2])        
        mockDomain(UserEvent, [new UserEvent(user: user1, event: au), new UserEvent(user: user1, event: uk), new UserEvent(user: user2, event: uk)])
        */
        new UserEvent(user: user1, event: au).save(failOnError:true, flush:true)
        new UserEvent(user: user1, event: uk).save(failOnError:true, flush:true)
        new UserEvent(user: user2, event: uk).save(failOnError:true, flush:true)

        newSubscribers.each {
            au.addToSubscribers(it)
            uk.addToSubscribers(it)            
        }
        
        20.times {
            def date = Date.parse("yyyy-MMM-dd","2011-DEC-${it+1}")
            new MessageLog(event:au, eventType:au.type, msisdn:'6612323434213', gateway:'dtac', 
                msg:"content $it", createBy:user1.username, date:date, 
                concatinationSize:1, price:2).save(failOnError:true, flush:true)
            new MessageLog(event:uk, eventType:au.type, msisdn:'6612323434213', gateway:'dtac', 
                msg:"content $it", createBy:user2.username, date:date, 
                concatinationSize:1, price:2).save(failOnError:true, flush:true)
            //new Message(event:au, title:"title $it", content:"content $it", createdDate:date).save(failOnError:true, flush:true)
            //new Message(event:uk, title:"title $it", content:"content $it", createdDate:date).save(failOnError:true, flush:true)
            //def message = new Message(title:"title $it", content:"content $it", createdDate:new Date())
            //au.addToMessages(message)
            //uk.addToMessages(message)
        }
        new MessageLog(event:au, eventType:au.type, msisdn:'6612323434213', gateway:'dtac', msg:'add new content', 
            createBy:user1.username, date:Date.parse("yyyy-MMM-dd","2011-DEC-11"), 
            concatinationSize:1, price:2).save(failOnError:true, flush:true)
        //new Message(event:au, title:"title new", content:"add new content", createdDate:Date.parse("yyyy-MMM-dd","2011-DEC-11")).save(failOnError:true, flush:true)
        au.save(failOnError:true)
        uk.save(failOnError:true)
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testGetTransactionReport() {
        assert 41 == MessageLog.count()
        //assert 3 == Subscriber.count()
        def reportService = new ReportService()
        def user1 = User.findByUsername('one')
        def user2 = User.findByUsername('two')
        
        // from date, to date
        // event , date(asc), message, phoneNo
        def fromDate = Date.parse("yyyy-MMM-dd","2011-DEC-1")
        def toDate = Date.parse("yyyy-MMM-dd","2011-DEC-20")
        def results = reportService.getTransactionReport(user1, fromDate, toDate)
        
        assert 41 == results.size()

        results = reportService.getTransactionReport(user2, fromDate, toDate)
        assert 20 == results.size()
    }

    void testGetUsageReport() {
        // from date, to date, group by (day , week, month)
        // event, message count, date
        // firstDate == thu
        def reportService = new ReportService()
        def user1 = User.findByUsername('one')
        def user2 = User.findByUsername('two')
        def fromDate = Date.parse("yyyy-MMM-dd","2011-DEC-1")
        def toDate = Date.parse("yyyy-MMM-dd","2011-DEC-20")
        def filters = ['date', 'event']
        println 'day'+fromDate.day+' '+fromDate.getAt(Calendar.WEEK_OF_YEAR)+' '+fromDate.month+' '+fromDate.getAt(Calendar.YEAR)
        println 'properties: '+fromDate.properties
        def results = reportService.getUsageReport(user1, fromDate, toDate, filters)
        assert 40 == results.size()
        filters = ['date']
        results = reportService.getUsageReport(user1, fromDate, toDate, filters)
        assert 20 == results.size()
        /*filters = ['week']
        results = reportService.getUsageReport(user1, fromDate, toDate, filters)
        assert 4 == results.size()
        fromDate += 10
        results = reportService.getUsageReport(user1, fromDate, toDate)
        assert 1 == results.size()

        results = reportService.getUsageReport(user1, fromDate, toDate, filters)
        assert 2 == results.size()
        */
        filters = ['event']  
        results = reportService.getUsageReport(user1, fromDate, toDate, filters)
        assert 2 == results.size()
    }
}
