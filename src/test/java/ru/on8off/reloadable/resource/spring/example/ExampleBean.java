package ru.on8off.reloadable.resource.spring.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.on8off.reloadable.resource.spring.ReloadableBean;
import ru.on8off.reloadable.resource.spring.ReloadableValue;

@Service
@ReloadableBean(resourceId = "example")
public class ExampleBean {
    @Autowired
    private ObjectMapper objectMapper;
    private String formatedData;


    public String getData() {
        return formatedData;
    }

    @ReloadableValue
    public void setData(JsonNode data) throws JsonProcessingException {
        if (data != null) {
            this.formatedData = objectMapper.writerWithDefaultPrettyPrinter()
                                            .writeValueAsString(data);
        }
    }

}
