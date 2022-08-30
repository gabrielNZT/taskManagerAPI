package security

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class UserRoleController {

    UserRoleService userRoleService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond userRoleService.list(params), model:[userRoleCount: userRoleService.count()]
    }

    def show(Long id) {
        respond userRoleService.get(id)
    }

    @Transactional
    def save(UserRole userRole) {
        if (userRole == null) {
            render status: NOT_FOUND
            return
        }
        if (userRole.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userRole.errors
            return
        }

        try {
            userRoleService.save(userRole)
        } catch (ValidationException e) {
            respond userRole.errors
            return
        }

        respond userRole, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(UserRole userRole) {
        if (userRole == null) {
            render status: NOT_FOUND
            return
        }
        if (userRole.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userRole.errors
            return
        }

        try {
            userRoleService.save(userRole)
        } catch (ValidationException e) {
            respond userRole.errors
            return
        }

        respond userRole, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id) {
        if (id == null || userRoleService.delete(id) == null) {
            render status: NOT_FOUND
            return
        }

        render status: NO_CONTENT
    }
}
