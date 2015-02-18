<div id="legend" class="col-md-2" style="background: #ffffff;">

    <div class="row" ng-click="onLegendFilter('t.dueDays <= -1')">
        <div class="col-sm-1 btn" style="background: #e41a1c; height: 20px;"></div>

        <div class="col-sm-8" style="padding-left: 0;padding-right: 0;">Outstanding</div>
    </div>

    <div class="row" ng-click="onLegendFilter('t.dueDays == 0')">
        <div class="col-sm-1 btn" style="background: #377eb8; height: 20px"></div>

        <div class="col-sm-8" style="padding-left: 0;padding-right: 0;">Today</div>
    </div>

    <div class="row" ng-click="onLegendFilter('t.dueDays == 1')">
        <div class="col-sm-1 btn" style="background: #4daf4a; height: 20px"></div>

        <div class="col-sm-8 " style="padding-left: 0;padding-right: 0;">1 day</div>
    </div>

    <div class="row" ng-click="onLegendFilter('t.dueDays == 2')">
        <div class="col-sm-1 btn" style="background: #984ea3; height: 20px"></div>

        <div class="col-sm-8" style="padding-left: 0;padding-right: 0;">2 days</div>
    </div>

    <div class="row" ng-click="onLegendFilter('t.dueDays == 3')">
        <div class="col-sm-1 btn" style="background: #ff7f00; height: 20px"></div>

        <div class="col-sm-8" style="padding-left: 0;padding-right: 0;">3 days</div>
    </div>

    <div class="row" ng-click="onLegendFilter('t.dueDays == 4')">
        <div class="col-sm-1 btn" style="background: #ffff33; height: 20px"></div>

        <div class="col-sm-8" style="padding-left: 0;padding-right: 0;">4 days</div>
    </div>

    <div class="row" ng-click="onLegendFilter('t.dueDays == 5')">
        <div class="col-sm-1 btn" style="background: #a65628; height: 20px"></div>

        <div class="col-sm-8" style="padding-left: 0;padding-right: 0;">5 days</div>
    </div>

    <div class="row" ng-click="onLegendFilter('t.dueDays == 6')">
        <div class="col-sm-1 btn" style="background: #f781bf; height: 20px"></div>

        <div class="col-sm-8" style="padding-left: 0;padding-right: 0;">6 days</div>
    </div>

    <div class="row" ng-click="onLegendFilter('t.dueDays == 7')">
        <div class="col-sm-1 btn" style="background: #999999; height: 20px"></div>

        <div class="col-sm-8" style="padding-left: 0;padding-right: 0;">7 days</div>
    </div>

    <div class="row" ng-click="onLegendFilter('t.dueDays >= 8 && t.type == \'task\'')">
        <div class="col-sm-1 btn" style="background: #000000; height: 20px"></div>

        <div class="col-sm-8" style="padding-left: 0;padding-right: 0;">8 days or more</div>
    </div>

    <div class="row " ng-click="onLegendFilter('t.type == \'customer\'')">
        <div class="col-sm-1 btn" style="height: 50px;padding-left: 0;">
            <g:img uri="http://chart.apis.google.com/chart?chst=d_map_xpin_letter&chld=pin_star|O|4B0082|FFFFFF|ffff33"/>
        </div>

        <div class="col-sm-8" style="padding-right: 0;">Other Customers</div>
    </div>

    <div class="row " ng-click="onLegendFilter('t.status == \'complete\' && t.type == \'task\'')">
        <div class="col-sm-1 btn" style="height: 50px;padding-left: 0;">
            <g:img uri="http://chart.apis.google.com/chart?chst=d_map_xpin_letter&chld=pin_star|O|4daf4a|FFFFFF|ffff33"/>
        </div>

        <div class="col-sm-8" style="padding-right: 0;">Complete</div>
    </div>
</div>