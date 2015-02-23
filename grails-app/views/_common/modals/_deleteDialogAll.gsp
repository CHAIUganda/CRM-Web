<!-- 
This is the standard dialog that initiates the delete action.
-->

<!-- Modal dialog -->
<div id="DeleteModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="DeleteModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				<h3 id="DeleteModalLabel"><g:message code="default.button.delete.confirm.title" default="Delete Items"/></h3>
			</div>
			<div class="modal-body">
				<p>Do you really want to delete these items?</p>
			</div>
			<div class="modal-footer">
				<g:form>
					<button class="btn" data-dismiss="modal" aria-hidden="true"><g:message code="default.button.cancel.label" default="Cancel"/></button>
					<span class="button">
						<g:submitButton name="delete" value="Delete Tasks" class="btn btn-danger"/>
					</span>
				</g:form>
			</div>
		</div>
	</div>
</div>