package br.com.zup.fabiano.dto

import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import java.time.LocalDateTime

data class ChavePixResponse(val idClient: String,
                            val idChavePix: String,
                            val tipoChave: TipoChave,
                            val chave: String,
                            val tipoConta: TipoConta,
                            val criadoEm: LocalDateTime
)