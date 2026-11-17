package ru.on8off.reloadable.resource.spring.annotated_interface;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AnnotatedInterfaceTest {
    @Autowired
    private AnnotatedInterfaceTestBean annotatedInterfaceTestBean;
    @Autowired
    private AnnotatedInterfaceTestDataLoader annotatedInterfaceTestDataLoader;

    @Test
    @DirtiesContext
    public void testAnnotatedInterface() throws InterruptedException {
        assertNotNull(annotatedInterfaceTestBean);
        assertEquals(0, annotatedInterfaceTestBean.getNumber());

        Thread.sleep(100);
        annotatedInterfaceTestDataLoader.incrementAndGet();
        Thread.sleep(100);
        assertNotNull(annotatedInterfaceTestBean);
        assertEquals(1, annotatedInterfaceTestBean.getNumber());

    }

}