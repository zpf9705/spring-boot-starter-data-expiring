package io.github.zpf9705.expiring.spring_jdk.support;

import cn.hutool.core.util.ArrayUtil;
import io.github.zpf9705.expiring.core.Console;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Abstract proxy bean defines Keygen,You can define your own switch annotations, scan representation annotations,
 * implement interface level automatic injection, and automatically create {@link BeanDefinition} implementation classes
 * <ul>
 *     <li>{@code O} Enable proxy environment annotation{@link #getOpenClazz()}</li>
 *     <li>{@code F} Scanning Markup Class Annotations{@link #getFindClazz()}</li>
 * </ul>
 *
 * @author zpf
 * @since 3.1.0
 */
public abstract class AbstractProxyBeanInjectSupport<O extends Annotation, F extends Annotation>
        implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {

    private Environment environment;

    private ResourceLoader resourceLoader;

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(@NotNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata metadata, @NotNull BeanDefinitionRegistry registry,
                                        @NotNull BeanNameGenerator importBeanNameGenerator) {
        this.registerBeanDefinitions(metadata, registry);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, @NotNull BeanDefinitionRegistry registry) {
        //Obtain the annotation value for opening
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(getOpenClazz().getName()));
        if (attributes == null) {
            Console.info("Analysis" + getOpenClazz().getName() + "attribute encapsulation to AnnotationAttributes failed");
            return;
        }
        //Obtain Scan Path
        String[] basePackages = attributes.getStringArray(getPackagesSign());
        if (ArrayUtil.isEmpty(basePackages)) {
            basePackages = new String[]{ReflectionUtils.findSpringApplicationPackageName()};
        }
        //Obtain Path Scan Provider
        ClassPathScanningCandidateComponentProvider classPathScan = this.getClassPathScanUseAnnotationClass();
        //The class annotated by the subcontracting scanning target
        for (String basePackage : basePackages) {
            Set<BeanDefinition> beanDefinitions = classPathScan.findCandidateComponents(basePackage);
            if (CollectionUtils.isEmpty(beanDefinitions)) {
                continue;
            }
            //Perform proxy registration for each bean
            for (BeanDefinition beanDefinition : beanDefinitions) {
                if (!(beanDefinition instanceof AnnotatedBeanDefinition)) {
                    continue;
                }
                AnnotatedBeanDefinition adi = (AnnotatedBeanDefinition) beanDefinition;
                //Obtain annotation class identification interface
                AnnotationMetadata meta = adi.getMetadata();
                if (!meta.isInterface()) {
                    continue;
                }
                //Obtain annotation class identification interface annotation value domain
                AnnotationAttributes attributesFu = AnnotationAttributes
                        .fromMap(meta.getAnnotationAttributes(getFindClazz().getCanonicalName()));
                if (attributesFu == null) {
                    continue;
                }
                //Bean registration
                this.beanRegister(attributesFu, registry, meta);
            }
        }
    }

    /**
     * Register annotation association scanning class
     *
     * @param attributes Annotation Value Range
     * @param registry   Bean Keygen
     * @param meta       Associated annotation interface
     */
    public void beanRegister(AnnotationAttributes attributes, BeanDefinitionRegistry registry, AnnotationMetadata meta) {
    }

    /**
     * Obtain a conditional class scanner based on the provided class object
     *
     * @return {@link ClassPathScanningCandidateComponentProvider}
     */
    @NotNull
    public ClassPathScanningCandidateComponentProvider getClassPathScanUseAnnotationClass() {
        ClassPathScanningCandidateComponentProvider classPathScan =
                new ClassPathScanningCandidateComponentProvider(false, this.environment) {
                    @Override
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        return beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata().isAnnotation();
                    }
                };
        classPathScan.setResourceLoader(this.resourceLoader);
        //scan all class type
        classPathScan.setResourcePattern("**/*.class");
        classPathScan.addIncludeFilter(new AnnotationTypeFilter(getFindClazz()));
        return classPathScan;
    }

    /**
     * Obtain the class object for opening class annotations
     *
     * @return no be {@literal null}
     */
    @NotNull
    public abstract Class<O> getOpenClazz();

    /**
     * Obtain the class object that identifies the class annotation
     *
     * @return no be {@literal null}
     */
    @NotNull
    public abstract Class<F> getFindClazz();

    /**
     * Obtain path scan variable identification, default to {@code value}
     *
     * @return no be {@literal null}
     */
    @NotNull
    public String getPackagesSign() {
        return "value";
    }

    /**
     * Get startup environment variables
     *
     * @return {@link Environment}
     */
    @NotNull
    public Environment getEnvironment() {
        return this.environment;
    }
}
