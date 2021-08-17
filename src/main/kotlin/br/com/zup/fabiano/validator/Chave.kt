package br.com.zup.fabiano.validator

import org.hibernate.validator.constraints.CompositionType
import org.hibernate.validator.constraints.ConstraintComposition
import org.hibernate.validator.constraints.br.CPF
import javax.annotation.MatchesPattern
import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Email
import kotlin.reflect.KClass


@ConstraintComposition(CompositionType.OR)
@ReportAsSingleViolation
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@CPF
@Email
@MatchesPattern("^\\+[1-9][0-9]\\d{1,14}\$")
annotation class Chave(val message: String = "chave invalida(CPF,Telefone ou EMAIL)",
                       val groups: Array<KClass<Any>> = [],
                       val payload: Array<KClass<Payload>> = [])
