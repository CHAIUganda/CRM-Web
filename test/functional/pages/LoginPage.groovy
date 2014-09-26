package pages

import geb.Page

/**
 * Created by kay on 9/26/14.
 */
class LoginPage extends Page {

    static url = "login/auth"

    static at = {title ==~ '(?i).*login.*'}

    static content = {
        username { $('#username') }
        password { $('#password') }
        submit { $('#submit') }
    }

}

class IndexPage extends Page {
    static url = 'home/index'

    static at = {title ==~ '(?i).*welcome.*'}

    static content = {

    }
}
