import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.lang.reflect.Method;

public class benchmark {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        List<String> classNames = new ArrayList<String>();
        ZipInputStream zip = new ZipInputStream(new FileInputStream("/home/saulo/programs/CryptoAPI-Bench/build/libs/rigorityj-samples-1.0-SNAPSHOT.jar"));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {//Filter to get only the .class
                String className = entry.getName().replace('/', '.'); // including ".class"
                Class<?> currentClass = Class.forName(className);
                Method[] methods = currentClass.getMethods();

                if(Arrays.asList(methods).contains("main")){
                    System.out.println("Classe: " + className + " Contém main: Sim");
                    try {
                        Method method = currentClass.getMethod("main", String.class);
                        Object[] parametro = new Object[1];
                        parametro[0] = "";
                        Object resultado = method.invoke(currentClass.newInstance(), parametro);

                        System.out.println("Chamada a main: " + resultado);

                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Classe: " + className + " Contém main: Não");
                }

                classNames.add(className.substring(0, className.length() - ".class".length()));
            }
        }
        System.out.println(classNames);

    }
}
