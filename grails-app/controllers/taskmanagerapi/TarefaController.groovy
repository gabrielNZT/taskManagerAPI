package taskmanagerapi

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.mail.MailService
import security.User
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
    SpringSecurityService springSecurityService
    UserCardService userCardService
    MailService mailService

    def index(){
        def model = [
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
        } finally {
            def user = User.get(springSecurityService.principal.id)
            UserCard userCard = new UserCard(user: user, date: new Date(), card: tarefa)
            userCardService.save(userCard)
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
        def user = User.get(springSecurityService.principal.id)

        Tarefa newCard = tarefa
        Tarefa oldCard = Tarefa.get(params.id)
        UserCard userCard = new UserCard(user: user, date: new Date(), card: oldCard)
        userCardService.save(userCard)
        Tarefa cardUpdated = tarefaService.moveTarefa(oldCard, newCard, user);


        respond cardUpdated, [status: OK, view:"show"]
    }

    @Transactional
    def update(){
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
        Tarefa oldTarefa = Tarefa.get(params.id)
        try{

        }catch(ValidationException ignored){
            respond tarefa.errors
            return
        } finally {
            def user = User.get(springSecurityService.principal.id)

            if(oldTarefa.header != tarefa.header && tarefa.description != oldTarefa.description){
                tarefaService.sendMessage(user, tarefa, oldTarefa, 'UPDATE_HEADER_DESCRIPTION')
            }
            if(oldTarefa.header != tarefa.header ){
                tarefaService.sendMessage(user, tarefa, oldTarefa, 'UPDATE_HEADER')
            }
            if(oldTarefa.description != tarefa.description){
                tarefaService.sendMessage(user, tarefa, oldTarefa, 'UPDATE_DESCRIPTION')
            }
            oldTarefa.setHeader(tarefa.header)
            oldTarefa.setDescription(tarefa.description)
            oldTarefa.setVersion(oldTarefa.getVersion() + 1)
            UserCard userCard = new UserCard(user: user, date: new Date(), card: oldTarefa)
            userCardService.save(userCard)
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
