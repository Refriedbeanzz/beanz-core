import java.io.*;
import java.net.*;
import java.lang.reflect.*;
public class InspectPacketHandlerPlayerRef {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      Class<?> c = Class.forName("com.hypixel.hytale.server.core.io.PacketHandler", false, cl);
      System.out.println("CLASS " + c.getName());
      for (Method m : c.getDeclaredMethods()) {
        String s = m.toString();
        if (s.contains("PlayerRef") || s.contains("World") || s.contains("getPlayer") || s.contains("Ref")) System.out.println("METHOD " + s);
      }
      for (Field f : c.getDeclaredFields()) {
        String s = f.toString();
        if (s.contains("PlayerRef") || s.contains("player") || s.contains("World")) System.out.println("FIELD " + s);
      }
    }
  }
}
