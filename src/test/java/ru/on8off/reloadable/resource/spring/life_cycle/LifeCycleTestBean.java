package ru.on8off.reloadable.resource.spring.life_cycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.on8off.reloadable.resource.spring.ReloadableBean;
import ru.on8off.reloadable.resource.spring.ReloadableValue;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@ReloadableBean(resourceId = "life_cycle")
class LifeCycleTestBean {
    @Value("${testBean.testProperty}")
    private String testProperty;
    @Autowired
    private AtomicLong postConstructCounter;
    @Autowired
    private AtomicLong preDestroyCounter;
    private Long number;

    public Long getNumber() {
        return number;
    }


    @ReloadableValue
    public void setNumber(Long number){
        log.info("----- called setNumber: number={}, this.hashCode={}", number, this.hashCode());
        this.number = number;
    }

    public String getTestProperty() {
        return testProperty;
    }

    @PostConstruct
    public void postConstruct(){
        log.info("----- called postConstruct: number={}, this.hashCode={}", this.number, this.hashCode());
        postConstructCounter.incrementAndGet();
    }

    @PreDestroy
    public void preDestroy(){
        log.info("----- called preDestroy: number={}, this.hashCode={}", this.number, this.hashCode());
        preDestroyCounter.incrementAndGet();
    }
}
