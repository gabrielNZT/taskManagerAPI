package taskmanagerapi

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional

import java.lang.invoke.DirectMethodHandle

@Service(Grupo)
interface GrupoService{

    Grupo get(Serializable id)

    List<Grupo>list (Map args)

    Long count()

    Grupo delete(Serializable id)

    Grupo save(Grupo grupo)
}