grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true
    legacyResolve false

    repositories {
        inherits true

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        runtime 'mysql:mysql-connector-java:5.1.22'
    }

    plugins {
      runtime ":hibernate:$grailsVersion"
      //runtime ":jquery:1.8.3"
      runtime ":resources:1.1.6"

      build ":tomcat:$grailsVersion"

      runtime ":database-migration:1.3.2"

      compile ':cache:1.0.1'
    }
}

coverage {
        exclusions = [
                        '**/*User*',
                        '**/Role*',
                        '**/EventDTO*',
                        '**/Login*',
                        '**/Logout*']
}

codenarc.reports = {
    // Each report definition is of the form:
    //    REPORT-NAME(REPORT-TYPE) {
    //        PROPERTY-NAME = PROPERTY-VALUE
    //        PROPERTY-NAME = PROPERTY-VALUE
    //    }

    /*MyXmlReport('xml') {                    // The report name "MyXmlReport" is user-defined; Report type is 'xml'
        outputFile = 'target/CodeNarc-Report.xml'  // Set the 'outputFile' property of the (XML) Report
        title = 'Sample Report'             // Set the 'title' property of the (XML) Report
    }*/
    MyHtmlReport('html') {                  // Report type is 'html'
        outputFile = 'target/CodeNarc-Report.html'
        title = 'OpenMessenger CoddNarc-Report'
    }
}
