<%@ page import="com.omnitech.chai.model.Report" %>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="kickstart"/>
    <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}"/>
    <title>Simple Report Filter</title>
</head>

<body>
<g:form class="form-horizontal" action="getReport" method="get">
    <g:hiddenField name="id" value="${report.id}"/>
    <g:each in="${filters}" var="f">
        <div class="form-group">
            <label class="col-md-3 control-label">${f.fieldDescription}</label>

            <div class="col-md-9">

                %{-- Number --}%
                <g:if test="${f.fieldType == Number}">
                    <g:field type="number" name="${f.fieldName}" class="form-control"/>
                </g:if>

                %{-- Strings --}%
                <g:if test="${f.fieldType == String}">
                    <g:textField name="${f.fieldName}" class="form-control"/>
                </g:if>


                %{-- Dates--}%
                <g:if test="${f.fieldType == Date}">
                    <bs:datePicker name="${f.fieldName}" class="form-control"/>
                </g:if>

                %{-- Select One--}%
                <g:if test="${f.fieldType == List}">
                    <g:select name="${f.fieldName}" from="${f.possibleValues}" class="form-control"/>
                </g:if>

            </div>

        </div>

    </g:each>
    <div class="form-group">
        <div class="col-md-9 col-md-offset-3">
            <g:submitButton name="getReport" value="Download" class="btn btn-default"/>
        </div>
    </div>
</g:form>
</body>

</html>
