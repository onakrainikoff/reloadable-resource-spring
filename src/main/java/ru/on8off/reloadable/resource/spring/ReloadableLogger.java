// SPDX-FileCopyrightText: 2026 onakrainikoff
// SPDX-License-Identifier: Apache-2.0
package ru.on8off.reloadable.resource.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.on8off.reloadable.resource.ReloadableData;
import ru.on8off.reloadable.resource.listener.ReloadableListener;
import ru.on8off.reloadable.resource.loader.DataLoader;

/**
 * A {@link ReloadableListener} that logs resource reload outcomes using SLF4J.
 */
public class ReloadableLogger implements ReloadableListener<Object> {
    private final Logger log;

    /**
     * Creates a logger backed by an SLF4J logger for this class.
     */
    public ReloadableLogger() {
        this(null);
    }

    /**
     * Creates a logger backed by the supplied SLF4J logger.
     *
     * @param log logger to use; if {@code null}, a logger for this class is used
     */
    public ReloadableLogger(Logger log) {
        if (log == null) {
            this.log = LoggerFactory.getLogger(this.getClass());
        } else {
            this.log = log;
        }
    }

    /**
     * Logs whether a reload failed, succeeded, or found no modification.
     *
     * @param name resource name
     * @param dataLoader loader used for the reload
     * @param oldData previously loaded data, if any
     * @param newData newly loaded data, if any
     * @param ex exception raised during the reload, or {@code null} on success
     * @param totalTimeMs elapsed reload time in milliseconds
     */
    @Override
    public void onReload(String name, DataLoader<?> dataLoader, ReloadableData<?> oldData, ReloadableData<?> newData, Exception ex, long totalTimeMs) {
        if (ex != null) {
            log.error("{}: failed to reload. Time: {} ms", name, totalTimeMs, ex);
        } else if (newData != null){
            log.info("'{}' resource reloaded successfully. Time: {} ms", name, totalTimeMs);
        } else {
            log.info("'{}': resource not modified. Time: {} ms", name, totalTimeMs);
        }
    }
}
