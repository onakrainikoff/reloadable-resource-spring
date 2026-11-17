package ru.on8off.reloadable.resource.spring.annotated_interface;


import ru.on8off.reloadable.resource.spring.ReloadableBean;
import ru.on8off.reloadable.resource.spring.ReloadableValue;

@ReloadableBean(resourceId = "annotated_interface")
public interface AnnotatedInterfaceTestBean {
    Long getNumber();
    @ReloadableValue
    void setNumber(Long number);
}
