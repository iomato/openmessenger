// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

import grails.plugins.springsecurity.SecurityConfigType;

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// set per-environment serverURL stem for creating absolute links
environments {
  production {
    grails.serverURL = "http://messenger.opendream.org"
    grails.app.context = "http://messenger.opendream.org"

    rabbitmq {
      connectionfactory {
        username = 'guest'
        password = 'guest'
        hostname = 'localhost'
        consumers = 5
      }
      queues = {
        openmessenger()
        eventCallback()
        openmessenger_telkomsel()
      }
    }

    // sms gateway configuration
    sms {
      gateway {
        suri     = 'https://api.clickatell.com'
        uri      = 'http://api.clickatell.com'
        path     = '/http/sendmsg'
        auth     = '/http/auth'
        ping     = '/http/ping'
        coverage = '/utils/routeCoverage.php'
        apiId    = 'defaultApiId'
        user     = 'defaultUser'
        password = 'defaultPassword'
        senderId = 'defaultSenderId'
      }
    }
    
    telkomsel {
      sms {
        gateway {
          uri = 'http://202.3.208.142:9002'
          path = '/submit.jsp'
          username = 'oxfam'
          password = 'oxfam123'
          senderId = 'openmsngr'
        }
      }
    }
  }

  development {
    grails.app.context = "/"

    // rabbitMQ Configuration
    rabbitmq {
      connectionfactory {
        username = 'guest'
        password = 'guest'
        hostname = 'localhost'
        consumers = 5
      }
      queues = {
        openmessenger()
        eventCallback()
        //openmessenger_dtac()
        openmessenger_telkomsel()
      }
    }

    // sms gateway configuration
    sms {
      gateway {
        suri     = 'http://localhost:3000'
        uri      = 'http://localhost:3000'
        path     = '/http/sendmsg'
        auth     = '/http/auth'
        ping     = '/http/ping'
        coverage = '/utils/routeCoverage.php'
        apiId    = 'defaultApiId'
        user     = 'defaultUser'
        password = 'defaultPassword'
        senderId = 'defaultSenderId'
      }
    }
    
    telkomsel {
      sms {
        gateway {
          uri = 'http://localhost:3000'
          path = '/'
          username = 'oxfam'
          password = 'oxfam123'
          senderId = 'openmsngr'
        }
      }
    }
  }

  test {
    grails.serverURL = "http://localhost:8080/${appName}"
    grails.app.context = "/"

    // rabbitMQ Configuration
    rabbitmq {
      connectionfactory {
        username = 'guest'
        password = 'guest'
        hostname = 'localhost'
        consumers = 5
      }
      queues = {
        openmessenger()
        eventCallback()
        //openmessenger_dtac()
      }
    }

    // sms gateway configuration
    sms {
      gateway {
        suri     = 'http://localhost:8090'
        uri      = 'http://localhost:8090'
        path     = '/clickatell-mocker/http/sendmsg'
        auth     = '/clickatell-mocker/http/auth'
        ping     = '/clickatell-mocker/http/ping'
        coverage = '/clickatell-mocker/utils/routeCoverage.php'
        apiId    = 'defaultApiId'
        user     = 'defaultUser'
        password = 'defaultPassword'
        senderId = 'defaultSenderId'
      }
    }
  }

}

// sms gateway configuration
sms.gateway.inactivity = 600000 // 10 mins

openmessenger {
  eventCallback = 'eventCallback'
  prefixSize = 4
  message {
    limit = 140
  }
}

/** SSL truststore configuration key */
//rest.https.truststore.path = 'web-app/certs/truststore.jks'
/** SSL keystore configuration key */
//rest.https.keystore.path='web-app/certs/keystore.jks'
/** SSL keystore password configuration key */
//rest.https.keystore.pass='changeme'
/** Certificate Hostname Verifier configuration key */
rest.https.cert.hostnameVerifier = 'ALLOW_ALL'
/** Enforce SSL Socket Factory */
//rest.https.sslSocketFactory.enforce = true

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName='openmessenger.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName='openmessenger.UserRole'
grails.plugins.springsecurity.authority.className='openmessenger.Role'

//grails.plugins.springsecurity.dao.reflectionSaltSourceProperty=''
grails.plugins.springsecurity.rememberMe.persistent = true
grails.plugins.springsecurity.rememberMe.persistentToken.domainClassName = 'openmessenger.PersistentLogin'

grails.plugins.springsecurity.securityConfigType = SecurityConfigType.InterceptUrlMap
grails.plugins.springsecurity.interceptUrlMap = [
  '/sec/**':            ['IS_AUTHENTICATED_FULLY'],
  '/user/view/**':        ['ROLE_ADMINS','ROLE_MANAGER','ROLE_USER'],
  '/user/edit/**':        ['ROLE_ADMINS','ROLE_MANAGER','ROLE_USER'],
  '/user/create/**':        ['ROLE_ADMINS'],
  '/user/delete/**':        ['ROLE_ADMINS'],
  '/user/list/**':        ['ROLE_ADMINS'],
  '/role/**':         ['ROLE_ADMINS'],
  '/report/**':       ['ROLE_ADMINS'],
  '/event/**':        ['ROLE_ADMINS','ROLE_MANAGER','ROLE_USER'],
  '/home/**':         ['ROLE_ADMINS','ROLE_MANAGER','ROLE_USER'],
  '/groupChat/**':            ['ROLE_ADMINS','ROLE_MANAGER'],
  '/api/**':            ['IS_AUTHENTICATED_ANONYMOUSLY'],
  '/js/**':               ['IS_AUTHENTICATED_ANONYMOUSLY'],
  '/css/**':              ['IS_AUTHENTICATED_ANONYMOUSLY'],
  '/images/**':           ['IS_AUTHENTICATED_ANONYMOUSLY'],
  '/*':                   ['IS_AUTHENTICATED_ANONYMOUSLY'],
  '/login/**':            ['IS_AUTHENTICATED_ANONYMOUSLY'],
  '/logout/**':           ['IS_AUTHENTICATED_ANONYMOUSLY'],
  '/console**':         ['ROLE_ADMINS']
 ]

grails.plugins.springsecurity.secureChannel.definition = [
  '/login/**': 'REQUIRES_SECURE_CHANNEL',
  '/role/**': 'REQUIRES_SECURE_CHANNEL',
  '/user/**': 'REQUIRES_SECURE_CHANNEL',
  '/subscriber/**': 'REQUIRES_SECURE_CHANNEL',
  '/j_spring_security_check': 'REQUIRES_SECURE_CHANNEL',
  '/home/main/**':  'REQUIRES_INSECURE_CHANNEL',
  '/index.gsp':  'REQUIRES_INSECURE_CHANNEL',
  '/event/**': 'REQUIRES_INSECURE_CHANNEL',
  '/api/auth/**': 'REQUIRES_SECURE_CHANNEL',
  '/api/ping/**': 'REQUIRES_INSECURE_CHANNEL',
  '/api/event/list/**': 'REQUIRES_INSECURE_CHANNEL',
  '/api/event/subscribers/**': 'REQUIRES_INSECURE_CHANNEL',
  '/api/event/sendmessage/**': 'REQUIRES_INSECURE_CHANNEL',
  '/api/event/sendPersonalMessage/**': 'REQUIRES_INSECURE_CHANNEL',
  '/api/event/messages/**': 'REQUIRES_INSECURE_CHANNEL',
  '/console**': 'REQUIRES_SECURE_CHANNEL'
]
