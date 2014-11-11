
<%@ page import="com.omnitech.chai.model.DetailerTask" %>
<!DOCTYPE html>
<html>

<head>
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'detailerTask.label', default: 'DetailerTask')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-detailerTask" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.assignedTo.label" default="Assigned To" /></td>
				
				<td valign="top" class="value"><g:link controller="null" action="show" id="${detailerTaskInstance?.assignedTo?.id}">${detailerTaskInstance?.assignedTo?.encodeAsHTML()}</g:link></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.buyingPrice.label" default="Buying Price" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "buyingPrice")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.completedBy.label" default="Completed By" /></td>
				
				<td valign="top" class="value"><g:link controller="null" action="show" id="${detailerTaskInstance?.completedBy?.id}">${detailerTaskInstance?.completedBy?.encodeAsHTML()}</g:link></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.completionDate.label" default="Completion Date" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${detailerTaskInstance?.completionDate}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.customer.label" default="Customer" /></td>
				
				<td valign="top" class="value"><g:link controller="null" action="show" id="${detailerTaskInstance?.customer?.id}">${detailerTaskInstance?.customer?.encodeAsHTML()}</g:link></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${detailerTaskInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.description.label" default="Description" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "description")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.diarrheaEffectsOnBody.label" default="Diarrhea Effects On Body" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "diarrheaEffectsOnBody")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.diarrheaPatientsInFacility.label" default="Diarrhea Patients In Facility" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "diarrheaPatientsInFacility")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.doYouStockOrsZinc.label" default="Do You Stock Ors Zinc" /></td>
				
				<td valign="top" class="value"><g:formatBoolean boolean="${detailerTaskInstance?.doYouStockOrsZinc}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.dueDate.label" default="Due Date" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${detailerTaskInstance?.dueDate}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.heardAboutDiarrheaTreatmentInChildren.label" default="Heard About Diarrhea Treatment In Children" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "heardAboutDiarrheaTreatmentInChildren")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.howDidYouHear.label" default="How Did You Hear" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "howDidYouHear")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.howManyZincInStock.label" default="How Many Zinc In Stock" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "howManyZincInStock")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.howmanyOrsInStock.label" default="Howmany Ors In Stock" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "howmanyOrsInStock")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.ifNoWhy.label" default="If No Why" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "ifNoWhy")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.knowledgeAbtOrsAndUsage.label" default="Knowledge Abt Ors And Usage" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "knowledgeAbtOrsAndUsage")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.knowledgeAbtZincAndUsage.label" default="Knowledge Abt Zinc And Usage" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "knowledgeAbtZincAndUsage")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${detailerTaskInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.orsBrandSold.label" default="Ors Brand Sold" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "orsBrandSold")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.orsPrice.label" default="Ors Price" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "orsPrice")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.otherWaysHowYouHeard.label" default="Other Ways How You Heard" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "otherWaysHowYouHeard")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.pointOfsaleMaterial.label" default="Point Ofsale Material" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "pointOfsaleMaterial")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.recommendationLevel.label" default="Recommendation Level" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "recommendationLevel")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.recommendationNextStep.label" default="Recommendation Next Step" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "recommendationNextStep")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.status.label" default="Status" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "status")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.uuid.label" default="Uuid" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "uuid")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.whatYouKnowAbtDiarrhea.label" default="What You Know Abt Diarrhea" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "whatYouKnowAbtDiarrhea")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.whyNotUseAntibiotics.label" default="Why Not Use Antibiotics" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "whyNotUseAntibiotics")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.zincBrandsold.label" default="Zinc Brandsold" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "zincBrandsold")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="detailerTask.zincPrice.label" default="Zinc Price" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: detailerTaskInstance, field: "zincPrice")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
