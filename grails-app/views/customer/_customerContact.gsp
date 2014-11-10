<%@ page import="com.omnitech.chai.model.CustomerContact" %>

<g:set var="customerContactInstance" value="${customerContactInstance as CustomerContact}"/>
<div class="panel panel-success" ng-repeat="c in customerContacts">
    <div class="panel-heading">
        <i class="glyphicon glyphicon-trash btn" ng-click="deleteContact($index)"></i>
        Customer Contact {{$index+1}}
    </div>

    <div class="panel-body">

        <div class="col-md-6">
            <div>
                <label for="contact" class="control-label">
                    <g:message code="customerContact.title.label" default="Contact Title"/>
                </label>

                <div><g:textField class='form-control' name="tCustomerContacts[{{\$index}}].title"
                                  ng-model="c.title"/></div>
            </div>


            <div>
                <label for="contact" class="control-label">
                    <g:message code="customerContact.firstName.label" default="Contact First Name"/>
                </label>

                <div><g:textField class='form-control' name="tCustomerContacts[{{\$index}}].firstName"
                                  ng-model="c.firstName"/></div>
            </div>


            <div>
                <label for="contact" class="control-label">
                    <g:message code="customerContact.surname.label" default="Contact Surname"/>
                </label>

                <div><g:textField class='form-control' name="tCustomerContacts[{{\$index}}].surname"
                                  ng-model="c.surname"/></div>
            </div>



        </div>

        <div class="col-md-6">
            <div>
                <label for="gender" class="control-label">
                    <g:message code="customerContact.gender.label" default="Gender"/>
                </label>

                <div><g:select class='form-control' name="tCustomerContacts[{{\$index}}].gender"
                               from="${['male', 'female']}"
                               ng-model="c.gender"/></div>
            </div>

            <div>
                <label for="networkOrAssociation" class="control-label">
                    <g:message code="customerContact.networkOrAssociation.label" default="Does this person belong to a network or association?"/>
                </label>


                <g:checkBox id="networkOrAssociation" name="tCustomerContacts[{{\$index}}].networkOrAssociation"
                            ng-model="c.networkOrAssociation">KKsks</g:checkBox>

            </div>

            <div>
                <label for="qualification" class="control-label">
                    <g:message code="customerContact.qualification.label" default="Qualification"/>
                </label>

                <div><g:textField class='form-control qualification' name="tCustomerContacts[{{\$index}}].qualification"
                                  ng-model="c.qualification"/></div>
            </div>

            <div>
                <label for="role" class="control-label">
                    <g:message code="customerContact.role.label" default="Role"/>
                </label>

                <div><g:textField class='form-control retailer-role' name="tCustomerContacts[{{\$index}}].role"
                                  ng-model="c.role"/></div>
            </div>


        </div>
    </div>
</div>

<div>
    <button type="button" ng-click="addContact()" class="btn btn-default">Add Contact</button>
</div>

<r:require module="jqueryUI"/>
<g:javascript>
    $(function () {
        var availableTags = ['Pharmacist', 'Doctor', 'Owner',
            'Purchasing Officer', 'Shop Attendant',
            'Relative', 'Dispenser', 'lab technician',
            'Nurse', 'Midwife', 'Medical assistant'];
        $(".retailer-role").autocomplete({
            source: availableTags, delay: 0
        });
    });
</g:javascript>
