import java.lang.reflect.*;
import java.net.*;
import java.io.*;
public class InspectInteractionApi {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      for (String name : args) {
        System.out.println("CLASS " + name);
        Class<?> c = Class.forName(name, false, cl);
        System.out.println("  SUPER " + c.getSuperclass());
        for (Constructor<?> k : c.getDeclaredConstructors()) System.out.println("  CTOR " + k);
        for (Method m : c.getDeclaredMethods()) System.out.println("  METHOD " + m);
        for (Field f : c.getDeclaredFields()) System.out.println("  FIELD " + f);
      }
    }
  }
}
