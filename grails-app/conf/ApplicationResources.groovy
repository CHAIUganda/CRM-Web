modules = {
    application {
        resource url:'js/application.js'
    }

    chosen {
        dependsOn 'jquery'
        resource url: 'js/lib/chosen/chosen.jquery.min.js'
        resource url: 'js/lib/chosen/chosen.css'
    }
}