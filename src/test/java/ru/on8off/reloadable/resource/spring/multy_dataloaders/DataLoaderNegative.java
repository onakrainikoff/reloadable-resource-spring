package ru.on8off.reloadable.resource.spring.multy_dataloaders;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.on8off.reloadable.resource.ReloadableData;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Profile("MultyDataloadersNegativeTest")
class DataLoaderNegative implements TestDataLoader {
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

    @Override
    public Long update(){
        valueLastModified = LocalDateTime.now();
        return value.decrementAndGet();
    }
}
