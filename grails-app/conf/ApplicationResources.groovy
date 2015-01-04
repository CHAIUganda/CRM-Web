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

    jqueryUI {
        dependsOn 'jquery'
        resource url: 'js/lib/jquery-ui-1.11.2.custom/jquery-ui.min.js'
        resource url: 'js/lib/jquery-ui-1.11.2.custom/jquery-ui.min.css'
    }

    'angular-ui' {
        dependsOn('angular')
        resource url: 'js/lib/ui-bootstrap/ui-bootstrap-custom-tpls-0.12.0.min.js'
    }

    reportWiz{
        dependsOn('angular','angular-resource','angular-animate')
        resource url: 'js/lib/reportWiz/reportWiz.js'
        resource url: 'js/lib/jquery.fileDownload.js'
        resource url: 'js/lib/reportWiz/styles.css'
    }
}