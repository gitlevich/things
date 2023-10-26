package com.things.one.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface WithLogging {
    val logger: Logger get() = LoggerFactory.getLogger(this::class.java)
}

