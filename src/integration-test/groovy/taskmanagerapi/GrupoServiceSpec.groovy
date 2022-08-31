package taskmanagerapi

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.grails.datastore.mapping.core.Datastore
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@Integration
@Rollback
class GrupoServiceSpec extends Specification {

    GrupoService grupoService
    @Autowired Datastore datastore

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Grupo(...).save(flush: true, failOnError: true)
        //new Grupo(...).save(flush: true, failOnError: true)
        //Grupo grupo = new Grupo(...).save(flush: true, failOnError: true)
        //new Grupo(...).save(flush: true, failOnError: true)
        //new Grupo(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //grupo.id
    }

    void cleanup() {
        assert false, "TODO: Provide a cleanup implementation if using MongoDB"
    }

    void "test get"() {
        setupData()

        expect:
        grupoService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Grupo> grupoList = grupoService.list(max: 2, offset: 2)

        then:
        grupoList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        grupoService.count() == 5
    }

    void "test delete"() {
        Long grupoId = setupData()

        expect:
        grupoService.count() == 5

        when:
        grupoService.delete(grupoId)
        datastore.currentSession.flush()

        then:
        grupoService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Grupo grupo = new Grupo()
        grupoService.save(grupo)

        then:
        grupo.id != null
    }
}
