package ru.on8off.reloadable.resource.spring.bean_by_interface;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.on8off.reloadable.resource.spring.ReloadableResourceConfiguration;
import ru.on8off.reloadable.resource.spring.ReloadableResourceManager;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@TestConfiguration
@SpringBootApplication
class BeanByInterfaceTestConfiguration {
    @Bean
    public ReloadableResourceManager reloadableManager(@Autowired List<ReloadableResourceConfiguration<?>> configurations){
        return new ReloadableResourceManager(configurations);
    }

    @Bean
    public BeanByInterfaceTestBean interfaceProxyingBean(){
        return new BeanByInterfaceTestBeanImpl();
    }

    @Bean
    public ReloadableResourceConfiguration<Long> reloadableResourceConfiguration(BeanByInterfaceTestDataLoader beanByInterfaceTestDataLoader){
        ReloadableResourceConfiguration<Long> resourceConfig =  ReloadableResourceConfiguration.<Long>builder()
                .resourceId("bean_by_interface")
                .dataLoader(beanByInterfaceTestDataLoader)
                .period(50, TimeUnit.MILLISECONDS)
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
}
