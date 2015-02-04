<h4>{{task.outletName}}</h4>

<p><i>{{task.descriptionOfOutletLocation}}</i></p>

<div class="form-horizontal">

    <div class="form-group">
        <label class="col-md-3 control-label">Priority</label>

        <div class="col-md-9"><p class="form-control-static">{{task.segment}}</p></div>
    </div>

    <div class="form-group">
        <label class="col-md-3 control-label">GPS</label>

        <div class="col-md-9"><p class="form-control-static">{{task.lat }},{{ task.lng}}</p></div>
    </div>

    <div class="form-group">
        <label class="col-md-3 control-label">Outlet Size</label>

        <div class="col-md-9"><p class="form-control-static">{{task.outletSize}}</p></div>
    </div>

    <div class="form-group">

        <div class="col-md-offset-3 col-md-9">

            <button class="btn btn-default"
                    data-target="#CreateTaskModal"
                    data-toggle="modal"
                    ng-click="onCreateNewTask()">Create Task</button>

        </div>
    </div>

</div>

<div id="CreateTaskModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="CreateTaskModalLabel"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>

                <h3 id="DeleteModalLabel">Create New Task for [{{task.outletName}}]</h3>
            </div>

            <div class="modal-body">
                <div class="form-horizontal">

                    <div class="form-group">
                        <div class="col-md-offset-3 col-md-9"><g:render template="/_common/ngError"/></div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label">Moment From Now</label>

                        <div class="col-md-9">
                            <p class="form-control-static">{{momentFromNow(newTask.dueDate)}}</p>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label">Due Date</label>

                        <div class="col-md-9">
                            <bs:datePicker id="newTaskDate" name="newTaskDate" class="form-control"/>
                        </div>
                    </div>
                </div>

            </div>

            <div class="modal-footer">
                <button class="btn" data-dismiss="modal" aria-hidden="true"><g:message
                        code="default.button.cancel.label" default="Cancel"/></button>
                <span class="button">
                    <button class="btn btn-success" ng-click="persistNewTask()">Create</button>
                </span>

            </div>
        </div>
    </div>
</div>