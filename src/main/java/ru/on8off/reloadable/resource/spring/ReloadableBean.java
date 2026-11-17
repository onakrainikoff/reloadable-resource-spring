// SPDX-FileCopyrightText: 2026 onakrainikoff
// SPDX-License-Identifier: Apache-2.0
package ru.on8off.reloadable.resource.spring;

import java.lang.annotation.*;

/**
 * Marks a Spring bean to reload by the {@link ReloadableResourceManager} when the resource value is chanced.
 *
 * <p>The bean must declare exactly one field or method annotated with {@link ReloadableValue}.</p>
 * <p>When the resource value is reloaded the {@link ReloadableResourceManager} creates new instance of this bean
 * with new resource value and actualizes Spring context </p>
 *
 * @see ReloadableValue
 * @see ReloadableResourceConfiguration
 * @see ReloadableResourceManager
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReloadableBean {
    /**
     * Identifier of the resource configuration used by this bean.
     *
     * @return resource identifier
     */
    String resourceId();
}
