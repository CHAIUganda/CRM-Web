<table>
    <tr>
        <td class="squarebrackets">

            <div class="form-inline">
                <div class="form-group">
                    <div class="input-group">
                        <div ng-click="deleteConditionGroup(group)"
                             ng-show="!group.isAncestor"
                             class="btn input-group-addon">-</div>
                        <select class="form-control" ng-model="group.binOperator"
                                style="padding-right: 11px; padding-left: 11px;">
                            <option>and</option>
                            <option>or</option>
                        </select>
                    </div>
                </div>
            </div>
        </td>


        <td>
            <div ng-repeat="cnd in group.conditions" class="form-inline anim-repeat-item" >
                <div class="form-group">
                    <select class="form-control" ng-model="cnd.left">
                        <option ng-repeat="col2 in columns">{{col2.emitString()}}</option>
                    </select>
                    <select class="form-control" ng-model="cnd.operator">
                        <option>Equal to</option>
                        <option>Not Equal</option>
                        <option>Less Than</option>
                        <option>Less Than or Equal</option>
                        <option>Greater Than</option>
                        <option>Contains</option>
                    </select>
                    <input type="text" placeholder="Value here" ng-model="cnd.right" class="form-control"/>
                    <button ng-click="deleteCondition(cnd)"
                            class=" glyphicon glyphicon-trash form-control"></button>
                </div>
            </div>

            <div>
                <button ng-click="addCondition(group)"
                        class="btn btn-default glyphicon glyphicon-plus"></button>
                <button ng-click="addConditionGroup(group)"
                        class="btn btn-default glyphicon glyphicon-plus-sign"></button>
            </div>
            <!--Pick starting the other groups-->
            <% myTemplate = createLink(action: 'conditionGroup') %>
            <div ng-repeat="group in group.groups" ng-include="'${myTemplate}'" class="anim-repeat-item">{{data.name}}</div>
        </td>
    </tr>
</table>