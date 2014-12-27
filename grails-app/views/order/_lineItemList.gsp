%{-- Line Items--}%
<div class="row">
    <div class="col-md-1"></div>

    <div class="col-md-10">
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Item</th>
                <th>Quantity</th>
                <th>Unit Price</th>
                <th>Price</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${taskInstance.lineItems}" var="li">
                <tr class="odd" ng-repeat="li in order.lineItems">
                    <td>
                        ${li.product.name}
                    </td>
                    <td>${li.quantity}</td>
                    <td>${li.unitPrice}</td>
                    <td>${li.getLineCost()}</td>

                </tr>
            </g:each>
            <tr class="info">
                <td><strong>Total</strong></td>
                <td></td>
                <td></td>
                <td>${taskInstance?.totalCost()}</td>
            </tr>
            </tbody>
        </table>

    </div>

    <div class="col-md-1"></div>
</div>
