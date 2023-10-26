package com.things.one

import org.axonframework.springboot.autoconfig.AxonServerAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.*
import org.springframework.web.reactive.config.EnableWebFlux
import java.time.Clock
import java.util.*

@SpringBootApplication(scanBasePackages = ["com.things.one", "com.things.one.command", "com.things.one.query"])
@EnableWebFlux
class AxonApplication

fun main(args: Array<String>) {
	runApplication<AxonApplication>(*args)
}

@Profile("!test")
@Configuration
class AppConfig {
	@Bean
	fun randomUUID(): () -> UUID = { UUID.randomUUID() }
}

@Configuration
@Profile("!test")
@Import(AxonServerAutoConfiguration::class)
class AxonConfig
