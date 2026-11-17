package ru.on8off.reloadable.resource.spring.multy_dataloaders;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.on8off.reloadable.resource.spring.ReloadableResourceConfiguration;
import ru.on8off.reloadable.resource.spring.ReloadableResourceManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@TestConfiguration
@SpringBootApplication
class MultyDataLoadersTestConfiguration {
    @Bean
    public ReloadableResourceManager reloadableManager(@Autowired List<ReloadableResourceConfiguration<?>> configurations){
        return new ReloadableResourceManager(configurations);
    }

    @Bean
    public ReloadableResourceConfiguration<Long> reloadableResourceConfiguration(TestDataLoader dataLoader){
        ReloadableResourceConfiguration<Long> resourceConfig =  ReloadableResourceConfiguration.<Long>builder()
                .resourceId("multy_dataloaders")
                .dataLoader(dataLoader)
                .period(50, TimeUnit.MILLISECONDS)
                .build();
        log.info("----- created resourceConfig={}", resourceConfig);
        return resourceConfig;
    }


}
