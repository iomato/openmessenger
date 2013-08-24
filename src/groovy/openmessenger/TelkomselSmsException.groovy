package openmessenger

class TelkomselSmsException extends RuntimeException {
    String message
    String errorCode
    
    String toString() {
        "TelkomselSmsException: $message"
    }
}