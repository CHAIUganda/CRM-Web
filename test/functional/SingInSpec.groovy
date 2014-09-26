import geb.spock.GebSpec
import pages.IndexPage
import pages.LoginPage

/**
 * Created by kay on 9/26/14.
 */
class SingInSpec extends GebSpec {


    def 'test that you can sign in to the dashboard'() {

        given:
        via IndexPage

        expect:
        at LoginPage

        when:
        username = 'root'
        password = 'pass'
        submit.click()

        then:
        at IndexPage
    }

}
