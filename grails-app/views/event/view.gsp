<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="openmessenger.Event.Type" %>
<%@ page import="openmessenger.GroupChat" %>
<html lang="en-US" xml:lang="en-US" xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>${event.name} | Open Messenger</title>
        <meta name="layout" content="main" />

        <r:require module='event'/>
        <r:require module='modal'/>

    </head>

    <body>
        <div class="row">
            <div class="span12">
                <div class="wrapper">

                    <div class="page-header">

                        <h1>${event.name} <small> All your messages is here</small></h1>
                    </div>
                </div> <!-- wrapper -->
            </div> <!-- span12 -->
        </div> <!-- row -->

        <div class="row" ng-app="eventPage" ng-init="defaultEvent=${event.id}" ng-controller="EventsCtrl">
            <div class="span8">
                <div class="wrapper wrapper-rborder">

                    <div class="well">
                        <h2>Send message</h2>

                        <form class="form-vertical" id="send-message" method="post" action="../sendMessage">



                            <div id="select-events-modal" class="modal hide fade">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                                    <h3>Which event do you want to send message?</h3>
                                </div>
                                <div class="modal-body">
                                    <ul class="unstyled">
                                        <li>
                                            <input type="checkbox" ng-model="selectedAll" ng-click="selectAllEvents()">All
                                            </input>
                                        </li>
                                        <li class="divider"></li>
                                        <li ng-repeat="event in events">
                                            <input type="checkbox" name="eventIds" ng-model="event.isChecked" ng-checked="event.isChecked || event.id==defaultEvent" ng-click="selectEvent()" ng-disabled="event.id==defaultEvent" value="{{event.id}}">{{event.name}}
                                            </input>
                                        </li>
                                        <li class="divider"></li>
                                        <li>
                                            <input type="checkbox" ng-model="selectedAll" ng-click="selectAllEvents()">All
                                            </input>
                                        </li>
                                    </ul>
                                    <button class="btn btn-primary" style="clear:right" type="button" class="close" data-dismiss="modal" aria-hidden="true">Ok</button>
                                    <button class="btn" type="button" ng-click="resetEvents()">Reset</button>
                                </div>
                            </div>

                            <div id="select-tags-modal" class="modal hide fade">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                                    <h3 ng-show="tags.length>0">Which tag do you want to add to message?</h3>
                                    <sec:ifAnyGranted roles="ROLE_ADMINS,ROLE_MANAGER">
                                    <input type="text" size="30"
                                    placeholder="add new tag here" ng-model="newTag" ng-change="checkNewTag()" />
                                    <input class="btn" type="button" ng-click="addNewTag()" value="add" />
                                    <span ng-show="addStatus" class="help-inline">
                                    {{errorMessage}}</span>
                                    </sec:ifAnyGranted>
                                </div>
                                <div class="modal-body">
                                    <ul class="unstyled">
                                        <li ng-show="tags.length>0">
                                            <input type="checkbox" ng-model="selectedAllTag" ng-click="selectAllTags()">All
                                            </input>
                                        </li>
                                        <li class="divider"></li>
                                        <li ng-repeat="tag in tags">
                                            <input type="checkbox" id="tags" name="tags" ng-model="tag.isChecked" ng-checked="tag.isChecked" ng-click="selectTag()" value="{{tag.name}}">{{tag.name}}
                                            </input>
                                        </li>
                                        <li class="divider"></li>
                                        <li ng-show="tags.length>0">
                                            <input type="checkbox" ng-model="selectedAllTag" ng-click="selectAllTags()">All
                                            </input>
                                        </li>
                                    </ul>
                                    <button class="btn btn-primary" style="clear:right" type="button" class="close" data-dismiss="modal" aria-hidden="true">Ok</button>
                                    <button class="btn" type="button" ng-click="resetTags()">Reset</button>
                                </div>
                            </div>


                            <fieldset>

                    			<g:hiddenField name="eventId" value="${event?.id}" />
                                <div class="control-group">
                        			<textarea id="textarea2" class="input-xlarge span7" rows="3" name="message" >${message}</textarea>
                                </div>
                                <span id="counter">${grailsApplication.config.openmessenger.message.limit}</span>
                    			<button id="sendButton" class="btn btn-primary" style="clear:right" type="submit">Send</button>
                    			<button class="btn" type="reset">Cancel</button>
                            </fieldset>
                  	    </form>
                    </div> <!-- well -->

                <header class="event-timeline">
                    <ul class="nav nav-tabs">
                      <li class="${timelineActive}">
                        <g:link
                          controller="event"
                          action="view"
                          id="${event.id}"><h2>Timeline</h2></g:link>
                      </li>
                      <li class="${replyActive}">
                        <g:link
                          controller="event"
                          action="view"
                          params="[reply:1]"
                          id="${event.id}"><h2>Reply</h2></g:link>
                      </li>
                    </ul>
                    <g:link
                        controller="event"
                        action="export"
                        id="${event.id}"
                        class="btn pull-right">Export Timeline</g:link>
                </header>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th class="header"></th>
                            <th class="blue header">Content</th>
                            <th class="blue header">Status</th>
                            <th class="blue header">Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <g:each in="${messages}" var="message" status="i">
                            <tr>
                                <td>${offset+i+1}</td>
                                <td>
                                    <p class="msg-word-warp">${message.content}</p>
                                    <small>${message.createBy}</small>
                                </td>
                                <td><span class="label label-success">Normal</span></td>
                                <td><p><g:formatDate format=" MMM dd, yyyy" date="${message.createdDate}"/></p></td>
                            </tr>
                        </g:each>
                    </tbody>
                </table>
                <div class="pagination">
                	<msngr:paginate id="${event?.id}" action="view" max="10" prev="&larr; Previous" next="Next &rarr;" total="${total}" />
                </div>
                </div> <!-- wrapper wrapper-rborder -->
            </div> <!-- span8 -->
            <div class="span4">
                <div class="wrapper wrapper-rsidebar">
                <div class="well">
                    <h3>Information</h3>
                    <ul class="list-sidebar unstyled">
                        <li>
                            <i class="icon-search"></i>
                            <b>${event.description}</b>
                        </li>
                        <li>
                            <i class="icon-calendar"></i>
                            <b>Created Date:</b><g:formatDate format=" MMM dd, yyyy" date="${event.occuredDate}"/>
                        </li>
                        <li>
                            <i class="icon-user"></i>
                            <g:link controller="event" action="listEventSubscribers" id="${event.id}"><b>Number of subscriber:</b></g:link>${event.subscribers.size()}
                        </li>
                    </ul>
                </div>
                </div>
            </div> <!-- span4-->
        </div> <!-- row -->

        <g:javascript>
        $(document).ready(function() {
            function checkMessageLength() {
                var len = $('#textarea2').val().length;
                var limit = ${grailsApplication.config.openmessenger.message.limit};
                var remain = limit - len;
                $('#counter').text(remain);
                if(remain==limit) {
                    $('#sendButton').attr('disabled', true);
                } else if(remain<0) {
                    $('#sendButton').attr('disabled', true);
                    $('#counter').css('color', 'red');
                } else {
                    $('#sendButton').attr('disabled', false);
                    $('#counter').css('color', '#333333');
                }
            }

            checkMessageLength();

            $('#textarea2').keyup(function(e) {
                checkMessageLength();
            });
        });
        </g:javascript>
    </body>
</html>