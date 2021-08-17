package br.com.zup.fabiano.factory

import br.com.zup.edu.ChavePixServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory {
    @Singleton
    fun chavePixClientStub(@GrpcChannel("chave-pix") channel: ManagedChannel): ChavePixServiceGrpc.ChavePixServiceBlockingStub? {
        return ChavePixServiceGrpc.newBlockingStub(channel)
    }
}