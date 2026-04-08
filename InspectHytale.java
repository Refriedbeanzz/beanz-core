import java.lang.reflect.*;
import java.net.*;
import java.io.*;

public class InspectHytale {
  public static void main(String[] args) throws Exception {
    URL jar = new File("C:/Users/jonathan/AppData/Roaming/Hytale/install/release/package/game/latest/Server/HytaleServer.jar").toURI().toURL();
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar})) {
      for (String name : args) {
        Class<?> c = Class.forName(name, false, cl);
        System.out.println("CLASS " + name);
        for (Method m : c.getDeclaredMethods()) {
          System.out.println("M " + Modifier.toString(m.getModifiers()) + " " + m.getReturnType().getTypeName() + " " + m.getName() + java.util.Arrays.toString(m.getParameterTypes()));
        }
        for (Field f : c.getDeclaredFields()) {
          System.out.println("F " + Modifier.toString(f.getModifiers()) + " " + f.getType().getTypeName() + " " + f.getName());
        }
      }
    }
  }
}
