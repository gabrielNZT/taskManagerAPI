package taskmanagerapi

import grails.gorm.transactions.Transactional
import grails.plugins.mail.MailService
import security.User

@Transactional
class TarefaService {
    MailService mailService

    Long count() {
        return Tarefa.count
    }

    Tarefa get(Long id) {
        return Tarefa.findById(id)
    }

    def delete(Long id) {
        Tarefa.findById(id).delete()
    }

    List<Tarefa> list() {
        return Tarefa.list()
    }

    def save(Tarefa tarefa) {
        tarefa.save()
    }

    Tarefa moveTarefa(Tarefa oldCard, Tarefa newCard, User user) {
        //has change group
        if (oldCard.grupo != newCard.grupo) {
            for (Tarefa element : Tarefa.findAllByGrupo(oldCard.grupo)) {
                if (element.position > oldCard.position && element != oldCard) {
                    element.setPosition((element.position - 1))
                }
            }

            for (Tarefa element : Tarefa.findAllByGrupo(newCard.grupo)) {
                if (element.position >= newCard.position && element != oldCard) {
                    element.setPosition((element.position + 1))
                }
            }
            sendMessage(user, newCard, oldCard, 'MOVE_CHANGE_GROUP')
        } else { // same group
            for (Tarefa element : Tarefa.findAllByGrupo(newCard.grupo)) {
                // card is going up?
                //going down
                if (newCard.position > oldCard.position) {
                    if (element.position > oldCard.position && element.position <= newCard.position && element.id != oldCard.id) {
                        element.setPosition((element.position - 1))
                    }
                }
                //going up
                else if (newCard.position < oldCard.position) {
                    if (element.position < oldCard.position && element.position >= newCard.position && element.id != oldCard.id) {
                        element.setPosition((element.position + 1))
                    }
                }
            }
        }
        oldCard.setVersion(oldCard.version + 1)
        oldCard.setGrupo(newCard.grupo)
        oldCard.setPosition(newCard.position)
        return oldCard
    }

    def sendMessage(User currentUser, Tarefa newCard, Tarefa oldCard, String action){
        ArrayList<String> receiveEmailTo = new ArrayList<>()

        UserCard.findAllByCard(Tarefa.get(oldCard.id)).forEach(map -> {
            if(currentUser != map.user && !receiveEmailTo.contains(map.user.getEmail()) && map.user.receiveEmail){
                receiveEmailTo.add(map.user.getEmail())
            }
        })

        String message
        switch (action){
            case 'MOVE_CHANGE_GROUP':
                message = "$currentUser.username Moveu a tarefa $oldCard.header do " +
                        "grupo $oldCard.grupo.header para " +
                        "$newCard.grupo.header\nSe não quiser receber essas " +
                        "atualizações desative a opção de notificações no menu"
                break
            case 'MOVE_SAME_GROUP':
                message = "$currentUser.username moveu a tarefa $oldCard.header da posição " +
                        "$oldCard.position para $newCard.position\nSe não quiser receber essas " +
                        "atualizações desative a opção de notificações no menu"
                break
            case 'UPDATE_HEADER':
                message = "$currentUser.username atualizou o nome da tarefa $oldCard.header " +
                        "para $newCard.header\nSe não quiser receber essas " +
                        "atualizações desative a opção de notificações no menu"
                break
            case 'UPDATE_DESCRIPTION':
                message = "$currentUser.username atualizou a descrição da tarefa $oldCard.description " +
                        "para $newCard.description\nSe não quiser receber essas " +
                        "atualizações desative a opção de notificações no menu"
                break
            case 'UPDATE_HEADER_DESCRIPTION':
                message = "$currentUser.username atualizou o nome e descrição da tarefa $oldCard.header " +
                        "para $newCard.header e $newCard.description\nSe não quiser receber essas " +
                        "atualizações desative a opção de notificações no menu"
                break
            default:
                return {  message = "Ocorreu uma mudança" }
        }

        if(!receiveEmailTo.isEmpty()){
            mailService.sendMail {
                to receiveEmailTo
                subject "Dashboard"
                text message
            }
        }
    }
}
