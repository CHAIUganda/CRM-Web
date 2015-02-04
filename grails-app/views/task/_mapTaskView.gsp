<h4>{{task.customer}}</h4>

<p><i>{{task.customerDescription}}</i></p>

<div class="form-horizontal">
    <div class="form-group">
        <label class="col-md-3 control-label">Task</label>

        <div class="col-md-9"><p class="form-control-static">{{task.description}}</p></div>
    </div>

    <div class="form-group">
        <label class="col-md-3 control-label">Priority</label>

        <div class="col-md-9"><p class="form-control-static">{{task.segment}}</p></div>
    </div>

    <div class="form-group">
        <label class="col-md-3 control-label">GPS</label>

        <div class="col-md-9"><p class="form-control-static">{{task.lat }},{{ task.lng}}</p></div>
    </div>

    <div class="form-group">
        <label class="col-md-3 control-label">Assigned User(s)</label>

        <div class="col-md-9"><p class="form-control-static">{{task.assignedUser}}</p></div>
    </div>

    <div class="form-group">
        <label class="col-md-3 control-label">Moment From Now</label>

        <div class="col-md-9"><p class="form-control-static">{{momentFromNow()}}</p></div>
    </div>

    <div class="form-group">
        <label class="col-md-3 control-label">Set Due Date</label>

        <div class="col-md-9">

            <div class="input-group">
                <input id="dueDateText" name="dueDate" class="datepicker form-control" size="16"
                       type="text" data-date-format="yyyy-mm-dd" ng-model="text.dueDateText">
                <span class="input-group-btn">
                    <button class="btn btn-default" type="button"
                            ng-click="persistDueDate()">Save!</button>
                </span>
            </div>

        </div>
    </div>

    <div class="form-group">
        <div class="col-md-offset-3 col-md-9"><g:render template="/_common/ngError"/></div>
    </div>

</div>