package br.com.zup.fabiano.validator

import br.com.zup.edu.TipoChave
import br.com.zup.fabiano.dto.ChavePixRegisterRequest
import io.micronaut.context.annotation.Factory
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ChaveTipoChaveCompatibilidadeValidator::class])
annotation class ChaveTipoChaveCompatibilidade(val message: String = "Tipo não é compativel com chave",
                                               val groups: Array<KClass<Any>> = [],
                                               val payload: Array<KClass<Payload>> = [])



class ChaveTipoChaveCompatibilidadeValidator: ConstraintValidator<ChaveTipoChaveCompatibilidade, ChavePixRegisterRequest> {
    override fun isValid(value: ChavePixRegisterRequest?, context: ConstraintValidatorContext?): Boolean {
        if(value?.chave.isNullOrEmpty()){
            return true
        }
        when {
            value!!.tipoChave == TipoChave.CPF && !value!!.chave.matches("^[0-9]{11}\$".toRegex()) -> {
                context!!.disableDefaultConstraintViolation()
                context!!.buildConstraintViolationWithTemplate("Chave não é compativel com o Tipo CPF").addConstraintViolation()
                return false
            }
            value!!.tipoChave == TipoChave.PHONE && !value!!.chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex()) -> {
                context!!.disableDefaultConstraintViolation()
                context!!.buildConstraintViolationWithTemplate("Chave não é compativel com o Tipo Telefone").addConstraintViolation()
                return false
            }
            value!!.tipoChave == TipoChave.EMAIL && !value!!.chave.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()) -> {
                context!!.disableDefaultConstraintViolation()
                context!!.buildConstraintViolationWithTemplate("Chave não é compativel com o Tipo Email").addConstraintViolation()
                return false
            }
        }
        return true
    }
}