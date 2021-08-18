package br.com.zup.fabiano.controller

import br.com.zup.edu.ChavePixServiceGrpc
import br.com.zup.edu.RegistrarChavePixGrpcResponse
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.fabiano.dto.ChavePixRegisterRequest
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
internal class ChavePixRegisterTest(){
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
    fun `teste deve registrar chave pix`(){
        val grpcResponse = RegistrarChavePixGrpcResponse
            .newBuilder()
            .setIdChavePix("1")
            .build()

        BDDMockito.given(grpcClient.registrarChavePix(Mockito.any())).willReturn(grpcResponse)

        val request = HttpRequest.POST("/api/chave-pix", ChavePixRegisterRequest(
            "04673696310",
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoConta.CACC,
            TipoChave.CPF
        ))

        val response = httpClient.toBlocking().exchange(request, Any::class.java)
        with(response){
            Assertions.assertEquals(HttpStatus.CREATED, response.status)
        }
    }

    @Test
    fun `teste deve retornar error bad request chave pix null`(){
        val grpcResponse = RegistrarChavePixGrpcResponse
            .newBuilder()
            .setIdChavePix("1")
            .build()

        BDDMockito.given(grpcClient.registrarChavePix(Mockito.any())).willReturn(grpcResponse)

        val request = HttpRequest.POST("/api/chave-pix", ChavePixRegisterRequest(
            "",
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoConta.CACC,
            TipoChave.CPF
        ))

        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status)
            Assertions.assertTrue(exception.message!!.contains("Missing required creator property 'chave'"))
        }
    }

    @Test
    fun `teste deve retornar error bad request cliente id null`(){
        val grpcResponse = RegistrarChavePixGrpcResponse
            .newBuilder()
            .setIdChavePix("1")
            .build()

        BDDMockito.given(grpcClient.registrarChavePix(Mockito.any())).willReturn(grpcResponse)

        val request = HttpRequest.POST("/api/chave-pix", ChavePixRegisterRequest(
            "04673696310",
            "",
            TipoConta.CACC,
            TipoChave.CPF
        ))

        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status)
            Assertions.assertTrue(exception.message!!.contains("Missing required creator property 'idCliente'"))
        }
    }

    @Test
    fun `teste deve retornar error unprocessable entity chave ja existe`(){
        BDDMockito.given(grpcClient
            .registrarChavePix(Mockito.any()))
            .willThrow(Status.ALREADY_EXISTS.withDescription("chave ja existe").asRuntimeException())

        val request = HttpRequest.POST("/api/chave-pix", ChavePixRegisterRequest(
            "04673696310",
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoConta.CACC,
            TipoChave.CPF
        ))

        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.status)
            Assertions.assertEquals(exception.message, "chave ja existe")
        }
    }

    @Test
    fun `teste deve retornar error bad request cliente id invalido`(){
        BDDMockito.given(grpcClient
            .registrarChavePix(Mockito.any()))
            .willThrow(Status.INVALID_ARGUMENT.withDescription("id cliente invalido").asRuntimeException())

        val request = HttpRequest.POST("/api/chave-pix", ChavePixRegisterRequest(
            "04673696310",
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoConta.CACC,
            TipoChave.CPF
        ))

        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status)
            Assertions.assertEquals(exception.message, "id cliente invalido")
        }
    }

    @Test
    fun `teste deve retornar error bad request tipo chave cpf nao é compativel com chave cpf`(){
        val grpcResponse = RegistrarChavePixGrpcResponse
            .newBuilder()
            .setIdChavePix("1")
            .build()

        BDDMockito.given(grpcClient.registrarChavePix(Mockito.any())).willReturn(grpcResponse)
        val request = HttpRequest.POST("/api/chave-pix", ChavePixRegisterRequest(
            "f@f.com",
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoConta.CACC,
            TipoChave.CPF
        ))

        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status)
            Assertions.assertEquals(exception.message, "request.null: Chave não é compativel com o Tipo CPF")
        }
    }

    @Test
    fun `teste deve retornar error bad request tipo chave phone nao é compativel com chave phone`(){
        val grpcResponse = RegistrarChavePixGrpcResponse
            .newBuilder()
            .setIdChavePix("1")
            .build()

        BDDMockito.given(grpcClient.registrarChavePix(Mockito.any())).willReturn(grpcResponse)
        val request = HttpRequest.POST("/api/chave-pix", ChavePixRegisterRequest(
            "f@f.com",
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoConta.CACC,
            TipoChave.PHONE
        ))

        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status)
            Assertions.assertEquals(exception.message, "request.null: Chave não é compativel com o Tipo Telefone")
        }
    }

    @Test
    fun `teste deve retornar error bad request tipo chave email nao é compativel com chave email`(){
        val grpcResponse = RegistrarChavePixGrpcResponse
            .newBuilder()
            .setIdChavePix("1")
            .build()

        BDDMockito.given(grpcClient.registrarChavePix(Mockito.any())).willReturn(grpcResponse)
        val request = HttpRequest.POST("/api/chave-pix", ChavePixRegisterRequest(
            "04673696310",
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            TipoConta.CACC,
            TipoChave.EMAIL
        ))

        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }
        with(exception){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.status)
            Assertions.assertEquals(exception.message, "request.null: Chave não é compativel com o Tipo Email")
        }
    }

    @Singleton
    @Replaces(bean = ChavePixServiceGrpc.ChavePixServiceBlockingStub::class)
    fun blockingStub(): ChavePixServiceGrpc.ChavePixServiceBlockingStub? {
       return Mockito.mock(ChavePixServiceGrpc.ChavePixServiceBlockingStub::class.java)
    }

}