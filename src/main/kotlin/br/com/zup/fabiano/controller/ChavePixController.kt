package br.com.zup.fabiano.controller

import br.com.zup.edu.*
import br.com.zup.fabiano.dto.*
import br.com.zup.fabiano.shared.converterError.CadastroChavePixConverterError
import br.com.zup.fabiano.shared.converterError.DeleteChavePixConverterError
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.http.uri.UriBuilder
import io.micronaut.validation.Validated
import java.time.LocalDateTime
import java.time.ZoneOffset
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

    @Get("/api/chave-pix/idClient/{idClient}")
    fun listarChavePix(@PathVariable idClient: String) : MutableHttpResponse<MutableList<ChavePixResponse>>? {
        val grpcRequest = ListarChavePixRequest
            .newBuilder()
            .setClientId(idClient)
            .build()

        try{
            val listaChave: MutableList<ChavePixResponse> = mutableListOf()

            grpcClient.listarChavePix(grpcRequest).let { response ->
                response.chavesList.map { chave ->
                    listaChave.add(ChavePixResponse(
                        chave.idCliente,
                        chave.idChavePix,
                        chave.tipoChave,
                        chave.chave,
                        chave.tipoConta,
                        LocalDateTime.ofEpochSecond(
                            chave.criadoEm.seconds,
                            chave.criadoEm.nanos,
                            ZoneOffset.UTC
                        )
                    ))
                }
            }
            return HttpResponse.ok<MutableList<ChavePixResponse>>().body(listaChave)
        }catch (e: StatusRuntimeException){
            throw HttpStatusException(DeleteChavePixConverterError.converter(e), e.status.description)
        }
    }

    @Get("/api/chave-pix/{id}/idClient/{idClient}")
    fun detalheChavePix(@PathVariable id: Long, @PathVariable idClient: String) : MutableHttpResponse<ChavePixDetalheResponse> {
        val grpcRequest = ConsultarChavePixKeyManagerRequest
            .newBuilder()
            .setIdChavePix(id.toString())
            .setIdCliente(idClient)
            .build()

        try{
            val response = grpcClient.consultarChavePixKeyManager(grpcRequest)
            return HttpResponse.ok<ChavePixDetalheResponse>().body(
                ChavePixDetalheResponse(
                    response.idCliente,
                    response.idChavePix,
                    response.tipoChave,
                    response.chave,
                    Titular(response.nome, response.cpf),
                    LocalDateTime.ofEpochSecond(
                        response.criadoEm.seconds,
                        response.criadoEm.nanos,
                        ZoneOffset.UTC
                    )
                )
            )
        }catch (e: StatusRuntimeException){
            throw HttpStatusException(DeleteChavePixConverterError.converter(e), e.status.description)
        }
    }
}