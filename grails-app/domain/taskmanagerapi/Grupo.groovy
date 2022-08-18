package taskmanagerapi

class Grupo {

    String title
    Integer index

    static hasMany = [cards: Tarefa]

    static mapping = {
        cards cascade: 'all-delete-orphan'
    }

    static constraints = {
        title maxSize: 255, nullable: false
        index nullable: false, unique: true
    }
}
