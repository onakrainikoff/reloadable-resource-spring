package ru.on8off.reloadable.resource.spring.lazy_mode;

import org.springframework.stereotype.Component;
import ru.on8off.reloadable.resource.ReloadableData;
import ru.on8off.reloadable.resource.loader.DataLoader;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Component
class LazyModeTestDataLoader implements DataLoader<Long> {
    private AtomicLong value = new AtomicLong();
    private volatile LocalDateTime valueLastModified = LocalDateTime.now();

    @Override
    public ReloadableData<Long> load(LocalDateTime lastModified) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (lastModified == null || lastModified.isBefore(valueLastModified)) {
            return new ReloadableData<>(valueLastModified, value.get());
        } else {
            return null;
        }
    }

    public Long incrementAndGet(){
        valueLastModified = LocalDateTime.now();
        return value.incrementAndGet();
    }
}
