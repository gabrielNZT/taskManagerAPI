package taskmanagerapi

class Tarefa {

    String title
    String description
    Integer index

    static belongsTo = [grupo: Grupo]

    static constraints = {
        title maxSize: 255, nullable: false
        description maxSize: 255, nullable: true
        index min: 0, nullable: false
    }
}
