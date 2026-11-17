package ru.on8off.reloadable.resource.spring.multy_dataloaders;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.on8off.reloadable.resource.spring.ReloadableBean;
import ru.on8off.reloadable.resource.spring.ReloadableValue;

@Slf4j
@Component
@ReloadableBean(resourceId = "multy_dataloaders")
class MultyDataLoadersTestBean {
    @ReloadableValue
    private Long number;

    public Long getNumber() {
        return number;
    }
}
