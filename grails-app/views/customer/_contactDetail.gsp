<g:each in="${customerInstance?.customerContacts}" var="customerContactInstance" status="i">
    <div class="panel panel-success">

        <div class="panel-heading">Contact-${i + 1}-${customerContactInstance?.names}</div>

        <div class="panel-body">
            <table class="table">
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.names.label"
                                                             default="Name"/></td>

                    <td valign="top" class="value">${fieldValue(bean: customerContactInstance, field: "names")}</td>

                </tr>



                <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.gender.label" default="Gender"/></td>

                    <td valign="top" class="value">${fieldValue(bean: customerContactInstance, field: "gender")}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.contact.label"
                                                             default="Contact"/></td>

                    <td valign="top" class="value">${fieldValue(bean: customerContactInstance, field: "contact")}</td>

                </tr>


                <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.networkOrAssociation.label"
                                                             default="Network Or Association"/></td>

                    <td valign="top"
                        class="value">${fieldValue(bean: customerContactInstance, field: "networkOrAssociationName")}</td>

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

                <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.dateCreated.label"
                                                             default="Date Created"/></td>

                    <td valign="top" class="value"><g:formatDate date="${customerContactInstance?.dateCreated}"/></td>

                </tr>


                <tr class="prop">
                    <td valign="top" class="name"><g:message code="customerContact.lastUpdated.label"
                                                             default="Last Updated"/></td>

                    <td valign="top" class="value"><g:formatDate date="${customerContactInstance?.lastUpdated}"/></td>

                </tr>

                </tbody>
            </table>

        </div>

    </div>
</g:each>

