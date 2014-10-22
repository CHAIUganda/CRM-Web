<%@ page import="com.omnitech.chai.model.CustomerSegment" %>



			<div class="${hasErrors(bean: customerSegmentInstance, field: 'callFrequency', 'error')} ">
				<label for="callFrequency" class="control-label"><g:message code="customerSegment.callFrequency.label" default="Call Frequency" /></label>
				<div>
					<g:field class='form-control' style="width: 50%;" type="number" name="callFrequency" value="${customerSegmentInstance.callFrequency}" />
					<span class="help-inline">${hasErrors(bean: customerSegmentInstance, field: 'callFrequency', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: customerSegmentInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="customerSegment.name.label" default="Name" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="name" value="${customerSegmentInstance?.name}" />
					<span class="help-inline">${hasErrors(bean: customerSegmentInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: customerSegmentInstance, field: 'segmentationScript', 'error')} ">
				<label for="segmentationScript" class="control-label"><g:message code="customerSegment.segmentationScript.label" default="Segmentation Script" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="segmentationScript" value="${customerSegmentInstance?.segmentationScript}" />
					<span class="help-inline">${hasErrors(bean: customerSegmentInstance, field: 'segmentationScript', 'error')}</span>
				</div>
			</div>

			<div class="${hasErrors(bean: customerSegmentInstance, field: 'teskGeneratorScript', 'error')} ">
				<label for="teskGeneratorScript" class="control-label"><g:message code="customerSegment.teskGeneratorScript.label" default="Tesk Generator Script" /></label>
				<div>
					<g:textField class='form-control' style="width: 50%;" name="teskGeneratorScript" value="${customerSegmentInstance?.teskGeneratorScript}" />
					<span class="help-inline">${hasErrors(bean: customerSegmentInstance, field: 'teskGeneratorScript', 'error')}</span>
				</div>
			</div>

