package br.com.zup.fabiano.controller

import br.com.zup.edu.ChavePixServiceGrpc
import br.com.zup.edu.ListarChavePixResponse
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.fabiano.dto.ChavePixDetalheResponse
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ChaveListaTest(){
    @Inject
    lateinit var grpcClient: ChavePixServiceGrpc.ChavePixServiceBlockingStub

    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @BeforeEach
    fun restBefore() {
        Mockito.reset(grpcClient)
    }

    @AfterEach
    fun restAffter() {
        Mockito.reset(grpcClient)
    }

    @Test
    fun `teste deve retornar uma lista de chave pix`(){
        val tempo = LocalDateTime.now()

        val chave = ListarChavePixResponse.Chave
            .newBuilder()
            .setIdCliente("123456")
            .setIdChavePix("1")
            .setChave("12345678901")
            .setTipoChave(TipoChave.CPF)
            .setTipoConta(TipoConta.CACC)
            .setCriadoEm(
                Timestamp
                .newBuilder()
                .setSeconds(tempo.toEpochSecond(ZoneOffset.UTC))
                .setNanos(tempo.nano)
                .build()
            )
            .build()

        val grpcResponse = ListarChavePixResponse
            .newBuilder()
            .addAllChaves(listOf(chave))
            .build()

        BDDMockito.given(grpcClient.listarChavePix(Mockito.any())).willReturn(grpcResponse)

        val request = HttpRequest.GET<MutableHttpResponse<ChavePixDetalheResponse>>("/api/chave-pix/idClient/123456/")
        val response = httpClient.toBlocking().exchange(request, Any::class.java)

        Assertions.assertEquals(response.status.code, HttpStatus.OK.code)
        Assertions.assertEquals(response.body.stream().count(), 1)

    }

    @Test
    fun `teste deve retornar um not found`(){

        BDDMockito.given(grpcClient.listarChavePix(Mockito.any())).willThrow(Status.NOT_FOUND.asRuntimeException())

        val request = HttpRequest.GET<MutableHttpResponse<ChavePixDetalheResponse>>("/api/chave-pix/idClient/123456/")

        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request,ChavePixDetalheResponse::class.java)
        }

        with(exception){
            Assertions.assertEquals(exception.status.code, HttpStatus.NOT_FOUND.code)
        }

    }

    @Singleton
    @Replaces(bean = ChavePixServiceGrpc.ChavePixServiceBlockingStub::class)
    fun blockingStub(): ChavePixServiceGrpc.ChavePixServiceBlockingStub? {
        return Mockito.mock(ChavePixServiceGrpc.ChavePixServiceBlockingStub::class.java)
    }

}