import java.lang.reflect.*;
import java.net.*;
import java.io.*;
public class InspectAssetRegistryGetters {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      Class<?> c = Class.forName("com.hypixel.hytale.server.core.plugin.registry.AssetRegistry", false, cl);
      for (Method m : c.getDeclaredMethods()) {
        String s = m.toString();
        if (s.contains("Interaction") || s.contains("Unarmed") || s.contains("Root") || s.contains("AssetMap")) System.out.println(s);
      }
    }
  }
}
