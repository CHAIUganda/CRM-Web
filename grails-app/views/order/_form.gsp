<%@ page import="com.omnitech.chai.model.Task" %>


%{-- Customer Details Sections --}%
<div class="panel panel-success" ng-controller="OrderCtrl">
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

                <div class="col-lg-2">B-Y Pharmacy</div>

                <div class="col-lg-1"><strong>Location:</strong></div>

                <div class="col-lg-2">Tororo</div>

                <div class="col-lg-2"><strong>Key Contact:</strong></div>

                <div class="col-lg-2">256-712-789-965</div>
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
            <tr class="odd">
                <td><i class="glyphicon glyphicon-edit"></i><i class="glyphicon glyphicon-trash"></i>Fansidar</td>
                <td>3</td>
                <td>2000</td>
                <td>6000</td>

            </tr>
            <tr class="even">
                <td><i class="glyphicon glyphicon-edit"></i><i class="glyphicon glyphicon-trash"></i>Fansidar</td>
                <td>3</td>
                <td>2000</td>
                <td>6000</td>
            </tr>

            <tr class="info">
                <td><strong>Total</strong></td>
                <td></td>
                <td></td>
                <td>20000</td>
            </tr>
            </tbody>
        </table>
        <button class="btn badge-important" data-toggle="modal" data-target="#line-item-form">Add Item</button>
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

<r:require modules="angular,angular-resource,angular-ui"/>
<g:javascript src="services/Common.js"/>
<g:javascript src="controllers/OrderCtrl.js"/>