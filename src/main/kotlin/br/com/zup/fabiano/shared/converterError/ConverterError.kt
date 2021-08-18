package br.com.zup.fabiano.shared.converterError

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpStatus

interface ConverterError {
    val map: Map<Status.Code, HttpStatus>

    fun converter(e: StatusRuntimeException): HttpStatus? {
        return null
    }
}