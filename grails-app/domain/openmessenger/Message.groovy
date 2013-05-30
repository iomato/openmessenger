package openmessenger

import org.grails.taggable.*

class Message implements Taggable {
    String title
    String content     
    Date createdDate
  String createBy
        
    static constraints = {
        title(nullable: false)
        content(nullable: false)
        createdDate(nullable: false)
    createBy(nullable: true)
    }
    
    static belongsTo = [event:Event]
}
