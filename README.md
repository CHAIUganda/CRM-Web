
### Rest Interface ###
#### Place (Regions/Districts/SubCountys) etc... ####

All the url below return a map of [id,name,uuid]

- http://23.239.27.196:8080/web-crm/rest/place/regions
- http://23.239.27.196:8080/web-crm/rest/place/districts
- http://23.239.27.196:8080/web-crm/rest/place/subCounties
- http://23.239.27.196:8080/web-crm/rest/place/parishes
- http://23.239.27.196:8080/web-crm/rest/place/villages

#### To Places 
- http://23.239.27.196:8080/web-crm/rest/place/subCounties/update
- http://23.239.27.196:8080/web-crm/rest/place/parishes/update
- http://23.239.27.196:8080/web-crm/rest/place/villages/update

### User Info
- http://23.239.27.196:8080/web-crm/rest/info

#### Customers

- http://23.239.27.196:8080/web-crm/rest/customer/list


#### Products
- http://23.239.27.196:8080/web-crm/rest/product/list
- http://23.239.27.196:8080/web-crm/rest/productGroup/list


#### Tasks
This URL Below will also come with orders
- http://23.239.27.196:8080/web-crm/rest/task/list

#### Sales
- http://23.239.27.196:8080/web-crm/rest/sale/directSale
- http://23.239.27.196:8080/web-crm/rest/sale/orderSale
- http://23.239.27.196:8080/web-crm/rest/sale/placeOrder

#### DetailerTask
- http://23.239.27.196:8080/web-crm/rest/task/update


PAY ATTENTION:
target/work/plugins/spring-security/src/groovy/grails/plugin/springsecurity/ReflectionUtils.groovy line 105:
	def Requestmap = Class.forName("com.omnitech.chai.model.RequestMap")





 




