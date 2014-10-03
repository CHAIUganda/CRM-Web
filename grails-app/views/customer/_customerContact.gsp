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
                    <g:message code="customerContact.contact.label" default="Contact"/>
                </label>

                <div><g:textField class='form-control' name="tCustomerContacts[{{\$index}}].contact"
                                  ng-model="c.contact"/></div>
            </div>

            <div>
                <label for="gender" class="control-label">
                    <g:message code="customerContact.gender.label" default="Gender"/>
                </label>

                <div><g:select class='form-control' name="tCustomerContacts[{{\$index}}].gender"
                                from="${['male','female']}"
                                  ng-model="c.gender"/></div>
            </div>

            <div>
                <label for="graduationYear" class="control-label">
                    <g:message code="customerContact.graduationYear.label" default="Graduation Year"/>
                </label>

                <div>
                    <g:field class='form-control' type="number" name="tCustomerContacts[{{\$index}}].graduationYear"
                                 ng-model="c.graduationYear"/>
                </div>
            </div>

            <div>
                <label for="name" class="control-label">
                    <g:message code="customerContact.name.label" default="Name"/>
                </label>

                <div><g:textField class='form-control' name="tCustomerContacts[{{\$index}}].name"
                                  ng-model="c.name"/></div>
            </div>
        </div>

        <div class="col-md-6">

            <div>
                <label for="networkOrAssociation" class="control-label">
                    <g:message code="customerContact.networkOrAssociation.label" default="Network Or Association"/>
                </label>

                <div>
                    <g:textField class='form-control' name="tCustomerContacts[{{\$index}}].networkOrAssociation"
                                 ng-model="c.networkOrAssociation"/>
                </div>
            </div>

            <div>
                <label for="qualification" class="control-label">
                    <g:message code="customerContact.qualification.label" default="Qualification"/>
                </label>

                <div><g:textField class='form-control' name="tCustomerContacts[{{\$index}}].qualification"
                                  ng-model="c.qualification"/></div>
            </div>

            <div>
                <label for="role" class="control-label">
                    <g:message code="customerContact.role.label" default="Role"/>
                </label>

                <div><g:textField class='form-control' name="tCustomerContacts[{{\$index}}].role"
                                  ng-model="c.role"/></div>
            </div>

            <div>
                <label for="typeOfContact" class="control-label">
                    <g:message code="customerContact.typeOfContact.label" default="Type Of Contact"/>
                </label>

                <div><g:select class='form-control' name="tCustomerContacts[{{\$index}}].typeOfContact"
                               from="${['key', 'ordinary']}"
                               value="ordinary"
                               ng-model="c.typeOfContact"/></div>
            </div>
        </div>
    </div>
</div>

<div>
    <button type="button" ng-click="addContact()" class="btn btn-default">Add Contact</button>
</div>
