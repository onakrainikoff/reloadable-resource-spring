package ru.on8off.reloadable.resource.spring.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.w3c.dom.ls.LSOutput;
import ru.on8off.reloadable.resource.ReloadableData;
import ru.on8off.reloadable.resource.loader.DataLoader;
import ru.on8off.reloadable.resource.loader.FileDataLoader;
import ru.on8off.reloadable.resource.loader.HttpDataLoader;
import ru.on8off.reloadable.resource.mapper.JsonNodeDataMapper;
import ru.on8off.reloadable.resource.spring.ReloadableResourceConfiguration;
import ru.on8off.reloadable.resource.spring.ReloadableResourceManager;
import ru.on8off.reloadable.resource.spring.StubDataLoader;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@TestConfiguration
@SpringBootApplication
public class SpringConfiguration {

    @Bean
    public ReloadableResourceManager reloadableManager(
            @Autowired List<ReloadableResourceConfiguration<?>> configurations
    ){
        return new ReloadableResourceManager(configurations);
    }

    @Bean
    public ReloadableResourceConfiguration<JsonNode> reloadableResourceConfiguration(
            DataLoader<JsonNode> dataLoader,
            @Value("${reloadable.example.period}") Long period,
            @Value("${reloadable.example.periodTimeUnit}") TimeUnit periodTimeUnit,
            @Value("${reloadable.example.lazyMode}") boolean lazyMode,
            @Value("${reloadable.example.disabled}") boolean disabled

    ){
        return ReloadableResourceConfiguration.<JsonNode>builder()
                    .resourceId("example")
                    .dataLoader(dataLoader)
                    .period(period, periodTimeUnit)
                    .lazyMode(lazyMode)
                    .disabled(disabled)
                    .build();
    }


    @Bean
    @ConditionalOnProperty(name = "reloadable.example.dataLoader.type", havingValue = "http")
    public DataLoader<JsonNode> httpDataLoader(
            @Value("${reloadable.example.dataLoader.url}") String url
    ){
        return new HttpDataLoader<>(url, new JsonNodeDataMapper());
    }


    @Bean
    @ConditionalOnProperty(name = "reloadable.example.dataLoader.type", havingValue = "file")
    public DataLoader<JsonNode> fileDataLoader(
            @Value("${reloadable.example.dataLoader.path}") String path
    ){
        return new FileDataLoader<>(path, new JsonNodeDataMapper());
    }


    @Bean
    @ConditionalOnProperty(name = "reloadable.example.dataLoader.type", havingValue = "stub")
    public DataLoader<JsonNode> stubDataLoader(ObjectMapper objectMapper) {
        JsonNode data = objectMapper.valueToTree(Collections.emptyMap());
        return new StubDataLoader<>(data);
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }


}
