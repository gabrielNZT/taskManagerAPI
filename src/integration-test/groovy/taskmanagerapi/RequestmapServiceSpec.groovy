package taskmanagerapi

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.grails.datastore.mapping.core.Datastore
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@Integration
@Rollback
class RequestmapServiceSpec extends Specification {

    RequestmapService requestmapService
    @Autowired Datastore datastore

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Requestmap(...).save(flush: true, failOnError: true)
        //new Requestmap(...).save(flush: true, failOnError: true)
        //Requestmap requestmap = new Requestmap(...).save(flush: true, failOnError: true)
        //new Requestmap(...).save(flush: true, failOnError: true)
        //new Requestmap(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //requestmap.id
    }

    void cleanup() {
        assert false, "TODO: Provide a cleanup implementation if using MongoDB"
    }

    void "test get"() {
        setupData()

        expect:
        requestmapService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Requestmap> requestmapList = requestmapService.list(max: 2, offset: 2)

        then:
        requestmapList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        requestmapService.count() == 5
    }

    void "test delete"() {
        Long requestmapId = setupData()

        expect:
        requestmapService.count() == 5

        when:
        requestmapService.delete(requestmapId)
        datastore.currentSession.flush()

        then:
        requestmapService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Requestmap requestmap = new Requestmap()
        requestmapService.save(requestmap)

        then:
        requestmap.id != null
    }
}
