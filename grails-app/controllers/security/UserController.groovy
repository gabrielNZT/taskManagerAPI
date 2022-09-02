package security

import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException

import java.security.Principal

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.CONFLICT

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class UserController {

    def springSecurityService
    UserService userService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @Secured(['ROLE_ADMIN'])
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond userService.list(params), model:[userCount: userService.count()]
    }

    @Secured(['ROLE_ADMIN', 'ROLE_USER'])
    def currentUser() {
        def current  = springSecurityService.principal

    }

    @Secured(['ROLE_ADMIN', 'ROLE_USER'])
    def show(Long id) {
        respond userService.get(id)
    }

    @Secured(['ROLE_ADMIN', 'ROLE_USER'])
    def getId (String username) {
       respond User.findByUsername(username)
    }

    @Transactional
    def save(User user) {
        if (user == null) {
            render status: NOT_FOUND
            return
        }
        if (user.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond user.errors
            return
        }

        try {
            userService.save(user)
        } catch (ValidationException e) {
            respond user.errors
            return
        }

        respond user, [status: CREATED, view:"show"]
    }

    @Secured(['ROLE_ADMIN'])
    @Transactional
    def update(User user) {
        if (user == null) {
            render status: NOT_FOUND
            return
        }
        if (user.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond user.errors
            return
        }

        try {
            userService.save(user)
        } catch (ValidationException e) {
            respond user.errors
            return
        }

        respond user, [status: OK, view:"show"]
    }

    @Secured(['ROLE_ADMIN'])
    @Transactional
    def delete(Long id) {
        if (id == null || userService.delete(id) == null) {
            render status: NOT_FOUND
            return
        }

        render status: NO_CONTENT
    }

    @Secured(['ROLE_ADMIN', 'ROLE_USER'])
    @Transactional
    def current() {
        User user = userService.get(springSecurityService.principal.id)
        respond user
    }
}
