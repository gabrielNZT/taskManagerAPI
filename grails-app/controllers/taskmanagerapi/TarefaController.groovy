package taskmanagerapi

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured

import javax.validation.ValidationException

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.BAD_REQUEST

@Secured(['ROLE_USER', 'ROLE_ADMIN'])
class TarefaController {

    static allowedMethods = [save: "POST", update: ["PUT", "POST"], patch: "PATCH", delete: "DELETE"]
    static responseFormats = ['json', 'xml']

    TarefaService tarefaService
    GrupoService grupoService

    def index(){
        def model= [
                tarefaList: Tarefa.list()
        ]
        respond model
    }

    def show(Long id){
        respond tarefaService.get(id)
    }

    @Transactional
    def save(Tarefa tarefa){
        if(tarefa == null){
            respond status: BAD_REQUEST
            return
        }

        if(tarefa.hasErrors()){
            transactionStatus.setRollbackOnly()
            respond tarefa.errors
            return
        }

        try{
            tarefaService.save(tarefa)
        }catch(ValidationException e){
            respond(tarefa.errors)
            return
        }

        respond tarefa, [status: CREATED, view:"show"]
    }

    @Transactional
    def moveCard(){

        Tarefa tarefa = new Tarefa()
        tarefa.properties = request

        if(tarefa == null){
            respond status: BAD_REQUEST
            return
        }

        if(tarefa.hasErrors()){
            transactionStatus.setRollbackOnly()
            respond tarefa.errors
            return
        }

        Tarefa updateCard = tarefa
        Tarefa card = Tarefa.get(params.id)

        try{
            //has change group
            if(card.grupo != updateCard.grupo){
                card.setGrupo(updateCard.grupo)
                for(Tarefa element : Tarefa.findAllByGrupo(card.grupo)){
                    if(element.position > card.position && element != card){
                        element.setPosition((element.position - 1))
                    }
                }

                for(Tarefa element : Tarefa.findAllByGrupo(updateCard.grupo)){
                    if(element.position >= updateCard.position && element != card){
                        element.setPosition((element.position + 1))
                    }
                }
            } else { // same group
                for(Tarefa element : Tarefa.findAllByGrupo(updateCard.grupo)){
                    if(element.position >= updateCard.position && element != card){
                        element.setPosition((element.position + 1))
                    }
                }
            }
            card.setPosition(updateCard.position)
        }catch(ValidationException e){
            respond(tarefa.errors)
            return
        }

        respond tarefa, [status: OK, view:"show"]
    }

    @Transactional
    def update(Tarefa tarefa){
        if(tarefa == null){
            respond status: BAD_REQUEST
            return
        }

        if(tarefa.hasErrors()){
            transactionStatus.setRollbackOnly()
            respond tarefa.errors
            return
        }

        try{
            tarefaService.save(tarefa)
        }catch(ValidationException e){
            respond tarefa.errors
            return
        }

        respond tarefa, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id){
        if(tarefaService.get(id) == null || id == null){
            respond status: NOT_FOUND
            return
        }
        for(Tarefa tarefa: Tarefa.findAllByGrupo(tarefaService.get(id).grupo)){
                if(tarefa.getPosition() > tarefaService.get(id).getPosition()){
                    tarefa.setPosition(tarefa.getPosition() - 1)
            }
        }

        tarefaService.delete(id)
        respond status: NO_CONTENT
    }
}
