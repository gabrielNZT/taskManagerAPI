package taskmanagerapi

import security.User

class Tarefa {

    String header
    String description
    Integer position

    static belongsTo = [grupo:Grupo]

    static hasMany = [userCard : UserCard]

    static mapping = {
        userCard cascade: 'all-delete-orphan'
    }

    static constraints = {
        header maxSize: 255, nullable: false
        description maxSize: 255, nullable: true
        position min: 0, nullable: false
    }
}
