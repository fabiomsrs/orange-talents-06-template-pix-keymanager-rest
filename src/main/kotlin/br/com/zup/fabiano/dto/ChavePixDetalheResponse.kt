package br.com.zup.fabiano.dto

import br.com.zup.edu.TipoChave
import java.time.LocalDateTime

data class ChavePixDetalheResponse(
    val idClient: String,
    val idChavePix: String,
    val tipoChave: TipoChave,
    val chave: String,
    val titular: Titular,
    val criadoEm: LocalDateTime
)

data class Titular(val nome: String, val cpf: String)