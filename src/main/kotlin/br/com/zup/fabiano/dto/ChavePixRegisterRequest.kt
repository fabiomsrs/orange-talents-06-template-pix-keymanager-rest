package br.com.zup.fabiano.dto

import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.fabiano.validator.Chave
import br.com.zup.fabiano.validator.ChaveTipoChaveCompatibilidade
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
@ChaveTipoChaveCompatibilidade
data class ChavePixRegisterRequest(
    @field:Chave val chave: String,
    @field:NotBlank val idCliente: String,
    @field:NotNull val tipoConta: TipoConta,
    @field:NotNull val tipoChave: TipoChave,
) {

}
