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
        User user = userService.get(springSecurityService.principal.id)
        respond user, [status: OK]
    }

    @Secured(['ROLE_ADMIN', 'ROLE_USER'])
    def show(Long id) {
        respond userService.get(id)
    }

    @Transactional
    def save(User user) {
        if(User.findByUsername(user.username) != null){
            render status: CONFLICT
            return
        }

        else if(User.findByUsername(user.username) == null){
            User newUser = new User(
                    username: user.username,
                    email: user.email,
                    password: user.password,
                    adm: user.adm,
                    enabled: user.enabled,
                    accountExpired: user.accountExpired,
                    accountLocked: user.accountLocked,
                    passwordExpired: user.passwordExpired
            )

            try {
                userService.save(user)
            } catch (ValidationException e){
                respond user.errors
                return
            }

            if(user.adm){
                new UserRole(user: User.findByUsername(user.username).id, role: Role.findByAuthority('ROLE_USER').id).save(flush: true)
            } else {
                new UserRole(user: User.findByUsername(user.username).id, role: Role.findByAuthority('ROLE_ADMIN').id).save(flush: true)
            }
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
