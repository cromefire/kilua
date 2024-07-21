/*
 * Copyright (c) 2017-present Robert Jaros
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.kilua.utils

import dev.kilua.externals.JSON
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import dev.kilua.dom.JsAny

/**
 * JSON serialization utility functions
 */
public object Serialization {

    /**
     * Custom JSON configuration for the application.
     */
    public var customConfiguration: Json? = null

    /**
     * An extension function to convert Serializable object to JS object
     */
    public inline fun <reified T : Any> T.toObj(): JsAny {
        return this.toObj(serializer())
    }

    /**
     * An extension function to convert Serializable object to JS object
     * @param serializer a serializer for T
     */
    public fun <T> T.toObj(serializer: SerializationStrategy<T>): JsAny {
        return JSON.parse((customConfiguration ?: Json).encodeToString(serializer, this))
    }
}
