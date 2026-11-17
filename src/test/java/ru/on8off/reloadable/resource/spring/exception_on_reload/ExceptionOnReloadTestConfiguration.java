package ru.on8off.reloadable.resource.spring.exception_on_reload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.on8off.reloadable.resource.ReloadableData;
import ru.on8off.reloadable.resource.listener.ReloadableListener;
import ru.on8off.reloadable.resource.loader.DataLoader;
import ru.on8off.reloadable.resource.spring.ReloadableResourceConfiguration;
import ru.on8off.reloadable.resource.spring.ReloadableResourceManager;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@TestConfiguration
@SpringBootApplication
class ExceptionOnReloadTestConfiguration {
    @Bean
    public ReloadableResourceManager reloadableManager(
            @Autowired List<ReloadableResourceConfiguration<?>> configurations,
            @Qualifier("exceptionCounterCommon") AtomicLong exceptionCounterCommon
    ){
        ReloadableListener<Object> listener = new ReloadableListener<Object>(){
            @Override
            public void onReload(String name, DataLoader<?> dataLoader, ReloadableData<?> oldData, ReloadableData<?> newData, Exception ex, long totalTimeMs) {
                if (ex != null) {
                    exceptionCounterCommon.incrementAndGet();
                }
            }
        };

        return new ReloadableResourceManager(configurations, List.of(listener));
    }

    @Bean
    public ReloadableResourceConfiguration<Long> reloadableResourceConfiguration(
            ExceptionOnReloadTestDataLoader exceptionOnReloadTestDataLoader,
            @Qualifier("exceptionCounter") AtomicLong exceptionCounter
    ){

        ReloadableListener<Object> listener = new ReloadableListener<Object>(){
            @Override
            public void onReload(String name, DataLoader<?> dataLoader, ReloadableData<?> oldData, ReloadableData<?> newData, Exception ex, long totalTimeMs) {
                if (ex != null) {
                    exceptionCounter.incrementAndGet();
                }
            }
        };

        ReloadableResourceConfiguration<Long> resourceConfig = ReloadableResourceConfiguration.<Long>builder()
                .resourceId("exception_on_reload")
                .dataLoader(exceptionOnReloadTestDataLoader)
                .period(50, TimeUnit.MILLISECONDS)
                .addListener(listener)
                .build();
        log.info("----- created resourceConfig={}", resourceConfig);
        return resourceConfig;
    }

    @Bean
    public AtomicLong postConstructCounter(){
        return new AtomicLong();
    }

    @Bean
    public AtomicLong preDestroyCounter(){
        return new AtomicLong();
    }

    @Bean
    public AtomicLong exceptionCounter(){
        return new AtomicLong();
    }

    @Bean
    public AtomicLong exceptionCounterCommon(){
        return new AtomicLong();
    }

}
