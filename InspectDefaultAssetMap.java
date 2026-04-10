import java.lang.reflect.*;
import java.net.*;
import java.io.*;
public class InspectDefaultAssetMap {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      Class<?> c = Class.forName("com.hypixel.hytale.assetstore.map.DefaultAssetMap", false, cl);
      System.out.println("CLASS " + c.getName());
      for (Field f : c.getDeclaredFields()) System.out.println("FIELD " + f);
      for (Method m : c.getDeclaredMethods()) System.out.println("METHOD " + m);
    }
  }
}
