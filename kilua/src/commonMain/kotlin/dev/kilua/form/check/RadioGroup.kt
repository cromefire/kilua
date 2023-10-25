/*
 * Copyright (c) 2023 Robert Jaros
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

package dev.kilua.form.check

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import dev.kilua.compose.ComponentNode
import dev.kilua.core.ComponentBase
import dev.kilua.core.DefaultRenderConfig
import dev.kilua.core.RenderConfig
import dev.kilua.form.StringFormControl
import dev.kilua.html.Div
import dev.kilua.state.ObservableDelegate
import dev.kilua.utils.StringPair

/**
 * Radio button group component.
 */
public open class RadioGroup(
    value: String? = null,
    inline: Boolean = false,
    name: String? = null,
    disabled: Boolean? = null,
    className: String? = null,
    renderConfig: RenderConfig = DefaultRenderConfig()
) : Div(className, renderConfig), StringFormControl {

    protected val observableDelegate: ObservableDelegate<String?> = ObservableDelegate()

    public override var value: String? by updatingProperty(
        value,
        skipUpdate,
        notifyFunction = { observableDelegate.notifyObservers(it) }) {
        findAllRadios().forEach { radio ->
            radio.value = radio.extraValue == it
        }
    }

    /**
     * The name attribute of the generated HTML radio input elements.
     */
    public open var inline: Boolean by updatingProperty(inline, skipUpdate) {
        children.forEach { child ->
            if (child is Div) {
                child.className = if (it) "kilua-radio-inline" else null
            }
        }
    }

    /**
     * The name attribute of the generated HTML radio input elements.
     */
    public override var name: String? by updatingProperty(name, skipUpdate) {
        findAllRadios().forEach { radio ->
            radio.name = it ?: "name_${componentId}"
        }
    }

    /**
     * The disabled attribute of the generated HTML radio input elements.
     */
    public override var disabled: Boolean? by updatingProperty(disabled, skipUpdate) {
        findAllRadios().forEach { radio ->
            radio.disabled = it
        }
    }

    /**
     * The autofocus attribute of the generated HTML radio input elements.
     */
    public override var autofocus: Boolean? by updatingProperty(skipUpdate = skipUpdate) {
        findAllRadios().firstOrNull()?.let { radio ->
            radio.autofocus = it
        }
    }

    /**
     * Find all radio button components in this group.
     */
    protected open fun findAllRadios(): List<Radio> {
        return children.flatMap { child ->
            if (child is Div) {
                child.children.mapNotNull { subChild ->
                    if (subChild is Radio) {
                        subChild
                    } else null
                }
            } else emptyList()
        }
    }

    override fun getValueAsString(): String? {
        return value.toString()
    }

    override fun subscribe(observer: (String?) -> Unit): () -> Unit {
        return observableDelegate.subscribe(value, observer)
    }
}

/**
 * Creates [RadioGroup] component.
 *
 * @param options a list of options (value to label pairs)
 * @param value initial value
 * @param inline determines if the options are rendered inline
 * @param name the name of the input
 * @param disabled whether the input is disabled
 * @param className the CSS class name
 * @param setup a function for setting up the component
 * @return a [RadioGroup] component
 */
@Composable
public fun ComponentBase.radioGroup(
    options: List<StringPair>? = null,
    value: String? = null,
    inline: Boolean = false,
    name: String? = null,
    disabled: Boolean? = null,
    className: String? = null,
    setup: @Composable RadioGroup.() -> Unit = {}
): RadioGroup {
    val component = remember { RadioGroup(value, inline, name, disabled, className, renderConfig) }
    DisposableEffect(component.componentId) {
        component.onInsert()
        onDispose {
            component.onRemove()
        }
    }
    ComponentNode(component, {
        set(value) { updateProperty(RadioGroup::value, it) }
        set(inline) { updateProperty(RadioGroup::inline, it) }
        set(name) { updateProperty(RadioGroup::name, it) }
        set(disabled) { updateProperty(RadioGroup::disabled, it) }
        set(className) { updateProperty(RadioGroup::className, it) }
    }) {
        setup(component)
        options?.forEachIndexed { index, option ->
            radio(
                label = option.second,
                value = option.first == component.value,
                name = component.name ?: "name_${component.componentId}",
                disabled = component.disabled,
                groupClassName = if (component.inline) "kilua-radio-inline" else null
            ) {
                if (index == 0 && component.autofocus == true) {
                    this.autofocus = true
                }
                this.extraValue = option.first
                onChange {
                    component.value = this.extraValue
                }
            }
        }
    }
    return component
}
