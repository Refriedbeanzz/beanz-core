import java.io.*;
import java.net.*;
import java.lang.reflect.*;
public class InspectPacketAdapters {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      for (String name : new String[]{
        "com.hypixel.hytale.server.core.io.adapter.PacketAdapters",
        "com.hypixel.hytale.server.core.io.adapter.PlayerPacketWatcher",
        "com.hypixel.hytale.server.core.io.adapter.PacketFilter"
      }) {
        Class<?> c = Class.forName(name, false, cl);
        System.out.println("CLASS " + c.getName());
        for (Method m : c.getDeclaredMethods()) System.out.println("METHOD " + m);
        for (Field f : c.getDeclaredFields()) System.out.println("FIELD " + f);
      }
    }
  }
}
