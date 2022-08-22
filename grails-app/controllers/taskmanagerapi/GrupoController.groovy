package taskmanagerapi

import grails.gorm.transactions.Transactional


import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

import javax.xml.bind.ValidationException

class GrupoController {

    GrupoService grupoService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index (){
        def model = [
                grupoList: Grupo.list()
        ]
        respond  model
    }

    def show (Long id){
        respond grupoService.get(id)
    }

    @Transactional
    def save (Grupo grupo){

        if(grupo == null){
            render status: NOT_FOUND
            return
        }
        if(grupo.hasErrors()){
            transactionStatus.setRollbackOnly()
            respond grupo.errors
            return
        }

        try{
            grupoService.save(grupo)
        }catch (ValidationException e) {
            respond grupo.errors
            return
        }

        respond grupo, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(Grupo grupo){
        if(grupo == null){
            render status: NOT_FOUND
            return
        }
        if(grupo.hasErrors()){
            transactionStatus.setRollbackOnly()
            respond grupo.errors
            return
        }
        try{
            grupoService.save(grupo)
        } catch(ValidationException e){
            respond grupo.errors
            return
        }
         respond grupo, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id){
        if(id == null || grupoService.delete(id) == null){
           render status: NOT_FOUND
            return
        }

        render status: NO_CONTENT
    }
}
