package taskmanagerapi

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@GrailsCompileStatic
@EqualsAndHashCode(includes=['configAttribute', 'url'])
@ToString(includes=['configAttribute', 'url'], cache=true, includeNames=true, includePackage=false)
class Requestmap implements Serializable{

    private static final long serialVersionUID = 1
    String configAttribute
    String url

    static constraints = {
        configAttribute blank: false
    }

    static mapping = {
        cache true
    }

}