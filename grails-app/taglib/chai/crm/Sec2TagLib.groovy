package chai.crm

import grails.plugin.springsecurity.SecurityTagLib

/**
 * RoleTagLib
 * A taglib library provides a set of reusable tags to help rendering the views.
 */
class Sec2TagLib extends SecurityTagLib {
//    static defaultEncodeAs = 'html'
//    static encodeAsForTags = [tagName: 'raw']
    static namespace = "sec2"

    /**
     * General linking to controllers, actions etc. Examples:<br/>
     *
     * &lt;g:link action="myaction"&gt;link 1&lt;/gr:link&gt;<br/>
     * &lt;g:link controller="myctrl" action="myaction"&gt;link 2&lt;/gr:link&gt;<br/>
     *
     * @attr controller The name of the controller to use in the link, if not specified the current controller will be linked
     * @attr action The name of the action to use in the link, if not specified the default action will be linked
     * @attr uri relative URI
     * @attr url A map containing the action,controller,id etc.
     * @attr base Sets the prefix to be added to the link target address, typically an absolute server URL. This overrides the behaviour of the absolute property, if both are specified.
     * @attr absolute If set to "true" will prefix the link target address with the value of the grails.serverURL property from Config, or http://localhost:&lt;port&gt; if no value in Config and not running in production.
     * @attr id The id to use in the link
     * @attr fragment The link fragment (often called anchor tag) to use
     * @attr params A map containing URL query parameters
     * @attr mapping The named URL mapping to use to rewrite the link
     * @attr event Webflow _eventId parameter
     * @attr elementId DOM element id
     * @attr alwaysRender Set true if u want the content rendered regardless of security
     */
    def link = { attrs, body ->
        if (hasAccess(attrs.clone(), "link")) {
            def applicationTagLib = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
            applicationTagLib.link.call(attrs, body)
        } else {
            def alwaysRender = attrs.alwaysRender ?: params.secAlwayRenderLinks ?: 'false'
            if (Boolean.valueOf(alwaysRender))
                out << body()
        }
    }
}
