// SPDX-FileCopyrightText: 2026 onakrainikoff
// SPDX-License-Identifier: Apache-2.0
package ru.on8off.reloadable.resource.spring;

import ru.on8off.reloadable.resource.ReloadableData;
import ru.on8off.reloadable.resource.loader.DataLoader;

import java.time.LocalDateTime;

/**
 * A {@link DataLoader} that repeatedly returns a fixed value.
 *
 * @param <T> type of the stub data
 */
public class StubDataLoader<T> implements DataLoader<T> {
    private final LocalDateTime stubDataLastModified;
    private final T stubData;

    /**
     * Creates a loader for the supplied fixed value.
     *
     * @param stubData value returned when loading; may be {@code null}
     */
    public StubDataLoader(T stubData) {
        this.stubData = stubData;
        this.stubDataLastModified = LocalDateTime.now();
    }

    /**
     * Returns the fixed value if it is newer than the supplied modification
     * time; otherwise returns {@code null} to indicate no change.
     *
     * @param lastModified modification time of the currently loaded data, or
     *                     {@code null} if no data has been loaded
     * @return the fixed value with its creation time, or {@code null} if it has
     *         not changed
     */
    @Override
    public ReloadableData<T> load(LocalDateTime lastModified) {
        if (lastModified == null || lastModified.isBefore(stubDataLastModified)) {
            return new ReloadableData<>(stubDataLastModified, stubData);
        } else {
            return null;
        }
    }
}
