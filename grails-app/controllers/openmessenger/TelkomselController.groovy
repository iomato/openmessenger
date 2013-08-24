package openmessenger

class TelkomselController {
  
  def checkmember = {
    def msisdn = params.msisdn
    if (Subscriber.findByMsisdn(msisdn)) {
      render "1"
    }
    else {
      render "0"
    }
  }
  
  def inboxlistener = {
    def msisdn = params.msisdn
    def msg = params.msg
    
    def subscriber, events
    
    if (msg && msisdn ==~ /\d+/) {
      // Get subscriber
      subscriber = Subscriber.findByMsisdn(msisdn)
      if (subscriber) {
        // Get event
        events = Event.where {
          subscribers.msisdn == msisdn
        } findAll()
        
        if (!events.size) {
          render status: 404, text: "No event related to MSISDN"
          return
        }
      }
      else {
        render status: 503, text: "MSISDN is not registered"
        return
      }
      
      def message = new Message(
        title: "Receive from Telkomsel MSISDN : " + msisdn,
        content: msg,
        createdDate: new Date(),
        createBy: "From Telkomsel : " + msisdn,
        isReceived: true,
        event: events[0]
      )
      
      if (message.save(flush: true)) {
        render "OK"
      }
      else {
        render status: 503, text: "Can not save the message"
      }
      
    }
    else {
      render status: 417, text: "Expectation Failed : Invalid MSISDN"
    }
    
  }
  
}