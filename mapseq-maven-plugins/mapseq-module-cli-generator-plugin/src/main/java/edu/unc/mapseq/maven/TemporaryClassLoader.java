package edu.unc.mapseq.maven;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;

public class TemporaryClassLoader extends ClassLoader {

    public TemporaryClassLoader() {
        super();
    }

    public TemporaryClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println("name = " + name);
        System.out.println(name.indexOf(".class"));
        if (name.indexOf(".class") == -1) {
            return super.loadClass(name);
        }
        try {
            File f = new File(name);
            URLConnection connection = f.toURI().toURL().openConnection();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = input.read();

            while (data != -1) {
                buffer.write(data);
                data = input.read();
            }

            input.close();

            byte[] classData = buffer.toByteArray();

            // edu/unc/mapseq/module/alignment/TrimCountAdapter.class
            int idx = name.indexOf("target/classes/");
            // System.out.println(idx);
            String fullClassName = name.replace(".class", "");
            // System.out.println(fullClassName);
            fullClassName = fullClassName.substring(idx + 15, fullClassName.length());
            // System.out.println(fullClassName);
            fullClassName = fullClassName.replace("/", ".");
            // System.out.println(fullClassName);
            return defineClass(fullClassName, classData, 0, classData.length);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
