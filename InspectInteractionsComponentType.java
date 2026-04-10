import java.lang.reflect.*;
import java.net.*;
import java.io.*;
public class InspectInteractionsComponentType {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      Class<?> c = Class.forName("com.hypixel.hytale.server.core.modules.interaction.Interactions", false, cl);
      for (Method m : c.getDeclaredMethods()) {
        if (m.getName().contains("getComponentType") || m.getName().contains("setInteractionId") || m.getName().contains("getInteractionId")) {
          System.out.println(m);
        }
      }
    }
  }
}
