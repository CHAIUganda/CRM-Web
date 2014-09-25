<%=packageName ? "package ${packageName}\n\n" : ''%>


import com.omnitech.chai.util.ChaiUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.support.Neo4jTemplate

import static com.omnitech.chai.util.ChaiUtils.extractId
import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

/**
 * ${className}Controller
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class ${className}Controller {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def  m${className}Service

	def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = m${className}Service.list${className}s(params)
        respond page.content, model: [${propertyName}Count: page.totalElements]
    }

	def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def page = m${className}Service.list${className}s(params)
        respond page.content, model: [${propertyName}Count: page.totalElements]
    }

    def show() {
        def id = extractId(params)
        if (id == -1) {
            notFound(); return
        }
        respond m${className}Service.find${className}(id)
    }

    def create() {
        respond ChaiUtils.bind(new ${className}(), params)
    }

    def save(${className} ${propertyName}) {
        if (${propertyName} == null) {
            notFound()
            return
        }

        if (${propertyName}.hasErrors()) {
            respond ${propertyName}.errors, view:'create'
            return
        }

        m${className}Service.save${className} ${propertyName}

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: '${className}.label', default: '${className}'), ${propertyName}.id])
                redirect action: 'show', id: ${propertyName}.id
            }
            '*' { respond ${propertyName}, [status: CREATED] }
        }
    }

    def edit() {
        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }
        def ${propertyName} = m${className}Service.find${className}(id)
        respond ${propertyName}
    }

    @Transactional
    def update(${className} ${propertyName}) {
        if (${propertyName} == null) {
            notFound()
            return
        }

        if (${propertyName}.hasErrors()) {
            respond ${propertyName}.errors, view:'edit'
            return
        }

        m${className}Service.save${className} ${propertyName}

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: '${className}.label', default: '${className}'), ${propertyName}.id])
                redirect action: 'show', id: ${propertyName}.id
            }
            '*'{ respond ${propertyName}, [status: OK] }
        }
    }

    @Transactional
    def delete() {

        def id = extractId(params)

        if (id == -1) {
            notFound(); return
        }

        m${className}Service.delete${className} id

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: '${className}.label', default: '${className}'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: '${className}.label', default: '${className}'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
