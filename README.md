# reloadable-resource-spring
[![](https://jitpack.io/v/onakrainikoff/reloadable-resource-spring.svg)](https://jitpack.io/#onakrainikoff/reloadable-resource-spring)

`reloadable-resource-spring` is a Spring integration of the [reloadable-resource-4j](https://github.com/onakrainikoff/reloadable-resource-4j) library.

## Features
- Managing Spring beans that depend on resource data.
- Periodic detection of resource changes using modification time and reloading managed beans.
- Loading data from files, classpath resources, HTTP endpoints and any other custom data sources.
- Decompression of `.zip` or `.gz` files.
- Mapping loaded data to Java types using built-in or custom mappers.
- Listeners mechanism for observing data reload events.

See also [reloadable-resource-4j  documentation](https://github.com/onakrainikoff/reloadable-resource-4j).

## Requirements

- Java 17 or later.
- Spring 6.2+ or Spring Boot 3.5+ 

## Installation

Maven:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.onakrainikoff</groupId>
    <artifactId>reloadable-resource-spring</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>com.github.onakrainikoff</groupId>
    <artifactId>reloadable-resource-4j</artifactId>
    <version>1.1.0</version>
</dependency>
```

Gradle:

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.onakrainikoff:reloadable-resource-spring:1.0.0'
    implementation 'com.github.onakrainikoff:reloadable-resource-4j:1.1.0'
}
```

## Usage sample

1. Annotate your bean class with @ReloadableBean and @ReloadableValue:

```java
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
```

2. Configure ReloadableResource:

```java
@Configuration
public class SpringConfiguration {
    //...
    @Bean
    public ReloadableResourceConfiguration<JsonNode> reloadableResourceConfiguration(@Autowired DataLoader<JsonNode> dataLoader){
        return ReloadableResourceConfiguration.<JsonNode>builder()
                    .resourceId("example")
                    .dataLoader(dataLoader)
                    .period(60, TimeUnit.SECONDS)
                    .lazyMode(false)
                    .disabled(false)
                    .build();
    }
    //...
}
```

3. Configure DataLoader:

```java
@Configuration
public class SpringConfiguration {
    //...
    @Bean
    public DataLoader<JsonNode> stubDataLoader(ObjectMapper objectMapper) {
        JsonNode data = objectMapper.valueToTree(Collections.emptyMap());
        return new StubDataLoader<>(data);
    }
    //...
}
```

4. Configure global ReloadableResourceManager:

```java
@Configuration
public class SpringConfiguration {
    //...  
    @Bean
    public ReloadableResourceManager reloadableManager(@Autowired List<ReloadableResourceConfiguration<?>> configurations) {
        return new ReloadableResourceManager(configurations);
    }
    //...
}
```

5. Autowire your bean and use actual resource data value:

```java
//..
@Autowired
private ExampleBean exampleBean;
//..
    public void test() {
        System.out.println(exampleBean.getData());
    }
//..
}
```

## Annotations

1. `@ReloadableBean(resourceId)` marks a Spring bean to reload by the `ReloadableResourceManager` when the resource with `resourceId` changes.
2. `@ReloadableValue` marks the field or method that receives the resource value. Only one field or method may be annotated.

Annotations can be used in classes, superclasses and interfaces.

## ReloadableResourceConfiguration

ReloadableResourceConfiguration describes reloadable resource:

| Properties | Purpose |
|---|---|
| `resourceId` | unique identifier used to associate this reloadable resource with `@ReloadableBean`-annotated beans|
| `dataLoader` | loader used to retrieve resource data. More about `DataLoader` and `DataMapper` [see](https://github.com/onakrainikoff/reloadable-resource-4j#usage) |
| `period` and `periodTimeUnit`| interval between resource reload attempts|
| `lazyMode` | if true - related beans will be created with value=null and first resource data loading will be performed asynchronous only after bean initialization|
| `disabled` | if true - this resource is not creating and reloading by the manager. Related beans will be created but setting value process will never be called|
| `listeners` | listeners are notified of the results of reloading only this resource|


## DataLoader

DataLoader loads a resource data, optionally using the previous source modification time to avoid reloading unchanged data. More about `DataLoader` and `DataMapper` [see](https://github.com/onakrainikoff/reloadable-resource-4j#usage).

It is possible to define more than one `DataLoader` for reloadable resource like this:

```java
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

```

## ReloadableResourceManager

ReloadableResourceManager integrates reloadable resources with the Spring bean lifecycle.
On the one hand, the manager creates reloadable resources and asynchronously preloads resources data.
On the other hand, it manages the lifecycle of beans marked as `@ReloadableBean`.

### Configuration
The ReloadableResourceManager is defined only once for all reloadable resource in a  String application.

It is possible to append custom ReloadableListeners for all resources through the ReloadableResourceManager.

It is possible to set custom ScheduledExecutorService for all resources through the ReloadableResourceManager.

It is possible to set custom ReloadableLogger through the ReloadableResourceManager.

### Lifecycle

When resource is changed the manager creates a new instance of annotated bean with new resource data value and destroy previous instance.
The setting value occurs in the field or method marked by the `@ReloadableValue` annotation and occurs between `Aware` and `@PostConstruct`.

### LazyMode 

For ReloadableResourceConfiguration with `lazyMode=true` related beans will be created with value=null and first resource data loading will be performed asynchronous only after bean initialization.

### Disabled

For ReloadableResourceConfiguration with `disabled=true` the manager will not create reloadable resource. Related beans will be created but setting value process will never be called.

## Dependencies

| Dependency | Version | Purpose |
|---|---:|---|
| `org.apache.commons:commons-lang3` | `3.18.0` |
| `commons-io:commons-io` | `2.16.1` |
| `org.apache.httpcomponents.client5:httpclient5` | `5.6.5` |
| `com.fasterxml.jackson.core:jackson-databind` | `2.22.4` |

## License

Copyright 2026 onakrainikoff. This project is licensed under the
[Apache License, Version 2.0](LICENSE).
