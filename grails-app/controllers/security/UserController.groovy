package security

import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import taskmanagerapi.UserCard
import taskmanagerapi.UserCardService

import java.security.Principal

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.FORBIDDEN
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
    def index(Integer pageSize, Integer current, String sort, String field) {
        def c = User.createCriteria()
        Integer max = (pageSize * current)
        Integer offset = (pageSize * (current - 1))
        def results = c.list (max: max, offset: offset) {
            order(field, sort)
        }

        def model = [
                userList: results,
                userCount: User.count()
        ]

        respond model
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

            String authority = user.adm? 'ROLE_ADMIN' : 'ROLE_USER'
            new UserRole(user: User.findByUsername(user.username), role: Role.findByAuthority(authority)).save(flush: true)

        }
        respond user, [status: CREATED, view:"show"]
    }

    @Secured(['ROLE_ADMIN', 'ROLE_USER'])
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

        User currentUser = userService.get(springSecurityService.principal.id)
        Role roleCurrentUser = UserRole.findByUser(currentUser).role
        if(roleCurrentUser.getAuthority() == 'ROLE_USER' && currentUser.id != user.id){
            render status: FORBIDDEN
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
        if (id == null || userService.get(id) == null) {
            render status: NOT_FOUND
            return
        }
        UserRole.removeAll(User.get(id))
        userService.delete(id)
        render status: NO_CONTENT
    }

    @Secured(['ROLE_ADMIN', 'ROLE_USER'])
    @Transactional
    def current() {
        User user = userService.get(springSecurityService.principal.id)
        respond user
    }
}
