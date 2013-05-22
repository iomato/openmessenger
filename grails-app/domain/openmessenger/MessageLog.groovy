package openmessenger

import java.util.Date;
import openmessenger.Event.Type

class MessageLog {
  Event event
  Type eventType 
  String msisdn
  String gateway
  String msg
  Integer concatinationSize
  Float price
  String createBy
  Date date
  Date dateCreated
  Date lastUpdated

  //static belongsTo = [event:Event]
  
    static constraints = {
    event(nullable: false)
    eventType(nullable: false)
    msisdn(nullable: false)
    gateway(nullable: false)
    msg(nullable: false)
    createBy(nullable: false)
    date(nullable: false)
    concatinationSize(nullable: false)
    }
  
  static mapping = {
    sort dateCreated:"desc"
  }
    
}
