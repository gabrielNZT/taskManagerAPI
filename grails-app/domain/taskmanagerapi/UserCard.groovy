package taskmanagerapi

import security.User



class UserCard {
    User user
    Date date

    static belongsTo = [card: Tarefa]

    static constraints = {
        user nullable: false
        date nullable: false
    }
}
