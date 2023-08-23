package io.github.zpf9705.expiring.util;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Utilize {@link Reflections} to modify Java properties such as classes, fields, and methods for certain features
 *
 * @author zpf
 * @since 3.1.5
 */
public class ReflectionUtils {

    public static String findSpringApplicationPackageName() {
        Class<?> startupClazz;
        //How to obtain the package path where the startup class is located without providing a scanning path
        Set<Class<?>> startupTypes = new Reflections(ConfigurationBuilder.build()
                .addScanners(Scanners.TypesAnnotated))
                .getTypesAnnotatedWith(SpringBootApplication.class);
        if (CollectionUtils.simpleIsEmpty(startupTypes)) {
            //No proof found, no startup project added
            throw new UtilsException("No Spring startup class found");
        } else {
            //Adding multiple startup class annotations is also not feasible and needs to be checked
            if (startupTypes.size() > 1) {
                throw new UtilsException("Multiple Spring startup class annotations found, please check the project");
            } else {
                //Extract the first one here as the startup package
                startupClazz = new ArrayList<>(startupTypes).get(0);
            }
        }
        return startupClazz.getPackage().getName();
    }

    public static List<Method> findPackageMethodsWithAnnotation(String packageName, Class<? extends Annotation> clazz) {
        if (StringUtils.simpleIsBlank(packageName) || clazz == null) {
            return Collections.emptyList();
        }
        //Add a method scanner to scan the specified annotation method
        Set<Method> method0s = new Reflections(packageName,
                Scanners.MethodsAnnotated).getMethodsAnnotatedWith(clazz);
        if (CollectionUtils.simpleIsEmpty(method0s)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(method0s);
    }
}
