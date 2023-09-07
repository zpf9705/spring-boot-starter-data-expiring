package io.github.zpf9705.expiring.spring_jdk.example_cron.annotation;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.spring_jdk.example_cron.CronWithBeanCallRegister;
import io.github.zpf9705.expiring.spring_jdk.example_cron.CronWithInstanceCallRegister;
import io.github.zpf9705.expiring.spring_jdk.support.SupportException;
import io.github.zpf9705.expiring.util.ArrayUtils;
import io.github.zpf9705.expiring.util.ScanUtils;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Timer task initiation processing registration class, which can rely on {@link ImportSelector} to
 * complete registration in the form of beans.
 * <p>
 * Trigger based on timed task switch annotation {@link EnableCronTaskRegister}, scan the package
 * path provided by it, find the corresponding method with {@link Cron} annotation, and determine
 * whether the calling object uses spring proxy or each instance invocation method through {@link Type} type
 *
 * @author zpf
 * @since 3.1.5
 */
public class CronTaskRegister implements DeferredImportSelector {

    @Override
    @NotNull
    public String[] selectImports(@NotNull AnnotationMetadata metadata) {
        //get Attributes for EnableCronTaskRegister
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableCronTaskRegister.class.getName()));
        if (attributes == null) {
            throw new SupportException("Analysis named" + EnableCronTaskRegister.class.getName() + "annotation " +
                    "to AnnotationAttributes failed");
        }
        scanPackage = attributes.getStringArray("basePackages");
        if (ArrayUtils.simpleIsEmpty(scanPackage)) {
            scanPackage = ScanUtils.findSpringApplicationPackageName();
        }
        noMethodDefaultStart = attributes.getBoolean("noMethodDefaultStart");
        Type type = attributes.getEnum("type");
        //Load different configuration classes based on the survival method of object calls
        if (type == Type.PROXY) {
            return new String[]{CronWithBeanCallRegister.class.getName()};
        } else if (type == Type.INSTANCE) {
            return new String[]{CronWithInstanceCallRegister.class.getName()};
        }
        return new String[0];
    }

    private static String[] scanPackage;

    private static boolean noMethodDefaultStart;

    public static String[] getScanPackage() {
        return scanPackage;
    }

    public static boolean isNoMethodDefaultStart() {
        return noMethodDefaultStart;
    }
}
