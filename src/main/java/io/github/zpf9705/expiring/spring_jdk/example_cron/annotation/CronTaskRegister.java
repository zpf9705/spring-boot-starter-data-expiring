package io.github.zpf9705.expiring.spring_jdk.example_cron.annotation;

import io.github.zpf9705.expiring.core.OperationsException;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.spring_jdk.example_cron.CronWithBeanCallRegister;
import io.github.zpf9705.expiring.spring_jdk.example_cron.CronWithInstanceCallRegister;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Timer task initiation processing registration class, which can rely on {@link ImportSelector} to
 * complete registration in the form of beans.
 * <p>
 * Trigger based on timed task switch annotation {@link EnableCronTaskRegister}, scan the package
 * path provided by it, find the corresponding method with {@link Cron} annotation, and determine
 * whether the calling object uses spring proxy or each instance invocation method through {@link Mode} type
 *
 * @author zpf
 * @since 3.1.5
 */
public class CronTaskRegister implements ImportSelector {

    @Override
    @NotNull
    public String[] selectImports(@NotNull AnnotationMetadata metadata) {
        //get Attributes for EnableCronTaskRegister
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableCronTaskRegister.class.getName()));
        if (attributes == null) {
            throw new OperationsException("No found named" + EnableCronTaskRegister.class.getName() + "annotation " +
                    "open in your project");
        }
        scanPackage = attributes.getStringArray("basePackages");
        //get call method with proxy obj or new obj
        Mode mode = attributes.getEnum("mode");
        if (mode == Mode.PROXY) {
            return new String[]{CronWithBeanCallRegister.class.getName()};
        } else if (mode == Mode.INSTANCE) {
            return new String[]{CronWithInstanceCallRegister.class.getName()};
        }
        return new String[0];
    }

    private static String[] scanPackage;

    public static String[] getScanPackage() {
        return scanPackage;
    }
}
