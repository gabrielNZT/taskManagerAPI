package taskmanagerapi

import grails.gorm.services.Service

@Service(UserCard)
interface UserCardService {

    UserCard get(Serializable id)

    List<UserCard> list(Map args)

    Long count()

    UserCard delete(Serializable id)

    UserCard save(UserCard userCard)

}
