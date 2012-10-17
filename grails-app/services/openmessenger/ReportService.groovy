package openmessenger

class ReportService {

    static transactional = true

    def getTransactionReport(user, fromDate, toDate, limit=null) {
        def userEvents = UserEvent.findAllByUser(user)
        def events = userEvents*.event
               
        def results = MessageLog.createCriteria().list() {
            ge('date', fromDate)
            le('date', toDate)
            'in'('event', events)
            if(limit) {
                maxResults(limit)
            }            
        }

        return results
    }

    def getUsageReport(user, fromDate, toDate, filters=null) {
        def userEvents = UserEvent.findAllByUser(user)
        def events = userEvents*.event

        def results = MessageLog.createCriteria().list() {
            ge('date', fromDate)
            le('date', toDate)
            'in'('event', events) 
            projections {
                count "msisdn"
                filters.each {
                    groupProperty(it)                    
                }
            }           
        }

        return results
    }
}
