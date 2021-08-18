package br.com.zup.fabiano.controller

import br.com.zup.edu.*
import br.com.zup.fabiano.dto.ChavePixDeleteRequest
import br.com.zup.fabiano.dto.ChavePixRegisterRequest
import br.com.zup.fabiano.shared.converterError.CadastroChavePixConverterError
import br.com.zup.fabiano.shared.converterError.DeleteChavePixConverterError
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Post
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.http.uri.UriBuilder
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.validation.Valid

@Validated
@Controller
class ChavePixController(@Inject val grpcClient: ChavePixServiceGrpc.ChavePixServiceBlockingStub) {

    @Post("/api/chave-pix")
    fun registrarChavePix(@Body @Valid request: ChavePixRegisterRequest): MutableHttpResponse<String>? {
        val grpcRequest = RegistrarChavePixGrpcRequest
            .newBuilder()
            .setChave(request!!.chave)
            .setIdCliente(request!!.idCliente)
            .setTipoChave(request!!.tipoChave)
            .setTipoConta(request!!.tipoConta)
            .build()

        try{
            val response = grpcClient.registrarChavePix(grpcRequest)
            val uri= UriBuilder.of("/api/chave-pix/{id}")
                .expand(mutableMapOf(Pair("id", response.idChavePix)))
            return HttpResponse.created<String?>(uri).body(response.idChavePix)
        }catch (e: StatusRuntimeException){
            throw HttpStatusException(CadastroChavePixConverterError.converter(e), e.status.description)
        }
    }

    @Delete("/api/chave-pix")
    fun deletarChavePix(@Body @Valid request: ChavePixDeleteRequest): MutableHttpResponse<String>? {
        val grpcRequest = RemoverChavePixGrpcRequest
            .newBuilder()
            .setIdChavePix(request.idChavePix)
            .setIdCliente(request.idClient)
            .build()

        try{
            val response = grpcClient.removerChavePix(grpcRequest)
            return HttpResponse.ok()
        }catch (e: StatusRuntimeException){
            throw HttpStatusException(DeleteChavePixConverterError.converter(e), e.status.description)
        }
    }
}