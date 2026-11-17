package ru.on8off.reloadable.resource.spring.exception_on_reload;

import org.springframework.stereotype.Component;
import ru.on8off.reloadable.resource.ReloadableData;
import ru.on8off.reloadable.resource.loader.DataLoader;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Component
class ExceptionOnReloadTestDataLoader implements DataLoader<Long> {
    private AtomicLong value = new AtomicLong();
    private volatile LocalDateTime valueLastModified = LocalDateTime.now();

    @Override
    public ReloadableData<Long> load(LocalDateTime lastModified) {
        if (lastModified == null || lastModified.isBefore(valueLastModified)) {
            if (value.get() > 0) {
                throw new RuntimeException("Test exception_on_reload");
            }
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
