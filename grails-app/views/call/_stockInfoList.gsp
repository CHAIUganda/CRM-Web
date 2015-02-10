%{-- Stock Lines Items--}%

<div class="panel panel-default">
    <div class="panel-heading">
        <strong>Stock Levels</strong>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-md-1"></div>

            <div class="col-md-10">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>Item</th>
                        <th>Quantity</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${taskInstance?.stockLines}" var="li">
                        <tr class="odd" ng-repeat="li in order.stockLines">
                            <td>${li.product.name}</td>
                            <td>${li.quantity}</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>

            </div>

            <div class="col-md-1"></div>
        </div>
    </div>
</div>
