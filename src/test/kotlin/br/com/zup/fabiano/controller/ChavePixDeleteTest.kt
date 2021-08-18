package br.com.zup.fabiano.controller

import br.com.zup.edu.*
import br.com.zup.fabiano.dto.ChavePixDeleteRequest
import br.com.zup.fabiano.dto.ChavePixRegisterRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
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
internal class ChavePixDeleteTest {
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
    fun `teste deve retornar 200 para delete valido`(){
        val grpcResponse = RemoverChavePixGrpcResponse
            .newBuilder()
            .setIdChavePix("1")
            .build()

        BDDMockito.given(grpcClient.removerChavePix(Mockito.any())).willReturn(grpcResponse)

        val request = HttpRequest.DELETE("/api/chave-pix", ChavePixDeleteRequest(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "1",
            )
        )

        val response = httpClient.toBlocking().exchange(request, Any::class.java)
        with(response){
            Assertions.assertEquals(HttpStatus.OK, response.status)
        }
    }

    @Test
    fun `teste deve retornar bad request para id client null`(){
        val grpcResponse = RemoverChavePixGrpcResponse
            .newBuilder()
            .setIdChavePix("1")
            .build()

        BDDMockito.given(grpcClient.removerChavePix(Mockito.any())).willReturn(grpcResponse)

        val request = HttpRequest.DELETE("/api/chave-pix", ChavePixDeleteRequest(
            "",
            "1",
            )
        )
        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.status)
            Assertions.assertTrue(exception.message!!.contains("Missing required creator property 'idClient'"))
        }
    }

    @Test
    fun `teste deve retornar bad request para id chave pix null`(){
        val grpcResponse = RemoverChavePixGrpcResponse
            .newBuilder()
            .setIdChavePix("1")
            .build()

        BDDMockito.given(grpcClient.removerChavePix(Mockito.any())).willReturn(grpcResponse)

        val request = HttpRequest.DELETE("/api/chave-pix", ChavePixDeleteRequest(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "",
            )
        )
        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.status)
            Assertions.assertTrue(exception.message!!.contains("Missing required creator property 'idChavePix'"))
        }
    }

    @Test
    fun `teste deve retornar not found para id chave pix nao encontrado`(){

        BDDMockito.given(grpcClient.removerChavePix(Mockito.any()))
            .willThrow(Status.NOT_FOUND.withDescription("chave não encontrada").asRuntimeException())

        val request = HttpRequest.DELETE("/api/chave-pix", ChavePixDeleteRequest(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "1",
            )
        )
        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.status)
        }
    }

    @Test
    fun `teste deve retornar forbbiden para id chave pix nao pertencente a client id`(){

        BDDMockito.given(grpcClient.removerChavePix(Mockito.any()))
            .willThrow(Status.PERMISSION_DENIED.withDescription("chave pix nao pertence a esse cliente").asRuntimeException())

        val request = HttpRequest.DELETE("/api/chave-pix", ChavePixDeleteRequest(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "1",
        )
        )
        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.status)
            Assertions.assertEquals(exception.message ,"chave pix nao pertence a esse cliente")
        }
    }

    @Singleton
    @Replaces(bean = ChavePixServiceGrpc.ChavePixServiceBlockingStub::class)
    fun blockingStub(): ChavePixServiceGrpc.ChavePixServiceBlockingStub? {
        return Mockito.mock(ChavePixServiceGrpc.ChavePixServiceBlockingStub::class.java)
    }

}