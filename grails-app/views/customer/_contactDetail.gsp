<g:each in="${customerInstance?.customerContacts}" var="customerContactInstance" status="i">
    <div class="panel panel-success">

        <div class="panel-heading">Contact-${i + 1}-${customerContactInstance?.firstName}</div>

        <div class="panel-body">
            <table class="table">
                <tbody>

                  <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.dateCreated.label"
                                                             default="Date Created"/></td>

                    <td valign="top" class="value"><g:formatDate date="${customerContactInstance?.dateCreated}"/></td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.gender.label" default="Gender"/></td>

                    <td valign="top" class="value">${fieldValue(bean: customerContactInstance, field: "gender")}</td>

                </tr>


                <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.lastUpdated.label"
                                                             default="Last Updated"/></td>

                    <td valign="top" class="value"><g:formatDate date="${customerContactInstance?.lastUpdated}"/></td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.surnname.label" default="Surname"/></td>

                    <td valign="top" class="value">${fieldValue(bean: customerContactInstance, field: "surname")}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.networkOrAssociation.label"
                                                             default="Network Or Association"/></td>

                    <td valign="top"
                        class="value">${fieldValue(bean: customerContactInstance, field: "networkOrAssociation")}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.qualification.label"
                                                             default="Qualification"/></td>

                    <td valign="top"
                        class="value">${fieldValue(bean: customerContactInstance, field: "qualification")}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.role.label" default="Role"/></td>

                    <td valign="top" class="value">${fieldValue(bean: customerContactInstance, field: "role")}</td>

                </tr>



                </tbody>
            </table>

        </div>

    </div>
</g:each>

