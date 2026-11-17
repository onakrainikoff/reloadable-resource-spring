package ru.on8off.reloadable.resource.spring.annotated_interface;

import org.springframework.stereotype.Component;
import ru.on8off.reloadable.resource.ReloadableData;
import ru.on8off.reloadable.resource.loader.DataLoader;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Component
class AnnotatedInterfaceTestDataLoader implements DataLoader<Long> {
    private AtomicLong value = new AtomicLong();
    private volatile LocalDateTime valueLastModified = LocalDateTime.now();

    @Override
    public ReloadableData<Long> load(LocalDateTime lastModified) {
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
