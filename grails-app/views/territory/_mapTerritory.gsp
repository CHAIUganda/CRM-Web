<div class="modal fade" id="map-territory" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog " ng-class="{blockui:blockUI==true}">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>

                <h3 class="modal-title">Assign SubCounties To [{{territory.name}}]</h3>
            </div>

            <div id="edit-message" class="first modal-body">

                %{--Errors--}%
                <div class="row">
                    <div class="col-md-12">
                        %{--Errors--}%
                        <div class="alert alert-danger" ng-show="error">{{error}}</div>
                    </div>
                </div>


                %{--District--}%
                <div class="row">
                    <div class="col-md-3 text-right">District</div>

                    <div class="col-md-8">
                        <g:select name="district" from="${districts}"
                                  optionKey="id"
                                  class="col-md-12 chzn-select"
                                  ng-model="districtId"/>
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
                                    <div class="col-md-6">SubCounties</div>

                                    <div class="col-md-6 text-right">
                                        <i class="glyphicon glyphicon-check btn">CheckAll</i>
                                    </div>
                                </div>
                            </div>

                            <div class="panel-body">
                                <div class="col-md-4" ng-repeat="sc in subCounties">
                                    <g:checkBox name="subCounty" ng-model="sc.mapped"/>
                                    <label>{{sc.name}}</label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

