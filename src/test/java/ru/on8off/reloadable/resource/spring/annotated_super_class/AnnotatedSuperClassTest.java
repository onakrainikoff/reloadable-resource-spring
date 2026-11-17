package ru.on8off.reloadable.resource.spring.annotated_super_class;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AnnotatedSuperClassTest {
    @Autowired
    private AnnotatedSuperClassTestBean annotatedSuperClassTestBean;
    @Autowired
    private AnnotatedSuperClassTestDataLoader annotatedSuperClassTestDataLoader;

    @Test
    @DirtiesContext
    public void testAnnotatedInterface() throws InterruptedException {
        assertNotNull(annotatedSuperClassTestBean);
        assertEquals(0, annotatedSuperClassTestBean.getNumber());

        Thread.sleep(100);
        annotatedSuperClassTestDataLoader.incrementAndGet();
        Thread.sleep(100);
        assertNotNull(annotatedSuperClassTestBean);
        assertEquals(1, annotatedSuperClassTestBean.getNumber());
    }

}