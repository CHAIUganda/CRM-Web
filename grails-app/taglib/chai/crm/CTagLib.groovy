package chai.crm

import com.omnitech.chai.util.ReflectFunctions

/**
 * Created by kay on 1/31/2015.
 */
class CTagLib {

    static namespace = "c"

    /**
     * Render a table property
     *
     * @attr value Property name
     * @attr label label
     */
    def renderProperty = { attrs ->
        out << '<tr class="prop">'
        out << '<td valign="top" class="name">' << attrs.label << '</td>'
        out << '<td valign="top" class="value">' << attrs.value << '</td>'
        out << '</tr>'
    }

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
     * @attr clear List of params to remove
     */
    def link = { attrs, body ->
        attrs.params = (attrs.extraParams ?: [:]) + (attrs.params ?: [:])

        def reset = attrs.reset
        if (reset instanceof String) {
            attrs.params.remove(reset)
        } else if (reset instanceof List) {
            reset.each { attrs.params.remove(it) }
        }
        out << g.link(attrs, body)
    }



}
