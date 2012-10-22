angular.module('eventPage', ['ngResource']).
factory('GetEvents', function ($resource) {
        var GetEvents = $resource('/OpenMessenger/event/getEvents');        
        return GetEvents;
    });


function EventsCtrl($scope, GetEvents) {
    $scope.events = GetEvents.query();
    checkEvents($scope.events);

    $scope.selectAllEvents = function() {
        checkEvents($scope.events);
    }

    $scope.selectEvent = function() {
        var count = 0;
        angular.forEach($scope.events, function(event) {            
                count += event.isChecked? 1 : 0;            
        });
        if($scope.events.length == count) {
            $scope.selectedAll = true;
        } else {
            $scope.selectedAll = false;
        }
    }

    $scope.resetEvents = function() {
        $scope.selectedAll = false;
        checkEvents($scope.events);
    }

    function checkEvents(events) {
        angular.forEach(events, function(event) {
            if($scope.selectedAll || event.id==$scope.defaultEvent) {
                event.isChecked = true;
            } else {
                event.isChecked = false;
            }
        });
    }
}