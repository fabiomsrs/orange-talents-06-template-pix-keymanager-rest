package br.com.zup.fabiano.shared.converterError

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpStatus

class CadastroChavePixConverterError{

    companion object: ConverterError{
        override val map = mapOf<Status.Code, HttpStatus>(
            Pair(Status.INVALID_ARGUMENT.code, HttpStatus.BAD_REQUEST),
            Pair(Status.ALREADY_EXISTS.code, HttpStatus.UNPROCESSABLE_ENTITY)
        )

        override fun converter(e: StatusRuntimeException): HttpStatus {
            return map.getOrDefault(e.status.code, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}