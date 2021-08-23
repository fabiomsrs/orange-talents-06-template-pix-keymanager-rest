package br.com.zup.fabiano

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zup.fabiano")
		.start()
}
