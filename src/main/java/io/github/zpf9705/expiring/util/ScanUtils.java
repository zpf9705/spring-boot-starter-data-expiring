package io.github.zpf9705.expiring.util;

import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.logger.Console;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Regarding un packaged and packaged scans of
 * <ul>
 *     <li>inheritance class objects{@link #getSubTypesOf(Class, String...)}</li>
 *     <li>annotation class objects{@link #getTypesAnnotatedWith(Class, String...)}</li>
 *     <li>method annotation objects{@link #getMethodsAnnotatedWith(Class, String...)}</li>
 * </ul>
 * etc., extensions can be made according to {@link #scan(String, Predicate)}
 *
 * @author zpf
 * @since 3.1.5
 */
public abstract class ScanUtils {

    private static String[] defaultPackage;

    public static String[] findSpringApplicationPackageName() {
        return defaultPackage;
    }

    public static void applicationSource(@NotNull Set<Object> source) {
        if (source.isEmpty()) {
            throw new UtilsException("No detection of the existence of the startup main class");
        }
        defaultPackage = source.stream().map(o -> {
            if (o instanceof Class<?>) {
                return ((Class<?>) o).getName().split(
                        "\\." + ((Class<?>) o).getSimpleName())[0];
            }
            return null;
        }).filter(Objects::nonNull).toArray(String[]::new);
    }

    public static <T> Set<Class<T>> getSubTypesOf(Class<T> clazz, String... packageNames) {
        Objects.requireNonNull(clazz, "clazz no be null");
        Objects.requireNonNull(packageNames, "scan packageNames no be null");
        Set<Class<T>> finder = new HashSet<>();
        for (String packageName : packageNames) {
            Set<Class<T>> scan = scan(packageName, compareClazz -> {
                //no annotation
                return !clazz.isAnnotation() &&
                        //not array
                        !clazz.isArray() &&
                        //not Enum
                        !clazz.isEnum() &&
                        //The former is a, while the latter is b
                        //----------------------------------------------------------------------------------
                        //The class information corresponding to object a is the parent class or interface of
                        // the class information corresponding to object b. Simply understood, a is the parent
                        // class or interface of b
                        //----------------------------------------------------------------------------------
                        //The class information corresponding to object a is the same as the class information
                        // corresponding to object b. Simply understood, a and b are the same class or interface
                        clazz.isAssignableFrom(compareClazz);
            });
            if (CollectionUtils.simpleNotEmpty(scan)) {
                finder.addAll(scan);
            }
        }
        return finder;
    }

    public static <T> Set<Class<T>> getTypesAnnotatedWith(Class<? extends Annotation> clazz,
                                                          String... packageNames) {
        Objects.requireNonNull(clazz, "clazz no be null");
        Objects.requireNonNull(packageNames, "scan packageNames no be null");
        Set<Class<T>> finder = new HashSet<>();
        for (String packageName : packageNames) {
            Set<Class<T>> scan = scan(packageName, finerClazz -> {
                //This annotation is standard on the class or interface
                return finerClazz.getAnnotation(clazz) != null;
            });
            if (CollectionUtils.simpleNotEmpty(scan)) {
                finder.addAll(scan);
            }
        }
        return finder;
    }

    public static <T> Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> clazz,
                                                          String... packageNames) {
        Objects.requireNonNull(clazz, "clazz no be null");
        Objects.requireNonNull(packageNames, "scan packageNames no be null");
        Set<Method> finder = new HashSet<>();
        for (String packageName : packageNames) {
            Set<Class<T>> scan = scan(packageName, null);
            if (CollectionUtils.simpleNotEmpty(scan)) {
                //First obtain the class object,
                // and then obtain the corresponding annotation method from the class object
                Set<Method> mes = scan.stream().map(cla -> {
                            Method[] methods = cla.getMethods();
                            if (ArrayUtils.simpleIsEmpty(methods)) {
                                return null;
                            }
                            return Arrays.stream(methods)
                                    .filter(me -> me.getAnnotation(clazz) != null).collect(Collectors.toSet());
                        }).filter(Objects::nonNull).flatMap(Collection::stream)
                        .collect(Collectors.toSet());
                finder.addAll(mes);
            }
        }
        return finder;
    }

    @SuppressWarnings("all")
    public static <T> Set<Class<T>> scan(String packageName, Predicate<Class<T>> filter) {
        // The set of the first class class
        Set<Class<T>> classes = new LinkedHashSet<>();
        // Whether to iterate in a loop
        boolean recursive = true;
        // Obtain the name of the package and replace it
        String packageDirName = packageName.replace('.', '/');
        // Define a collection of enumerations and loop through to handle things in this directory
        Enumeration<URL> dirs;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = ScanUtils.class.getClassLoader();
            }
            dirs = classLoader.getResources(packageDirName);
            // Loop and iterate on
            while (dirs.hasMoreElements()) {
                // Get Next Element
                URL url = dirs.nextElement();
                // Obtain the name of the agreement
                String protocol = url.getProtocol();
                // If saved as a file on the server
                if ("file".equals(protocol)) {
                    // Obtain the physical path of the package
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // Scan the files under the entire package as files and add them to the collection
                    scanByFile(packageName, filePath, recursive, classes, classLoader, filter);
                } else if ("jar".equals(protocol)) {
                    // If it is a jar package file
                    //Define a JarFile
                    JarFile jar;
                    try {
                        // Get jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // Obtain an enumeration class from this jar package
                        Enumeration<JarEntry> entries = jar.entries();
                        // Repeat the loop iteration process
                        while (entries.hasMoreElements()) {
                            // Obtaining an entity in a jar can be a directory and other
                            // files in jar packages such as META INF
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // If it starts with/
                            if (name.charAt(0) == '/') {
                                // Get the following string
                                name = name.substring(1);
                            }
                            // If the first half is the same as the defined package name
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // If ending with '/' is a package
                                if (idx != -1) {
                                    // Obtain the package name and replace '/' with '.'
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // If it can iterate and be a package
                                if ((idx != -1) || recursive) {
                                    // If it is a. class file and not a directory
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // Remove the following '. class' to obtain the true class name
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        String name0 = packageName + '.' + className;
                                        try {
                                            // Add to classes, but pass filter testing
                                            // Add directly without a filter to enter
                                            Class<T> clazz = (Class<T>) Class.forName(name0);
                                            if (filter != null) {
                                                if (filter.test(clazz)) {
                                                    classes.add(clazz);
                                                }
                                            } else {
                                                classes.add(clazz);
                                            }
                                        } catch (ClassNotFoundException e) {
                                            throw new UtilsException("Error adding user defined view class. " +
                                                    "class file not found for named " + name0 + " class");
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new UtilsException("Error retrieving files from jar package " +
                                "while scanning user-defined views : " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new UtilsException("Error retrieving files from jar package " +
                    "while scanning user-defined views : " + e.getMessage());
        }
        return classes;
    }

    private static <T> void scanByFile(String packageName, String packagePath, final boolean recursive,
                                       Set<Class<T>> classes, ClassLoader classLoader,
                                       Predicate<Class<T>> filter) {
        // Obtain the directory of this package and create a file
        File dir = new File(packagePath);
        // If it does not exist or is not a directory, return it directly
        if (!dir.exists() || !dir.isDirectory()) {
            Console.warn("User defined package name {} there are no files available below", packageName);
            return;
        }
        // If it exists, obtain all files under the package, including directories
        // If the custom filtering rules can be looped (including subdirectories)
        // or files ending in. class (compiled Java class files)
        File[] dirFiles = dir.listFiles(file -> (recursive && file.isDirectory())
                || (file.getName().endsWith(".class")));
        if (ArrayUtils.simpleIsEmpty(dirFiles)) {
            return;
        }
        // Loop All Files
        for (File file : dirFiles) {
            // If it is a directory, continue scanning
            if (file.isDirectory()) {
                scanByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes,
                        classLoader, filter);
            } else {
                // If it is a Java class file, remove the following. class and leave only the class name
                String className = file.getName().substring(0, file.getName().length() - 6);
                String name = packageName + '.' + className;
                try {
                    //There are some drawbacks to using forName here, as it triggers the
                    // static method and does not clean up the load using classLoader
                    @SuppressWarnings("unchecked")
                    Class<T> clazz = (Class<T>) classLoader.loadClass(name);
                    if (filter != null) {
                        if (filter.test(clazz)) {
                            classes.add(clazz);
                        }
                    } else {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    throw new UtilsException("Error adding user defined view class. " +
                            "class file not found for named " + name + " class");
                }
            }
        }
    }
}
