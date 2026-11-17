package ru.on8off.reloadable.resource.spring.annotated_super_class;


import ru.on8off.reloadable.resource.spring.ReloadableBean;
import ru.on8off.reloadable.resource.spring.ReloadableValue;

@ReloadableBean(resourceId = "annotated_super_class")
abstract class AnnotatedSuperClassTestBean {
    abstract Long getNumber();

    @ReloadableValue
    abstract void setNumber(Long number);
}
