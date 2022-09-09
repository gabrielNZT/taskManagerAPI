package taskmanagerapi

import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
@Secured(['ROLE_USER', 'ROLE_ADMIN'])
class UserCardController {

    UserCardService userCardService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond userCardService.list(params), model:[userCardCount: userCardService.count()]
    }

    def show(Long id) {
        respond userCardService.get(id)
    }

    @Transactional
    def save(UserCard userCard) {
        if (userCard == null) {
            render status: NOT_FOUND
            return
        }
        if (userCard.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userCard.errors
            return
        }

        try {
            userCardService.save(userCard)
        } catch (ValidationException e) {
            respond userCard.errors
            return
        }

        respond userCard, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(UserCard userCard) {
        if (userCard == null) {
            render status: NOT_FOUND
            return
        }
        if (userCard.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userCard.errors
            return
        }

        try {
            userCardService.save(userCard)
        } catch (ValidationException e) {
            respond userCard.errors
            return
        }

        respond userCard, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id) {
        if (id == null || userCardService.delete(id) == null) {
            render status: NOT_FOUND
            return
        }

        render status: NO_CONTENT
    }
}
