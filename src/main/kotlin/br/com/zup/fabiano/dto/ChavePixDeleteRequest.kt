package br.com.zup.fabiano.dto

import io.micronaut.core.annotation.Introspected

@Introspected
class ChavePixDeleteRequest(val idClient: String, val idChavePix: String)