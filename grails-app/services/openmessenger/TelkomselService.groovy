package openmessenger

import com.rabbitmq.client.*

class TelkomselService {

    static transactional = true

    static rabbitQueue = 'openmessenger_telkomsel'

    def telkomselSmsService

    void handleMessage(Map map) {
      telkomselSmsService.sendMessage(map)
    }
}
