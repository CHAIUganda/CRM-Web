modules = {
    application {
        resource url:'js/application.js'
    }

    chosen {
        dependsOn 'jquery'
        resource url: 'js/lib/chosen/chosen.jquery.min.js'
        resource url: 'js/lib/chosen/chosen.css'
    }

    jqueryTreeTable {
        dependsOn 'jquery'
        resource url: 'js/lib/jquery.treetable.js'
        resource url: 'css/jqtreetable/jquery.treetable.css'
    }
}