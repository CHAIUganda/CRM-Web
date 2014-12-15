<%@ page import="com.omnitech.chai.model.Task" %>


%{-- Customer Details Sections --}%
<div class="panel panel-success">
    <div class="panel panel-heading">Customer Order</div>

    <div class="panel panel-body">

        <div class="form-horizontal">
            <div class="form-group">
                <label class="col-sm-5 control-label">Search</label>

                <div class="col-sm-5">
                    <input type="text" placeholder="Search Term..."
                           ng-model="cutomerObj"
                           typeahead="customer.outletName for customer in searchCustomerByName($viewValue) | filter:$viewValue"
                           typeahead-editable="false" typeahead-on-select="onSelectCustomer($item)">

                </div>
            </div>

            %{-- Customer Details --}%
            <div class="row">
                <div class="col-md-1"></div>

                <div class="col-lg-4"><strong>Outlet Name:</strong> {{order.customer.outletName}}</div>


                <div class="col-lg-3"><strong>Location:</strong>{{order.customer.district}}</div>


                <div class="col-lg-4"><strong>Key Contact:</strong> {{order.customer.contact}}</div>

                <div class="col-md-1"></div>
            </div>
        </div>

        <hr/>

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
                    <tr class="odd" ng-repeat="li in order.lineItems">
                        <td>
                            <a class="glyphicon glyphicon-edit" href="#"></a>
                            <a class="glyphicon glyphicon-trash text-danger" href="#" ng-click="deleteLine($index)"></a>
                            {{li.product.name}}
                        </td>
                        <td>{{li.quantity}}</td>
                        <td>{{li.unitPrice}}</td>
                        <td>{{lineCost(li)}}</td>

                    </tr>
                    <tr class="info">
                        <td><strong>Total</strong></td>
                        <td></td>
                        <td></td>
                        <td>{{orderCost(order)}}</td>
                    </tr>
                    </tbody>
                </table>

                <div class="btn btn-default" data-toggle="modal" data-target="#line-item-form"
                     ng-click="createLineItem()">Add Item</div>
            </div>

            <div class="col-md-1"></div>
        </div>

        <hr/>

        %{-- Comments --}%
        <div class="row">
            <div class="col-md-1"></div>

            <div class="col-md-10">
                <div class="form-group">
                    <label for="comment" class="col-sm-1 control-label">Comment</label>

                    <div class="col-sm-11">
                        <textarea id="comment" class="form-control"></textarea>
                    </div>
                </div>
            </div>

            <div class="col-md-1"></div>
        </div>

    </div>
</div>



%{-- Order Form --}%
<g:render template="lineItemForm"/>

