import com.omnitech.chai.migrations.DbMigrations1
import com.omnitech.chai.migrations.DbMigrations2
import com.omnitech.chai.migrations.IMigration
import com.omnitech.chai.model.DirectSale
import com.omnitech.chai.model.Order
import com.omnitech.chai.util.ChaiUtils
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.data.neo4j.annotation.NodeEntity

class BootStrap {

    def springSecurityService
    def txHelperService
    def graphDatabaseService
    def migrationService


    def init = { servletContext ->
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+3:00"))
        ChaiUtils.injectUtilityMethods()
        createUuidConstraints()

        //Migrations
        [new DbMigrations1(), new DbMigrations2()].each { IMigration m ->
            migrationService.runMigration(m.migrations())
        }
    }


    def destroy = {
        println("*****SHUTTING DOWN GRAPH DB****")
        graphDatabaseService.shutdown()
    }


    def createUuidConstraints() {
        txHelperService.doInTransaction {
            getPersistentEntities().each {
                def constrainQuery = "CREATE CONSTRAINT ON (bean:${getSimpleName(it.beanClassName)}) ASSERT bean.uuid IS UNIQUE"
                println("Creating Unique UUID Constraint for $it.beanClassName")
                neo.query(constrainQuery, [:])
            }

            //other constraints
            ["CREATE CONSTRAINT ON (bean:${Order.simpleName}) ASSERT bean.clientRefId IS UNIQUE",
             "CREATE CONSTRAINT ON (bean:${DirectSale.simpleName}) ASSERT bean.clientRefId IS UNIQUE"
            ].each {
                println("Executing: $it")
                neo.query(it, [:])
            }
        }


    }

    String getSimpleName(String className) { Class.forName(className).simpleName }

    Set<BeanDefinition> getPersistentEntities() {
        def provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(NodeEntity));
        provider.findCandidateComponents("com.omnitech.chai.model");
    }

}
