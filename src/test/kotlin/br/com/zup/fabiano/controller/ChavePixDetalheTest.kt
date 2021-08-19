package br.com.zup.fabiano.controller

import br.com.zup.edu.ChavePixServiceGrpc
import br.com.zup.edu.ConsultarChavePixResponse
import br.com.zup.edu.Conta
import br.com.zup.edu.TipoConta
import br.com.zup.fabiano.dto.ChavePixDetalheResponse
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.mockito.BDDMockito
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class ChavePixDetalheTest {
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
    fun `teste deve retornar uma chave pix`(){
        val grpcResponse = ConsultarChavePixResponse
            .newBuilder()
            .setIdCliente("04673696310/c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setIdChavePix("1")
            .setChave("04673696310")
            .setConta(Conta.newBuilder().setTipoConta(TipoConta.CACC).setNumero("123456").setAgencia("1234").setInstituicao("ITAU").build())
            .setCpf("04673696310")
            .setNome("fulano")
            .setCriadoEm(Timestamp.getDefaultInstance())
            .build()

        BDDMockito.given(grpcClient.consultarChavePixKeyManager(Mockito.any())).willReturn(grpcResponse)

        val request = HttpRequest.GET<ChavePixDetalheResponse>("/api/chave-pix/04673696310/idClient/c56dfef4-7901-44fb-84e2-a2cefb157890")

        val response = httpClient.toBlocking().exchange(request,ChavePixDetalheResponse::class.java)
        with(response){
            Assertions.assertEquals(HttpStatus.OK, response.status)
            Assertions.assertEquals("1", response.body().idChavePix)
        }
    }


    @Test
    fun `teste deve retornar not found`(){
        BDDMockito.given(grpcClient.consultarChavePixKeyManager(Mockito.any())).willThrow(Status.NOT_FOUND.asRuntimeException())

        val request = HttpRequest.GET<ChavePixDetalheResponse>("/api/chave-pix/04673696310/idClient/c56dfef4-7901-44fb-84e2-a2cefb157890")

        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request,ChavePixDetalheResponse::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.status)
        }
    }

    @Test
    fun `teste deve retornar not found caso nao informe idClient`(){
        val request = HttpRequest.GET<ChavePixDetalheResponse>("/api/chave-pix/04673696310/idClient")

        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request,ChavePixDetalheResponse::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.status)
            Assertions.assertEquals(exception.message, "Page Not Found")
        }
    }

    @Singleton
    @Replaces(bean = ChavePixServiceGrpc.ChavePixServiceBlockingStub::class)
    fun blockingStub(): ChavePixServiceGrpc.ChavePixServiceBlockingStub? {
        return Mockito.mock(ChavePixServiceGrpc.ChavePixServiceBlockingStub::class.java)
    }
}