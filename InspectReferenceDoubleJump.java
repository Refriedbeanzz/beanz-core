import java.lang.reflect.*;
import java.net.*;
import java.io.*;
public class InspectReferenceDoubleJump {
  public static void main(String[] args) throws Exception {
    File serverJar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    File modDir = new File("C:\\Dev\\Hytale\\Double Jump-0.1.5");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{modDir.toURI().toURL(), serverJar.toURI().toURL()})) {
      Class<?> c = Class.forName("org.narwhals.plugin.DoubleJumpInteraction", false, cl);
      System.out.println("CLASS " + c);
      System.out.println("SUPER " + c.getSuperclass());
      for (Constructor<?> k : c.getDeclaredConstructors()) System.out.println("CTOR " + k);
      for (Method m : c.getDeclaredMethods()) System.out.println("METHOD " + m);
      for (Field f : c.getDeclaredFields()) System.out.println("FIELD " + f);
    }
  }
}
