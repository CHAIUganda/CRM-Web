<div class="modal fade" id="map-supervisor" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog " ng-class="{blockui:blockUI==true}">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>

                <h3 class="modal-title">Assign Territories To [{{territory.name}}]</h3>
            </div>

            <div id="edit-message" class="first modal-body">

                %{--Errors--}%
                <g:render template="/_common/ngError"/>


                %{--District--}%
                <div class="row">
                    <div class="col-md-3 text-right">Territory</div>

                    <div class="col-md-8">
                        <g:select name="territory" from="${territories}"
                                  optionKey="id"
                                  class="col-md-12 chzn-select"
                                  ng-model="territoryId"/>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-12">&nbsp;</div>
                </div>

                %{--SubCounties--}%
                <div class="row">
                    <div class="col-md-12">
                        %{--Panel--}%
                        <div class="panel panel-success">

                            %{--Heading--}%
                            <div class="panel-heading ">
                                <div class="row">
                                    <div class="col-md-6">Territories</div>

                                    <div class="col-md-6 text-right">
                                        <i class="glyphicon glyphicon-check btn" ng-click="onToggleAll()">ToggleAll</i>
                                    </div>
                                </div>
                            </div>

                            <div class="panel-body">
                                <div class="col-md-6" ng-repeat="item in childItems">
                                    <g:checkBox name="subCounty" ng-model="sc.mapped"/>
                                    <label>{{item.name}}
                                        <span ng-show="item.territory && !item.mapped"
                                              class="glyphicon glyphicon-transfer">
                                            {{item.territory}}</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>


                %{-- Button--}%
                <div class="row">
                    <div class="col-md-12">
                        <div class="text-center btn btn-block" ng-click="onSave()">
                            <i class="glyphicon glyphicon-save"></i>Save
                        </div>
                    </div>
                </div>

            </div>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

