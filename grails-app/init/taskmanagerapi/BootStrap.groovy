package taskmanagerapi

import grails.gorm.transactions.Transactional
import security.Role
import security.User
import security.UserRole

class BootStrap {

    def listUrl = [
            [ url: '/api',  configAttribute: 'permitAll' ],
            [ url: '/user/show', configAttribute: 'ROLE_USER' ],
            [ url: '/user/**',  configAttribute: 'permitAll' ],
            [ url: '/user/save', configAttribute: 'permitAll'],
            [ url: '/user/getUsername', configAttribute: 'ROLE_ADMIN, ROLE_USER' ],
            [ url: '/user/update/**',  configAttribute: 'ROLE_USER' ],
            [ url: '/user/delete',  configAttribute: 'ROLE_USER' ],
            [ url: '/oauth/access_token',    configAttribute: 'permitAll' ],
            [ url: '/login/**', configAttribute: 'permitAll' ],
            [ url: '/j_spring_security_switch_user',  configAttribute: 'ROLE_SWITCH_USER,isFullyAuthenticated()' ]
    ]

    def init = { servletContext ->
        addUrls()
        addUsers()
    }

    @Transactional
    void addUrls(){
        for( item in listUrl){
            if(Requestmap.findByUrl(item.url) == null){
                new Requestmap(url: item.url, configAttribute: item.configAttribute).save()
            }
        }
    }

    @Transactional
    void addUsers(){
        Role admin = Role.findByAuthority("ADMIN")
        if (admin == null){
            admin = new Role(authority: "ADMIN").save(flush: true)
        }
        Role user = Role.findByAuthority("USER")
        if( user == null ){
            user = new Role(authority: "USER").save(flush: true)
        }

        User user_adm = User.findByUsername("administrador")
        if( user_adm == null){
            user_adm = new User(username: "administrador", password: "123", enabled: true,
                    accountExpired: false, accountLocked: false, passwordExpired: false).save(flush: true)
        }

        User user_user = User.findByUsername("user")
        if( user_user == null){
            user_user = new User(username: "user", password: "123", enabled: true,
                    accountExpired: false, accountLocked: false, passwordExpired: false).save(flush: true)
        }

        if ( UserRole.findByUserAndRole(user_adm, admin) == null){
            new UserRole(user: user_adm, role: admin).save(flush: true)
        }

        if( UserRole.findByUserAndRole(user_user, user) == null){
            new UserRole(user: user_user, role: user).save(flush: true)
        }
    }
    def destroy = {
    }
}
