package br.com.zup.fabiano.shared.utils

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpStatus

class GrpcStatusErrorParaHttpStatusError {

    companion object{
        val map = mapOf<Status.Code, HttpStatus>(
            Pair(Status.INVALID_ARGUMENT.code, HttpStatus.BAD_REQUEST),
            Pair(Status.ALREADY_EXISTS.code, HttpStatus.UNPROCESSABLE_ENTITY)
        )

        fun converter(e: StatusRuntimeException): HttpStatus {
            return map.getOrDefault(e.status.code, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}