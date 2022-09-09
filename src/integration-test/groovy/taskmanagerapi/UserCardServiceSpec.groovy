package taskmanagerapi

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.grails.datastore.mapping.core.Datastore
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@Integration
@Rollback
class UserCardServiceSpec extends Specification {

    UserCardService userCardService
    @Autowired Datastore datastore

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new UserCard(...).save(flush: true, failOnError: true)
        //new UserCard(...).save(flush: true, failOnError: true)
        //UserCard userCard = new UserCard(...).save(flush: true, failOnError: true)
        //new UserCard(...).save(flush: true, failOnError: true)
        //new UserCard(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //userCard.id
    }

    void cleanup() {
        assert false, "TODO: Provide a cleanup implementation if using MongoDB"
    }

    void "test get"() {
        setupData()

        expect:
        userCardService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<UserCard> userCardList = userCardService.list(max: 2, offset: 2)

        then:
        userCardList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        userCardService.count() == 5
    }

    void "test delete"() {
        Long userCardId = setupData()

        expect:
        userCardService.count() == 5

        when:
        userCardService.delete(userCardId)
        datastore.currentSession.flush()

        then:
        userCardService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        UserCard userCard = new UserCard()
        userCardService.save(userCard)

        then:
        userCard.id != null
    }
}
