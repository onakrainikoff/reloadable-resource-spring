package ru.on8off.reloadable.resource.spring.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class Example {
    @Autowired
    private ExampleBean exampleBean;

    @Test
    @DirtiesContext
    public void testExample() {
        assertNotNull(exampleBean);
        assertNotNull(exampleBean.getData());
        System.out.println(exampleBean.getData());
    }
}
