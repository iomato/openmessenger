package openmessenger

import org.grails.taggable.Tag
import grails.converters.JSON

class TagController {

    def index = {
        redirect(action: "list")
    }

    def list = {
        def tags = Tag.list(sort: "name").collect{[name:it.name]}
        render tags as JSON
    }

    def create = {
        def name = params.name
        def ret = [:]
        def tag = new Tag(name: name)
        try {
            tag.save(failOnError:true, flush:true)
            ret.status = 1
            ret.name = name
            ret.message = 'success'
        } catch(e) {
            log.error e
            ret.status = 0
            ret.name = name
            ret.message = 'fail'
            if(!name) {
                ret.message = 'name can not be null'
            }
        } finally {
            render ret as JSON
        }
    }
}
