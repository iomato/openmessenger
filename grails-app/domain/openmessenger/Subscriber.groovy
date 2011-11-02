package openmessenger

class Subscriber {
    String msisdn
    String active
	//Boolean support
	Gateway gateway

	static transients = ['msisdnx']
	
	String getMsisdnx(){
		return msisdn.substring(0, msisdn.length()-4).concat('XXXX')
	}
	
    static constraints = {
        msisdn(nullable: false, size:10..15, unique:true)
        active(nullable: false, inList: ['Y','N'])
		gateway(nullable: true)
    }
    static mapping = {
        sort msisdn:"desc"
    }
}
