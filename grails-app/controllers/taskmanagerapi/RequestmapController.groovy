package taskmanagerapi

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional

@ReadOnly
class RequestmapController {

    RequestmapService requestmapService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond requestmapService.list(params), model:[requestmapCount: requestmapService.count()]
    }

    def show(Long id) {
        respond requestmapService.get(id)
    }

    @Transactional
    def save(Requestmap requestmap) {
        if (requestmap == null) {
            render status: NOT_FOUND
            return
        }
        if (requestmap.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond requestmap.errors
            return
        }

        try {
            requestmapService.save(requestmap)
        } catch (ValidationException e) {
            respond requestmap.errors
            return
        }

        respond requestmap, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(Requestmap requestmap) {
        if (requestmap == null) {
            render status: NOT_FOUND
            return
        }
        if (requestmap.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond requestmap.errors
            return
        }

        try {
            requestmapService.save(requestmap)
        } catch (ValidationException e) {
            respond requestmap.errors
            return
        }

        respond requestmap, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id) {
        if (id == null || requestmapService.delete(id) == null) {
            render status: NOT_FOUND
            return
        }

        render status: NO_CONTENT
    }
}