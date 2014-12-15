<%@ page import="com.omnitech.chai.model.Task" %>


%{-- Customer Details Sections --}%
<div class="panel panel-success" >
    <div class="panel panel-heading">Select Customer</div>

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

            <div class="row">
                <div class="col-lg-2"><strong>Outlet Name:</strong></div>

                <div class="col-lg-2">{{order.customer.outletName}}</div>

                <div class="col-lg-1"><strong>Location:</strong></div>

                <div class="col-lg-2">{{order.customer.district}}</div>

                <div class="col-lg-2"><strong>Key Contact:</strong></div>

                <div class="col-lg-2">{{order.customer.contact}}</div>
            </div>
        </div>

    </div>
</div>

%{-- Order Entries --}%
<div class="panel panel-success">
    <div class="panel panel-heading">Order Details</div>

    <div class="panel panel-body">
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
                    <i class="glyphicon glyphicon-edit"></i>
                    <i class="glyphicon glyphicon-trash"></i>
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
        <div class="btn btn-default" data-toggle="modal" data-target="#line-item-form" ng-click="addLineItem()">Add Item</div>
    </div>
</div>

%{-- Order Entries --}%
<div class="panel panel-success">
    <div class="panel panel-heading">Other Details</div>

    <div class="panel panel-body">
        <div class="form-horizontal">
            <div class="form-group">
                <label for="comment" class="col-sm-1 control-label">Comment</label>

                <div class="col-sm-11">
                    <textarea id="comment" class="form-control"></textarea>
                </div>
            </div>
        </div>
    </div>
</div>


%{-- Order Form --}%
<g:render template="lineItemForm"/>

