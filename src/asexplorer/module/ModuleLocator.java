package asexplorer.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 *
 * @author unixo
 */
public class ModuleLocator
{

    public static Class<ModuleInterface>[] getModules()
    {
        Class[] modules;

        try {
            modules = ModuleLocator.getClasses("asexplorer.module");
        } catch (Exception ex) {
            return null;
        }

        return modules;
    }

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            if (directory.getPath().contains(".jar!")) {
                String jarPath = directory.getPath().substring(5);
                String[] parts = jarPath.split("!");

                classes.addAll(getClasseNamesInPackage(parts[0], packageName));
            } else {
                classes.addAll(findClasses(directory, packageName));
            }
        }

        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     *
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base
     * directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException, IOException
    {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();

        for (File file : files) {
            System.out.println("File: " + file.getName());

            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                Class c = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                System.out.println("Class: " + c.getName());

                //if (c.isAssignableFrom(ModuleInterface.class)) {
                if (ModuleLocator.isValidClass(c)) {
                    classes.add(c);
                }
            }
        }

        return classes;
    }

    private static boolean isValidClass(Class aClass)
    {
        Class[] interfaces = aClass.getInterfaces();

        for (Class c : interfaces) {
            if (c.equals(ModuleInterface.class)) {
                return true;
            }
        }

        return false;
    }

    public static List<Class> getClasseNamesInPackage(String jarName, String packageName)
    {
        List<Class> classes = new ArrayList<Class>();

        packageName = packageName.replaceAll("\\.", "/");

        try {
            JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
            JarEntry jarEntry;

            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                if ((jarEntry.getName().startsWith(packageName)) && (jarEntry.getName().endsWith(".class"))) {
                    Class c = Class.forName(jarEntry.getName().replaceAll("/", "\\.").replaceAll(".class", ""));

                    if (isValidClass(c)) {
                        classes.add(c);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
}
