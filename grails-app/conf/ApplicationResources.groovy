modules = {
    /*angular {
        resource url: 'js/angularjs/angular.min.js'
        resource url: 'js/angularjs/angular-bootstrap.min.js'
        resource url: 'js/angularjs/angular-cookies.min.js'
        resource url: 'js/angularjs/angular-loader.min.js'
        resource url: 'js/angularjs/angular-resource.min.js'
        resource url: 'js/angularjs/angular-sanitize.min.js'
    }*/

    event {
        dependsOn 'angular'
        resource url: 'js/page/eventPage.js'
    }

    modal {
        resource url: 'js/bootstrap-modal.js'   
    }
}