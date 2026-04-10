import java.io.*;
import java.net.*;
import java.lang.reflect.*;
public class InspectHytaleServerApi2 {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      Class<?> c = Class.forName("com.hypixel.hytale.server.core.HytaleServer", false, cl);
      System.out.println("CLASS " + c.getName());
      for (Method m : c.getDeclaredMethods()) {
        String s=m.toString();
        if (s.contains("Event") || s.contains("get()")) System.out.println("METHOD " + s);
      }
    }
  }
}
