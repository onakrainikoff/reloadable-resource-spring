package ru.on8off.reloadable.resource.spring.bean_by_interface;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeanByInterfaceTest {
    @Autowired
    private BeanByInterfaceTestBean beanByInterfaceTestBean;
    @Autowired
    private BeanByInterfaceTestDataLoader beanByInterfaceTestDataLoader;

    @Test
    @DirtiesContext
    public void testBeanByInterface() throws InterruptedException {
        assertNotNull(beanByInterfaceTestBean);
        assertEquals(0, beanByInterfaceTestBean.getNumber());

        Thread.sleep(100);
        beanByInterfaceTestDataLoader.incrementAndGet();
        Thread.sleep(100);
        assertNotNull(beanByInterfaceTestBean);
        assertEquals(1, beanByInterfaceTestBean.getNumber());
    }

}