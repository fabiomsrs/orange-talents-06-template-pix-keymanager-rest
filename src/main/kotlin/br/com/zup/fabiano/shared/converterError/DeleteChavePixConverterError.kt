package br.com.zup.fabiano.shared.converterError

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpStatus

class DeleteChavePixConverterError {
    companion object: ConverterError{
        override val map = mapOf<Status.Code, HttpStatus>(
            Pair(Status.INVALID_ARGUMENT.code, HttpStatus.BAD_REQUEST),
            Pair(Status.NOT_FOUND.code, HttpStatus.NOT_FOUND),
            Pair(Status.PERMISSION_DENIED.code, HttpStatus.FORBIDDEN)
        )

        override fun converter(e: StatusRuntimeException): HttpStatus {
            return map.getOrDefault(e.status.code, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}