import java.io.*;
import java.net.*;
import java.lang.reflect.*;
public class InspectEventBusApi2 {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      Class<?> c = Class.forName("com.hypixel.hytale.event.EventBus", false, cl);
      System.out.println("CLASS " + c.getName());
      for (Method m : c.getDeclaredMethods()) System.out.println("METHOD " + m);
    }
  }
}
