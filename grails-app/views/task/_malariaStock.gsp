<div class="panel panel-default">

    <div class="panel-heading">Malaria Stock</div>

    <div class="panel-body">

        <table class="table table-striped">
            <thead>
            <tr>
                <th>Category</th>
                <th>Brand</th>
                <th>Stock Level</th>
                <th>Buying Price</th>
                <th>Selling Price</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${taskInstance?.detailerMalariaStocks}" var="v">
                <tr>
                    <td>${v.category}</td>
                    <td>${v.brand}</td>
                    <td>${v.stockLevel}</td>
                    <td>${v.buyingPrice}</td>
                    <td>${v.sellingPrice}</td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

</div>