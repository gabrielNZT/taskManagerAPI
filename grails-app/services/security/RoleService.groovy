package security

import grails.gorm.services.Service

@Service(Role)
interface RoleService {

    Role get(Serializable id)

    List<Role> list(Map args)

    Long count()

    Role delete(Serializable id)

    Role save(Role role)

}
