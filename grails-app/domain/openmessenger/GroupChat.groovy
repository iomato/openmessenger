package openmessenger

class GroupChat extends Event {
	//String codename
	
    static constraints = {
		codename(size:1..7, nullable: false, unique:true)
    }

    def beforeInsert() {
        codename = codename.toLowerCase()
   } 
}
