<div class="modal fade" id="line-item-form" tabindex="-1" role="myModalLabel" aria-hidden="true">

    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>

                <h3 class="modal-title">Select Item</h3>
            </div>

            <div class="first modal-body">

                <div class="form-horizontal">

                    %{-- Select Product --}%
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Product</label>
                        <div class="col-sm-8">
                            <g:select name="product" from="${['Quninin', 'Septring']}" class="form-control"/>
                        </div>
                    </div>

                    %{-- Quantity --}%
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Quantity</label>
                        <div class="col-sm-8">
                            <input type="text" class="form-control">
                        </div>
                    </div>

                     %{-- Unit Price --}%
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Recommended Price</label>
                        <div class="col-sm-8">
                            <p class="form-control-static">7005</p>
                        </div>
                    </div>

                    %{-- Total Price --}%
                    <div class="form-group">
                        <label class="col-sm-4 control-label">Total</label>
                        <div class="col-sm-8">
                            <p class="form-control-static">7005</p>
                        </div>
                    </div>

                </div>

            </div>
        </div>
    </div>

</div>