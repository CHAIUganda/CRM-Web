<%@ page import="com.omnitech.chai.model.Village" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'village.label', default: 'Village')}"/>
    <title><g:message code="default.index.label" args="[entityName]"/></title>
</head>

<body>

<section id="index-village" class="first">

    <table class="table table-bordered margin-top-medium">
        <thead>
        <tr>

            <g:sortableColumn property="name" title="${message(code: 'village.name.label', default: 'Name')}"/>


            <td>
                Action
            </td>
        </tr>
        </thead>
        <tbody>
        <g:each in="${villageInstanceList}" status="i" var="villageInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <td><g:link action="show"
                            id="${villageInstance.id}">${fieldValue(bean: villageInstance, field: "name")}</g:link></td>


                <td>
                    <g:link action="edit" id="${villageInstance.id}"><i class="glyphicon glyphicon-pencil"></i></g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div>
        <bs:paginate total="${villageInstanceCount}"/>
    </div>
</section>

</body>

</html>
