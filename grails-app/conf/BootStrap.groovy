import openmessenger.Event
import java.text.SimpleDateFormat

class BootStrap {

    def init = { servletContext ->
        if (!Event.count()) {
            def firstEvent = new Event(name: 'The Championships, Wimbledon', 
                description: 'The oldest tennis tournament in the world, considered by many to be the most prestigious',
                occuredDate: new SimpleDateFormat("yyyy-MMM-dd").parse("2008-DEC-25"),
                status: 'NORMAL').save(failOnError: true)
			
            def secondEvent = new Event(name: 'The Australian Open', 
                description: 'The tournament is held in the middle of the Australian summer, in the last fortnight of the month of January; thus an extreme-heat policy is put into play when temperatures reach dangerous levels.',
                occuredDate: new SimpleDateFormat("yyyy-MMM-dd").parse("2008-DEC-25"),
                status: 'NORMAL').save(failOnError: true)
        }
    }
    def destroy = {
    }
}
