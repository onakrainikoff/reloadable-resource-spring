package ru.on8off.reloadable.resource.spring.disabled;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.on8off.reloadable.resource.spring.ReloadableBean;
import ru.on8off.reloadable.resource.spring.ReloadableValue;

@Slf4j
@Component
@ReloadableBean(resourceId = "disabled")
class DisabledTestBean {
    private Long number;

    public Long getNumber() {
        return number;
    }

    @ReloadableValue
    public void setNumber(Long number) {
        log.info("----- called setNumber: number={}, this.hashCode={}", number, this.hashCode());
        this.number = number;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("----- called postConstruct: number={}, this.hashCode={}", this.number, this.hashCode());
    }

    @PreDestroy
    public void preDestroy() {
        log.info("----- called preDestroy: number={}, this.hashCode={}", this.number, this.hashCode());
    }

}
