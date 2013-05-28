angular.module('eventPage', ['ngResource']).
    factory('GetEvents', function ($resource) {
        var GetEvents = $resource('/OpenMessenger/event/getEvents');        
        return GetEvents;
    }).
    factory('GetTags', function ($resource) {
        var GetTags = $resource('/OpenMessenger/tag/list');        
        return GetTags;
    }).
    factory('AddTag', function ($resource) {
        var AddTag = $resource('/OpenMessenger/tag/create');        
        return AddTag;
    });


function EventsCtrl($scope, GetEvents, GetTags, AddTag) {
    $scope.newTag = "";
    $scope.events = GetEvents.query();
    $scope.tags = GetTags.query();
    rearrangeCheckboxs($scope.events, isChecked($scope.selectedAll, $scope.defaultEvent));

    // event actions
    $scope.selectAllEvents = function() {
        rearrangeCheckboxs($scope.events, isChecked($scope.selectedAll, $scope.defaultEvent));
    }

    $scope.selectAllTags = function() {
        rearrangeCheckboxs($scope.tags, isChecked($scope.selectedAllTag));
    }

    $scope.selectEvent = function() {
        $scope.selectedAll = doCheck($scope.events);
    }

    $scope.selectTag = function() {
        $scope.selectedAllTag = doCheck($scope.tags);
    }

    $scope.resetEvents = function() {
        $scope.selectedAll = false;
        rearrangeCheckboxs($scope.events, isChecked($scope.selectedAll, $scope.defaultEvent));
    }

    $scope.resetTags = function() {
        $scope.selectedAllTag = false;
        rearrangeCheckboxs($scope.tags, isChecked($scope.selectedAllTag));
    }

    $scope.addNewTag = function() {        
        var tag = $scope.newTag;
        var options = {name:tag};
        console.log(tag);
        if(tag=="") {
            $scope.addStatus = true;
            $scope.errorMessage = 'require!';
        } else {
            $scope.addStatus = false;
            AddTag.get({name:tag}, function(result) {
                console.log(result);
                console.log(result.status);
                if(result.status==0) {
                    $scope.addStatus = true;
                    $scope.errorMessage = result.message;
                } else if(result.status==1) {
                    $scope.addStatus = false;
                    $scope.errorMessage = "";
                    $scope.newTag = "";
                    $scope.tags.push({name:result.name});
                    $scope.tags.sort(function(a, b) { return a.name > b.name ;});
                }
            });
        } 
    }

    $scope.checkNewTag = function() {
        if($scope.newTag!="") {
            $scope.addStatus = false;
        }
    }

    // function
    function isChecked(isSelectedAll, defaultEvent=null) {
        return function(checkbox) {
            if(defaultEvent) {    
                return (isSelectedAll || checkbox.id==defaultEvent);
            } else {
                return isSelectedAll;
            }
        } ;           
    }

    function doCheck(targets) {
        var count = 0;
        angular.forEach(targets, function(target) {
                count += target.isChecked? 1 : 0;            
        });
        return (targets.length == count)
    }

    function rearrangeCheckboxs(events, isChecked) {
        angular.forEach(events, function(event) {
            if(isChecked(event)) {
                event.isChecked = true;
            } else {
                event.isChecked = false;
            }
        });
    }    
}