package taskmanagerapi

import grails.gorm.transactions.Transactional
import security.Role
import security.User
import security.UserRole

class BootStrap {

    def init = { servletContext ->
        addUsers()
    }

    @Transactional
    void addUsers(){
        Role admin = Role.findByAuthority("ROLE_ADMIN")
        if (admin == null){
            admin = new Role(authority: "ROLE_ADMIN").save(flush: true)
        }
        Role user = Role.findByAuthority("ROLE_USER")
        if( user == null ){
            user = new Role(authority: "ROLE_USER").save(flush: true)
        }

        User user_adm = User.findByUsername("administrador")
        if( user_adm == null){
            user_adm = new User(username: "admin", password: "123", email: 'admin@gmail.com', enabled: true,
                    accountExpired: false, accountLocked: false, passwordExpired: false).save(flush: true)
        }

        User user_user = User.findByUsername("user")
        if( user_user == null){
            user_user = new User(username: "user", password: "123", email: 'user@gmail.com', enabled: true,
                    accountExpired: false, accountLocked: false, passwordExpired: false).save(flush: true)
        }

        if ( UserRole.findByUserAndRole(user_adm, admin) == null){
            new UserRole(user: user_adm, role: admin).save(flush: true)
        }

        if( UserRole.findByUserAndRole(user_user, user) == null){
            new UserRole(user: user_user, role: user).save(flush: true)
        }

        Grupo Backlog = new Grupo(header: "BACKLOG", position: 0).save()

        Tarefa card = new Tarefa(header: "card 1", position: 0, description: "nothing here", user: user_adm).save()
    }
    def destroy = {
    }
}
