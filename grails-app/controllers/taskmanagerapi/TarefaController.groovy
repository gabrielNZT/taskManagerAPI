package taskmanagerapi


import grails.rest.*
import grails.converters.*

class TarefaController extends RestfulController {
    static responseFormats = ['json', 'xml']
    TarefaController() {
        super(Tarefa)
    }
}
