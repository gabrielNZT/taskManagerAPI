package taskmanagerapi

import security.User

class Grupo {

    String header
    Integer position


    static hasMany = [cards: Tarefa]

    static mapping = {
        cards cascade: 'all-delete-orphan'
    }

    static constraints = {
        header maxSize: 255, nullable: false
        position nullable: false, unique: false
    }
}
