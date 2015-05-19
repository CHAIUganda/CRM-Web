<c:renderProperty label="How many malaria patients do you see in your outlet/facility in a week?*"
                  value="${fieldValue(bean: taskInstance, field: "malariaPatientsInFacility")}"/>

<c:renderProperty label="How many of these patients are children under 5?*"
                  value="${fieldValue(bean: taskInstance, field: "numberOfChildren")}"/>

<c:renderProperty label="When a patient with suspected malaria comes to your shop, do you usually prescribe the treatment or does the patient request something specifically?"
                  value="${fieldValue(bean: taskInstance, field: "doYouPrescribeTreatment")}"/>

<c:renderProperty label="Have you heard about the treatment of malaria using antimalarial bearing a green leaf - ACTs?*"
                  value="${fieldValue(bean: taskInstance, field: "heardAboutGreenLeaf")}"/>

<c:renderProperty label="If yes, how did you hear about it?"
                  value="${fieldValue(bean: taskInstance, field: "howDidYouHear")}"/>

<c:renderProperty label="How do you suspect a patient has malaria?*"
                  value="${fieldValue(bean: taskInstance, field: "howYouSuspectMalaria")}"/>

<c:renderProperty label="Do you know the MOH guideline for managing a patient with fever?*" value="${fieldValue(bean: taskInstance, field: "doYouKnowMOHGuidelines")}"/>

<c:renderProperty label="If Yes, probe?*" value="${fieldValue(bean: taskInstance, field: "mohGuidelines")}"/>

<c:renderProperty label="Do you know about antimalarials bearing a Greenleaf?*"
                  value="${fieldValue(bean: taskInstance, field: "heardAboutGreenLeaf")}"/>

<c:renderProperty label="If yes, what does it represent?*"
                  value="${fieldValue(bean: taskInstance, field: "knowAboutGreenLeafAntimalarials")}"/>

<c:renderProperty label="Do you prescribe antimalarials without Greenleaf?*"
                  value="${fieldValue(bean: taskInstance, field: "doYouPrescribeWithoutGreenLeaf")}"/>

<c:renderProperty label="If sometimes and always, ask why?*"
                  value="${fieldValue(bean: taskInstance, field: "whyPrescribeWithoutGreenLeaf")}"/>

<c:renderProperty label="Do you know what severe malaria is, how to identify it, and deal with severe malaria patients?*"
                  value="${fieldValue(bean: taskInstance, field: "knowWhatSevereMalariaIs")}"/>

<c:renderProperty label="If yes, what are signs of severe malaria?*"
                  value="${fieldValue(bean: taskInstance, field: "signsOfSevereMalaria")}"/>

<c:renderProperty label="If yes, how do you manage patients with severe malaria?*"
                  value="${fieldValue(bean: taskInstance, field: "howToManagePatientsWithSevereMalaria")}"/>

<c:renderProperty label="Do you stock antimalarials?"
                  value="${fieldValue(bean: taskInstance, field: "doYouStockAntimalarials")}"/>

<c:renderProperty label="Do you stock RDTs?"
                  value="${fieldValue(bean: taskInstance, field: "doYouStockRDTs")}"/>

<c:renderProperty label="Point of sales materials dropped (Dropdown (checkboxes): RRP poster, dangler)"
                  value="${fieldValue(bean: taskInstance, field: "pointOfsaleMaterial")}"/>
<c:renderProperty label="Recommended next steps"
                  value="${fieldValue(bean: taskInstance, field: "recommendationNextStep")}"/>
