<!--
This is the standard dialog that initiates the delete action.
-->

<!-- Modal dialog -->
<div id="SegmentModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="DeleteModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				<h3 id="SegmentModalLabel">Run Automatic Customer Segmentation?</h3>
			</div>
			<div class="modal-body">
				<p>Are you Sure You Want To Run The Segmentation Process?</p>
			</div>
			<div class="modal-footer">
                <g:form controller="customer" action="autoSegment">
                    <button class="btn" data-dismiss="modal" aria-hidden="true"><g:message code="default.button.cancel.label" default="Cancel"/></button>
                    <span class="button"><g:submitButton name="autoSegment" class="btn btn-success " action="delete" value="Run Segmentation" /></span>
                </g:form>
			</div>
		</div>
	</div>
</div>