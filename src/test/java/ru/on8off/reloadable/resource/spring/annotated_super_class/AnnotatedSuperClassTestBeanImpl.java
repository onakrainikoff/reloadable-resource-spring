package ru.on8off.reloadable.resource.spring.annotated_super_class;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class AnnotatedSuperClassTestBeanImpl extends AnnotatedSuperClassTestBean {
    private Long number;

    @Override
    public Long getNumber() {
        return number;
    }

    @Override
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
