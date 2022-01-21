import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class benchmark {
    private static List<String[]> dataLines = new ArrayList<>();

    public String handleSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::handleSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public void givenDataArray_whenConvertToCSV_thenOutputCreated() throws IOException {
        File csvOutputFile = new File("results.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
        assertTrue(csvOutputFile.exists());
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        dataLines.add(new String[] { "Classe", "Possui main?", "Resultado de chamada a main"});
        Collection<Class<?>> classes = new ArrayList<Class<?>>();

        JarFile jar = new JarFile("/home/saulo/programs/CryptoAPI-Bench/build/libs/rigorityj-samples-1.0-SNAPSHOT.jar");
        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements() ;) {
            JarEntry entry = entries.nextElement();
            String file = entry.getName();
            if (file.endsWith(".class")) {
                String classname = file.replace('/', '.').substring(0, file.length() - 6);
                try {
                    Class<?> c = Class.forName(classname);
                    classes.add(c);
                }
                catch (Throwable e) {
                    System.out.println("WARNING: failed to instantiate " + classname + " from " + file);
                }
            }
        }

        for (Class<?> c : classes) {
            Class<?> currentClass = Class.forName(c.getName());
            Method[] methods = currentClass.getMethods();

            int flagMain = 0;
            for(Method method : methods){
                if (method.getName().equals("main")){
                    flagMain++;
                    System.out.println("Classe: " + c.getName() + "\tContém main: Sim");
                    //dataLines.add(new String[] { c.getName(), "Sim", "-"});
                    try {
                        Method currentMethod = currentClass.getMethod("main", String[].class);
                        String[] parameter = null;
                        System.out.println("Chamada a main:");
                        dataLines.add(new String[] { c.getName(), "Sim", (String) currentMethod.invoke(null, (Object) parameter)});
                        //currentMethod.invoke(null, (Object) parameter);

                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        dataLines.add(new String[] { c.getName(), "Sim", e.toString()});
                        e.printStackTrace();
                    }
                }
            }
            if(flagMain == 0){
                System.out.println("Classe: " + c.getName() + "\tContém main: Não");
                dataLines.add(new String[] { c.getName(), "Nao", "-"});
            }
        }
        System.out.println("\n");
        new benchmark().givenDataArray_whenConvertToCSV_thenOutputCreated();
    }
}
