package com.library.polargx.helpers

import kotlinx.serialization.json.Json

/**
 * Shared JSON configuration for the entire SDK.
 * Use this instance for all manual JSON serialization/deserialization operations.
 */
object JsonConfig {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        prettyPrint = true // Enable for debugging if needed
    }
}

