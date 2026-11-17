// SPDX-FileCopyrightText: 2026 onakrainikoff
// SPDX-License-Identifier: Apache-2.0
package ru.on8off.reloadable.resource.spring;

import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.Validate;
import ru.on8off.reloadable.resource.listener.ReloadableListener;
import ru.on8off.reloadable.resource.loader.DataLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for a reloadable resource managed by {@link ReloadableResourceManager}.
 *
 * @param <T> type of data loaded by the configured {@link DataLoader}
 */
@Getter
@ToString
public class ReloadableResourceConfiguration<T> {
    private final String resourceId;
    private final DataLoader<T> dataLoader;
    private final long period;
    private final TimeUnit periodTimeUnit;
    private final List<ReloadableListener<? super T>> listeners;
    private final boolean lazyMode;
    private final boolean disabled;


    private ReloadableResourceConfiguration(Builder<T> builder) {
        this.resourceId = builder.resourceId;
        this.dataLoader = builder.dataLoader;
        this.period = builder.period;
        this.periodTimeUnit = builder.periodTimeUnit;
        this.listeners = new ArrayList<>(builder.listeners);
        this.lazyMode = builder.lazyMode;
        this.disabled = builder.disabled;
    }

    /**
     * Creates a builder for a reloadable resource configuration.
     *
     * @param <T> type of data loaded by the configured {@link DataLoader}
     * @return a new configuration builder
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * Builds an immutable reloadable resource configuration.
     *
     * @param <T> type of data loaded by the configured {@link DataLoader}
     */
    public static class Builder<T> {
        private String resourceId;
        private DataLoader<T> dataLoader;
        private long period = 0;
        private TimeUnit periodTimeUnit = null;
        private final List<ReloadableListener<? super T>> listeners = new ArrayList<>();
        private boolean lazyMode = false;
        private boolean disabled = false;

        private Builder() {
        }

        /**
         * Sets the unique identifier used to associate this reloadable resource with
         * {@link ReloadableBean}-annotated beans.
         *
         * @param resourceId reloadable resource identifier
         * @return this builder
         */
        public Builder<T> resourceId(String resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        /**
         * Sets the loader used to retrieve resource data.
         *
         * @param dataLoader resource data loader
         * @return this builder
         */
        public Builder<T> dataLoader(DataLoader<T> dataLoader) {
            this.dataLoader = dataLoader;
            return this;
        }

        /**
         * Sets the interval between resource reload attempts.
         *
         * @param period positive reload interval
         * @param periodTimeUnit unit of {@code period}
         * @return this builder
         */
        public Builder<T> period(long period, TimeUnit periodTimeUnit) {
            this.period = period;
            this.periodTimeUnit = periodTimeUnit;
            return this;
        }

        /**
         * Adds a listener notified about reload results. A {@code null} listener
         * is ignored.
         *
         * @param listener listener to add
         * @return this builder
         */
        public Builder<T> addListener(ReloadableListener<? super T> listener) {
            if (listener != null) {
                this.listeners.add(listener);
            }
            return this;
        }

        /**
         * Replaces the listeners currently configured on this builder.
         *
         * @param listeners listeners to use; {@code null} clears the list
         * @return this builder
         */
        public Builder<T> listeners(List<ReloadableListener<? super T>> listeners) {
            this.listeners.clear();
            if (listeners != null) {
                this.listeners.addAll(listeners);
            }
            return this;
        }

        /**
         * Sets whether the initial resource loading will be performed only after bean initialization.
         *
         * @param lazyMode {@code true} to defer the initial load
         * @return this builder
         */
        public Builder<T> lazyMode(boolean lazyMode) {
            this.lazyMode = lazyMode;
            return this;
        }


        /**
         * Sets whether this resource is disabled. Disabled resources are not
         * created and reloaded by the manager.
         *
         * @param disabled {@code true} to disable the resource
         * @return this builder
         */
        public Builder<T> disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        /**
         * Creates the reloadable resource configuration.
         *
         * @return the reloadable resource configuration.
         * @throws IllegalArgumentException if the resourceId is empty or the
         *                                  reload period is not positive
         * @throws NullPointerException if the data loader or period unit is
         *                              {@code null}
         */
        public ReloadableResourceConfiguration<T> build() {
            Validate.notEmpty(resourceId, "'resourceId' must not be null or Empty");
            if (!disabled) {
                Validate.notNull(dataLoader, "'dataLoader' must not be null");
                Validate.notNull(periodTimeUnit, "'periodTimeUnit' must not be null");
                Validate.isTrue(period > 0, "'period' must be > 0");
            }
            return new ReloadableResourceConfiguration<>(this);
        }
    }
}
