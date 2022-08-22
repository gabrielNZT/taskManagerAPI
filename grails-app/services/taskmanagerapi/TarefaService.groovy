package taskmanagerapi

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional

@Service(Tarefa)
interface TarefaService{

    Tarefa get(Serializable id)

    List<Tarefa>list (Map args)

    Long count()

    Tarefa delete(Serializable id)

    Tarefa save(Tarefa tarefa)

}
