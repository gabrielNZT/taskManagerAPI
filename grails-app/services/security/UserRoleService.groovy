package security

import grails.gorm.services.Service

@Service(UserRole)
interface UserRoleService {

    UserRole get(Serializable id)

    List<UserRole> list(Map args)

    Long count()

    UserRole delete(Serializable id)

    UserRole save(UserRole userRole)

}
