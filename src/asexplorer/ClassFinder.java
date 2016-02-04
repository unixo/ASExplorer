package asexplorer;

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
public class ClassFinder
{

    public static Class[] getClassesInPackage(String packageName)
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // Format package name with slashes instead of dots
        String path = packageName.replace('.', '/');

        ArrayList<Class> classes = new ArrayList<>();

        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<>();

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }

            for (File directory : dirs) {
                if (directory.getPath().contains(".jar!")) {
                    String jarPath = directory.getPath().substring(5);
                    String[] parts = jarPath.split("!");

                    classes.addAll(getClasseNamesInPackage(parts[0], packageName));
                } else {
                    classes.addAll(getClasseNamesInDirectory(directory, packageName));
                }
            }
        } catch (Exception ex) {
        }

        return classes.toArray(new Class[classes.size()]);
    }

    private static List<Class> getClasseNamesInDirectory(File directory, String packageName) throws ClassNotFoundException, IOException
    {
        List<Class> classes = new ArrayList<>();
        if (directory.exists() == false) {
            return classes;
        }

        File[] files = directory.listFiles();

        for (File file : files) {
            System.out.println("File: " + file.getName());

            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(getClasseNamesInDirectory(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                Class c = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                classes.add(c);
            }
        }

        return classes;
    }

    private static List<Class> getClasseNamesInPackage(String jarName, String packageName)
    {
        List<Class> classes = new ArrayList<>();

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
                    classes.add(c);
                }
            }
        } catch (Exception e) {
        }

        return classes;
    }
}
