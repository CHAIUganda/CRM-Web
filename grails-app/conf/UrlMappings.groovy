class UrlMappings {

	static mappings = {

		/*
		 * Pages without controller
		 */
//		"/"				(view:"/index")
		"/about"		(view:"/siteinfo/about")
		"/blog"			(view:"/siteinfo/blog")
		"/systeminfo"	(view:"/siteinfo/systeminfo")
		"/contact"		(view:"/siteinfo/contact")
		"/terms"		(view:"/siteinfo/terms")
		"/imprint"		(view:"/siteinfo/imprint")
		"/nextSteps"	(view:"/home/nextSteps")

		/*
		 * Pages with controller
		 * WARN: No domain/controller should be named "api" or "mobile" or "web"!
		 */
        "/"(redirect: [controller: 'home'])
        "/$controller/$action?/$id?"{
            constraints {
                controller(matches:/^((?!(api|mobile|web)).*)$/)
            }
        }

		/*
		 * System Pages without controller
		 */
		"403"	(view:'/_errors/403')
		"404"	(view:'/_errors/404')
		"500"	(view:'/_errors/500')
		"503"	(view:'/_errors/503')

        "/$namespace/$controller/$action?/$id?"()

        // Products
        '/rest/product/list'(controller: 'product', namespace: 'rest', action: 'listProducts')
        '/rest/productGroup/list'(controller: 'product', namespace: 'rest', action: 'listProductGroups')

        //Pace
        '/rest/place/parishes/update'(controller: 'place',namespace : 'rest',action: 'updateParish')
        '/rest/place/villages/update'(controller: 'place',namespace : 'rest',action: 'updateVillage')
    }
}
