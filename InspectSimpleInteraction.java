import java.lang.reflect.*;
import java.net.*;
import java.io.*;
public class InspectSimpleInteraction {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      Class<?> c = Class.forName("com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction", false, cl);
      System.out.println("CLASS " + c.getName());
      System.out.println("SUPER " + c.getSuperclass());
      for (Constructor<?> k : c.getDeclaredConstructors()) System.out.println("CTOR " + k);
      for (Method m : c.getDeclaredMethods()) System.out.println("METHOD " + m);
      for (Field f : c.getDeclaredFields()) System.out.println("FIELD " + f);
    }
  }
}
