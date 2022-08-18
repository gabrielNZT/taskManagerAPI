package taskmanagerapi


import grails.rest.*
import grails.converters.*

class GrupoController extends RestfulController {
    static responseFormats = ['json', 'xml']
    GrupoController() {
        super(Grupo)
    }
}
