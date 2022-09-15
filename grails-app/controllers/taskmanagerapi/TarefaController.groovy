package taskmanagerapi

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.mail.MailService
import org.springframework.beans.factory.annotation.Autowired
import security.User

import javax.validation.ValidationException
import java.util.stream.Collector
import java.util.stream.Stream

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

        Tarefa updateCard = tarefa
        Tarefa card = Tarefa.get(params.id)
        sendMessage(user, card)
        UserCard userCard = new UserCard(user: user, date: new Date(), card: card)
        userCardService.save(userCard)

            //has change group
            if(card.grupo != updateCard.grupo){
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
                    // card is going up?
                    //going down
                    if( updateCard.position > card.position ){
                        if(element.position > card.position && element.position <= updateCard.position && element.id != card.id){
                            element.setPosition((element.position - 1))
                        }
                    }
                        //going up
                    else if( updateCard.position < card.position ){
                        if( element.position <card.position && element.position >= updateCard.position && element.id != card.id){
                            element.setPosition((element.position + 1))
                        }
                    }
                }
            }
            card.setVersion(card.version + 1)
            card.setGrupo(updateCard.grupo)
            card.setPosition(updateCard.position)
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
        }catch(ValidationException ignored){
            respond tarefa.errors
            return
        } finally {
            def user = User.get(springSecurityService.principal.id)
            sendMessage(user, tarefa)
            UserCard userCard = new UserCard(user: user, date: new Date(), card: tarefa)
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

    def sendMessage(User currentUser, Tarefa card){
        ArrayList<String> receiveEmailTo = new ArrayList<>()

        UserCard.findAllByCard(Tarefa.get(card.id)).forEach(map -> {
            if(currentUser != map.user && !receiveEmailTo.contains(map.user.getEmail()) && map.user.receiveEmail){
                receiveEmailTo.add(map.user.getEmail())
            }
        })

        mailService.sendMail {
            to receiveEmailTo
            subject "Dashboard"
            text "Ocorreu uma atualização"
        }
    }
}
