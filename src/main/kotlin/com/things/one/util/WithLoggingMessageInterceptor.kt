package com.things.one.util

import com.things.one.util.ConfigurableLogger.LogAtLevel.*
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.eventhandling.EventMessage
import org.axonframework.messaging.InterceptorChain
import org.axonframework.messaging.interceptors.MessageHandlerInterceptor
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.queryhandling.QueryMessage
import org.axonframework.spring.stereotype.Aggregate

abstract class WithLoggingMessageInterceptor: WithLogging {
    @MessageHandlerInterceptor(messageType = CommandMessage::class)
    fun <C : Any> intercept(message: CommandMessage<C>, interceptorChain: InterceptorChain): Any? {
        commandLogger.log(message.payload)
        return interceptorChain.proceed()
    }

    @MessageHandlerInterceptor(messageType = EventMessage::class)
    fun <E : Any> intercept(message: EventMessage<E>, interceptorChain: InterceptorChain): Any? {
        eventLogger.log(message.payload)
        return interceptorChain.proceed()
    }

    @MessageHandlerInterceptor(messageType = QueryMessage::class)
    fun <Q : Any> intercept(message: QueryMessage<Q, Any>, interceptorChain: InterceptorChain): Any? {
        queryLogger.log(message.payload)
        return interceptorChain.proceed()
    }


    open val commandLogger: ConfigurableLogger get() = ConfigurableLogger(this, prefix = "[  COMMAND ]")
    open val eventLogger: ConfigurableLogger get() = ConfigurableLogger(this, prefix = "[    EVENT ]")
    open val policyLogger: ConfigurableLogger get() = ConfigurableLogger(this, prefix = "[   POLICY ]")
    open val queryLogger: ConfigurableLogger get() = ConfigurableLogger(this, prefix = "[    QUERY ]")

    private val errorLogger: ConfigurableLogger get() = ConfigurableLogger(this, prefix = "[    ERROR ]")
    private val warningLogger: ConfigurableLogger get() = ConfigurableLogger(this, prefix = "[  WARNING ]")
    private val debugLogger: ConfigurableLogger get() = ConfigurableLogger(this, prefix = "")

    fun logEvent(message: Any) = eventLogger.log(message)
    fun logCommand(message: Any) = commandLogger.log(message)
    fun logQuery(message: Any) = queryLogger.log(message)
    fun logPolicy(message: Any) = policyLogger.log(message)

    fun logError(message: Any) = errorLogger.log(message, logLevel = ERROR)
    fun logWarning(message: Any) = warningLogger.log(message, logLevel = WARN)
    fun logDebug(message: Any) = debugLogger.log(message, logLevel = DEBUG)
    fun logTrace(message: Any) = debugLogger.log(message, logLevel = TRACE)

    fun commandLogger(block: ConfigurableLogger.() -> Unit) {
        commandLogger.apply(block)
    }
}

data class ConfigurableLogger(
    val loggingEntity: WithLogging,
    val prefix: String,
    val defaultLogLevel: LogAtLevel = DEBUG,
    val maxMessageLength: Int = 180,
    val enabled: Boolean = true,
    val renderMessage: (Any) -> String = { it.toString() }
) {

    private fun isLiveAggregate() = AggregateLifecycle.isLive()

    private fun isAnAggregate() =
        loggingEntity.javaClass.isAnnotationPresent(Aggregate::class.java)

    fun log(message: Any, logLevel: LogAtLevel = defaultLogLevel) {
        val shouldLogAggregate = enabled && isAnAggregate() && isLiveAggregate()
        val shouldLogOther = !isAnAggregate() && enabled
        if (shouldLogAggregate || shouldLogOther) when (logLevel) {
            TRACE -> loggingEntity.logger.trace(render(message, true))
            DEBUG -> loggingEntity.logger.debug(render(message, true))
            INFO -> loggingEntity.logger.info(render(message, true))
            WARN -> loggingEntity.logger.warn(render(message, false))
            ERROR -> loggingEntity.logger.error(render(message, false))
        }
    }

    fun enabled(isEnabled: Boolean) = copy(enabled = isEnabled)

    private fun render(payload: Any, truncate: Boolean = true): String {
        val payloadAsString = "$prefix ${renderMessage(payload)}"
        return if (truncate && payloadAsString.length >= maxMessageLength)
            payloadAsString.take(maxMessageLength) + "..." else payloadAsString
    }

    enum class LogAtLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    override fun equals(other: Any?): Boolean = other is ConfigurableLogger
    override fun hashCode(): Int = ConfigurableLogger::class.hashCode()
}
