package io.github.zpf9705.expiring.banner;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * Access to source expire version information
 *
 * @author zpf
 * @since 2.2.2
 */
public final class Version {

    public static final Attributes.Name BUNDLE_VERSION = new Attributes.Name("Bundle-Version");

    private Version() {
    }

    public static String getVersion(Class<?> sourceClass) {
        if (sourceClass == null) return "UNKNOWN";
        return determineExpireVersion(sourceClass);
    }

    private static String determineExpireVersion(Class<?> sourceClass) {
        String implementationVersion = sourceClass.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        }
        CodeSource codeSource = sourceClass.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return null;
        }
        URL codeSourceLocation = codeSource.getLocation();
        try {
            URLConnection connection = codeSourceLocation.openConnection();
            if (connection instanceof JarURLConnection) {
                return getImplementationVersion(((JarURLConnection) connection).getJarFile());
            }
            try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
                return getImplementationVersion(jarFile);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        Attributes attributes = jarFile.getManifest().getMainAttributes();
        String version = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        if (StringUtils.isBlank(version)) {
            version = attributes.getValue(BUNDLE_VERSION);
        }
        return version;
    }
}
