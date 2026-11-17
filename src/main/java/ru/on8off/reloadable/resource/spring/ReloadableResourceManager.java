// SPDX-FileCopyrightText: 2026 onakrainikoff
// SPDX-License-Identifier: Apache-2.0
package ru.on8off.reloadable.resource.spring;

import org.apache.commons.lang3.Validate;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.HotSwappableTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import ru.on8off.reloadable.resource.ManagedReloadableResource;
import ru.on8off.reloadable.resource.ReloadableException;
import ru.on8off.reloadable.resource.listener.ReloadableListener;
import ru.on8off.reloadable.resource.listener.SetNewDataValueTo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;


/**
 * Integrates reloadable resources with the Spring bean lifecycle.
 *
 * <p>The manager creates new instance of annotated by {@link ReloadableBean} beans with new resource values
 * after reloads managed resources, and actualizes Spring context and shuts down managed resources when
 * the Spring context is destroyed.</p>
 */
public class ReloadableResourceManager implements BeanPostProcessor, BeanFactoryAware, DisposableBean {
    /** Bean factory used to create and destroy reloadable bean targets. */
    protected ConfigurableListableBeanFactory beanFactory;
    /** Configurations indexed by resource identifier, including disabled ones. */
    protected final Map<String, ReloadableResourceConfiguration<?>> reloadableResourceConfigurations;
    /** Enabled reloadable resources indexed by resource identifier. */
    protected final Map<String, ManagedReloadableResource> reloadableResources;
    /** Current target instances indexed by Spring bean name. */
    protected final Map<String, Object> beans = new ConcurrentHashMap<>();
    /** Asynchronous initial-load tasks indexed by resource identifier. */
    protected final Map<String, Future<?>> preloaded = new ConcurrentHashMap<>();

    /**
     * Creates a manager with the default logger and default ScheduledExecutorService.
     *
     * @param reloadableResourceConfigurations non-empty resource configurations
     */
    public ReloadableResourceManager(List<ReloadableResourceConfiguration<?>> reloadableResourceConfigurations) {
        this(reloadableResourceConfigurations, null,  null, null);
    }

    /**
     *
     * Creates a manager with common listeners shared by all enabled resources and with default ScheduledExecutorService.
     *
     * @param reloadableResourceConfigurations non-empty resource configurations
     * @param commonListeners listeners applied to every enabled resource
     */
    public ReloadableResourceManager(List<ReloadableResourceConfiguration<?>> reloadableResourceConfigurations, List<ReloadableListener<?>> commonListeners) {
        this(reloadableResourceConfigurations, commonListeners,  null, null);
    }
    /**
     * Creates a manager using the supplied ScheduledExecutorService for its resources.
     *
     * @param reloadableResourceConfigurations non-empty resource configurations
     * @param commonExecutorService executor service shared by the resources
     */
    public ReloadableResourceManager(List<ReloadableResourceConfiguration<?>> reloadableResourceConfigurations, ScheduledExecutorService commonExecutorService) {
        this(reloadableResourceConfigurations, null,  commonExecutorService, null);
    }

    /**
     * Creates a manager with shared listeners, ScheduledExecutorService, and logger.
     * Optional arguments may be {@code null}.
     *
     * @param reloadableResourceConfigurations non-empty resource configurations
     * @param commonListeners listeners applied to every enabled resource
     * @param commonExecutorService executor service shared by the resources
     * @param reloadableLogger logger listener; a default logger is used if null
     * @throws IllegalArgumentException if configurations are null or empty, or
     *                                  contain duplicate resourceIds
     */
    public ReloadableResourceManager(List<ReloadableResourceConfiguration<?>> reloadableResourceConfigurations, List<ReloadableListener<?>> commonListeners, ScheduledExecutorService commonExecutorService, ReloadableLogger reloadableLogger) {
        Validate.notNull(reloadableResourceConfigurations, "reloadableResourceConfigurations 'path' must not be null");
        Validate.isTrue(!reloadableResourceConfigurations.isEmpty(), "reloadableResourceConfigurations must be empty");
        this.reloadableResourceConfigurations = new ConcurrentHashMap<>();
        this.reloadableResources = new ConcurrentHashMap<>();
        // create ReloadableResources
        for (ReloadableResourceConfiguration<?> configuration : reloadableResourceConfigurations) {
            // check resourceId duplication in configurations
            if(this.reloadableResourceConfigurations.containsKey(configuration.getResourceId())){
               throw new IllegalArgumentException("Duplicated resourceId=" + configuration.getResourceId());
            }
            this.reloadableResourceConfigurations.put(configuration.getResourceId(), configuration);
            // skip creation if disabled
            if (configuration.isDisabled()) {
                continue;
            }
            List<ReloadableListener<?>> listeners = new ArrayList<>();
            // add logger
            listeners.add(reloadableLogger != null ? reloadableLogger : new ReloadableLogger());
            // add common listeners
            if(commonListeners != null && !commonListeners.isEmpty()) {
                listeners.addAll(commonListeners);
            }
            // add resource listeners
            if (!configuration.getListeners().isEmpty()) {
                listeners.addAll(configuration.getListeners());
            }
            ManagedReloadableResource reloadableResource = new ManagedReloadableResource(
                    configuration.getResourceId(),
                    configuration.getDataLoader(),
                    configuration.getPeriod(),
                    configuration.getPeriodTimeUnit(),
                    listeners,
                    commonExecutorService
            );
            reloadableResources.put(configuration.getResourceId(), reloadableResource);
            // start asynchronous preloading, except for the resources in LasyMode, they will be loaded after context initialization
            if (!configuration.isLazyMode()) {
                preloaded.put(configuration.getResourceId(), reloadableResource.loadAsync());
            }
        }

    }

    /**
     * Stores the configurable bean factory used to create and destroy bean
     * instances.
     *
     * @param beanFactory Spring bean factory
     * @throws BeansException if the bean factory cannot be set
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        }
    }

    /**
     * Wraps beans marked with {@link ReloadableBean} in a proxy and initializes
     * their {@link ReloadableValue} from the associated resource.
     *
     * @param bean bean instance currently being initialized
     * @param beanName name of the bean in the factory
     * @return the original bean, or a proxy for a reloadable bean
     * @throws BeansException if Spring bean processing fails
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // check: factory matches this bin
        if (beanFactory == null || !beanFactory.containsBeanDefinition(beanName)) {
            return bean;
        }
        // check: only one instance of ReloadableResourceManager in the context
        String[] beanNames = beanFactory.getBeanNamesForType(ReloadableResourceManager.class);
        if (beanNames.length > 1) {
            throw new IllegalStateException("There should be only one instance of ReloadableResourceManager in the context, but found " + beanNames.length + ": " + Arrays.asList(beanNames));
        }
        // check: have not processed this bin before to avoid recursive BPP
        if (beans.containsKey(beanName)) {
            return bean;
        }
        // check: bean class is annotated by @ReloadableBean
        Class<?> beanClass = bean.getClass();
        if (!AnnotatedElementUtils.hasAnnotation(beanClass, ReloadableBean.class)) {
            return bean;
        }
        // check: resourceId exists in the manager
        String resourceId = AnnotationUtils.findAnnotation(beanClass, ReloadableBean.class).resourceId();

        if (!reloadableResourceConfigurations.containsKey(resourceId)) {
            throw new ReloadableException("Not found resourceId=" + resourceId + " for bean=" + beanName + " class=" + beanClass.getName());
        }
        // looking for annotated by @ReloadableValue fields and methods
        List<Field> annotatedFields =  findAnnotatedFields(beanClass, ReloadableValue.class);
        List<Method> annotatedMethods = findAnnotatedMethods(beanClass, ReloadableValue.class);
        // check: there is only one annotated element: a field or a method
        if (annotatedFields.size() + annotatedMethods.size() == 0) {
            throw new ReloadableException("In bean=" + beanName + " class=" + beanClass.getName() + " there is not field or method annotated by @ReloadableValue");
        } else if (annotatedFields.size() + annotatedMethods.size() > 1) {
            String message = "In bean=" + beanName + " class=" + beanClass.getName() + " there are more then one @ReloadableValue:";
            if (!annotatedFields.isEmpty()) {
                message += " annotatedFields=" + annotatedFields;
            }
            if (!annotatedMethods.isEmpty()) {
                message += " annotatedMethods=" + annotatedMethods;
            }
            throw new ReloadableException(message);
        }
        // check: if disabled return without processing @ReloadableValue
        if(reloadableResourceConfigurations.get(resourceId).isDisabled()) {
            return bean;
        }
        // create bean proxy for hot reloading
        HotSwappableTargetSource targetSource = new HotSwappableTargetSource(bean);
        beans.put(beanName, bean);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource(targetSource);
        proxyFactory.setProxyTargetClass(true);
        Object proxy = proxyFactory.getProxy();
        // setting the first value for @ReloadableValue
        ManagedReloadableResource<?> reloadableResource = reloadableResources.get(resourceId);
        Object firstValue = null;
        // wait asynchronous preloading done, except for the resources in LasyMode, for them, the first set value will be null
        if (preloaded.containsKey(resourceId)) {
            try {
                preloaded.get(resourceId).get();
                firstValue = reloadableResource.getDataValue();
            } catch (Exception e) {
                throw new ReloadableException(e);
            }
        }
        if (!annotatedFields.isEmpty()) {
            Field field = annotatedFields.get(0);
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, bean, firstValue);
        } else {
            Method method = annotatedMethods.get(0);
            ReflectionUtils.makeAccessible(method);
            ReflectionUtils.invokeMethod(method, bean, firstValue);
        }

        // add reloadable listener to create new bean instance with new ReloadableValue
        reloadableResource.getListeners().add(new SetNewDataValueTo<>(newValue -> {
            // initialize new bean instance
            Object newBean = beanFactory.createBean(beanClass);
            // set new ReloadableValue
            if (!annotatedFields.isEmpty()) {
                Field field = annotatedFields.get(0);
                ReflectionUtils.setField(field, newBean, reloadableResource.getDataValue());
            } else {
                Method method = annotatedMethods.get(0);
                ReflectionUtils.invokeMethod(method, newBean, reloadableResource.getDataValue());
            }
            // swap
            Object oldBean =  targetSource.getTarget();
            targetSource.swap(newBean);
            beans.put(beanName, newBean);
            // destroy old bean instance
            beanFactory.destroyBean(oldBean);
        }));
        // start scheduled reloads
        reloadableResource.startScheduling(0);
        return proxy;
    }


    /**
     * Stops managed resources and destroys the manager's current bean targets.
     */
    @Override
    public void destroy() {
        this.reloadableResources.forEach((id, resource) -> {
            resource.shutdown();
        });
        this.beans.forEach((beanName, bean) -> {
            if (beanFactory != null && beanFactory.containsBeanDefinition(beanName)) {
                beanFactory.destroyBean(bean);
            }
        });
    }


    /**
     * Finds fields carrying the specified annotation, including inherited
     * fields.
     *
     * @param targetClass class to inspect
     * @param annotationClass annotation to find
     * @param <A> annotation type
     * @return matching fields
     */
    protected static <A extends Annotation> List<Field> findAnnotatedFields(Class<?> targetClass, Class<A> annotationClass) {
        List<Field> annotatedFields = new ArrayList<>();
        ReflectionUtils.doWithFields(targetClass, annotatedFields::add, f -> AnnotatedElementUtils.hasAnnotation(f, ReloadableValue.class));
        return annotatedFields;
    }

    /**
     * Finds methods carrying the specified annotation.
     *
     * @param targetClass class to inspect
     * @param annotationClass annotation to find
     * @param <A> annotation type
     * @return matching methods
     */
    protected static <A extends Annotation> List<Method> findAnnotatedMethods(Class<?> targetClass, Class<A> annotationClass) {
        Map<Method, A> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<A>) method -> AnnotatedElementUtils.findMergedAnnotation(method, annotationClass)
        );
        return annotatedMethods.keySet().stream().toList();
    }

}
