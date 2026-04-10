import java.lang.reflect.*;
import java.net.*;
import java.io.*;
public class InspectJavaPluginApi {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      Class<?> c = Class.forName("com.hypixel.hytale.server.core.plugin.JavaPlugin", false, cl);
      for (Method m : c.getDeclaredMethods()) {
        if (m.getName().contains("Codec") || m.getName().contains("Registry")) System.out.println(m);
      }
    }
  }
}
