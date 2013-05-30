package openmessenger

import grails.test.*
import org.grails.taggable.Tag
import grails.converters.JSON

@TestFor(TagController)
@Mock(Tag)
class TagControllerTests {

    @Before
    void setUp() {
        mockDomain(Tag, [
            new Tag(name:'info'),
            new Tag(name:'etc')
        ])
    }


    void testList() {
        controller.list()
        def json = JSON.parse(controller.response.contentAsString)
        println json
        assert 2 == json.size()
        assert 'etc' == json[0].name
        assert 'info' == json[1].name
    }

    void testCreate() {
        controller.params.name = 'weather'
        controller.create()
        def json = JSON.parse(controller.response.contentAsString)
        assert 1 == json.status
        assert 'weather' == json.name
    }

    void testCreateFailWithBlankParams() {
        controller.create()
        def json = JSON.parse(controller.response.contentAsString)
        assert 0 == json.status
        assert 'name can not be null' == json.message
    }

    void testCreateFailWithDuplicatedName() {
        controller.params.name = 'info'
        controller.create()
        def json = JSON.parse(controller.response.contentAsString)
        assert 0 == json.status
        assert 'fail' == json.message
        assert 'info' == json.name
    }
}
