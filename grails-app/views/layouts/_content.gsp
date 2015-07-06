<div class="container" style="max-width: 1170px; padding: 0; margin-bottom: 3px;">
    <!-- Secondary menu in one row (e.g., actions for current controller) -->
    <g:if test="${!layout_nosecondarymenu}">
        <g:render template="/_menu/submenubar"/>
    </g:if>
</div>

<div id="Content" class="container">
<!-- print system messages (infos, warnings, etc) - not validation errors -->
    <g:if test="${flash.message && !layout_noflashmessage}">
        <div class="alert alert-info">${flash.message}</div>
    </g:if>

    <g:if test="${flash.error && !layout_noflashmessage}">
        <div class="alert alert-danger"><strong>${flash.error}</strong></div>
    </g:if>

<!-- Show page's content -->
    <g:layoutBody />
    <g:pageProperty name="page.body" />
</div>
