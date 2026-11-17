// SPDX-FileCopyrightText: 2026 onakrainikoff
// SPDX-License-Identifier: Apache-2.0
package ru.on8off.reloadable.resource.spring;

import java.lang.annotation.*;

/**
 * Marks the single field or method on a {@link ReloadableBean} that receives
 * the resource value.
 *
 * <p>For a method, the method must accept the resource value as its argument.</p>
 *
 * @see ReloadableBean
 */
@Inherited
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReloadableValue {
}
