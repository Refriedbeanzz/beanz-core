import java.io.*;
import java.net.*;
import java.lang.reflect.*;
public class InspectPacketApis {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()}); java.util.jar.JarFile jf = new java.util.jar.JarFile(jar)) {
      String[] names = {
        "com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain",
        "com.hypixel.hytale.protocol.InteractionType",
        "com.hypixel.hytale.server.core.networking.PacketWatcher",
        "com.hypixel.hytale.server.core.networking.PacketWatchers",
        "com.hypixel.hytale.server.core.network.PacketWatcher",
        "com.hypixel.hytale.server.core.network.PacketWatchers",
        "com.hypixel.hytale.server.core.packet.PacketWatcher"
      };
      for (String name : names) {
        try {
          Class<?> c = Class.forName(name, false, cl);
          System.out.println("CLASS " + c.getName());
          for (Field f : c.getDeclaredFields()) System.out.println("FIELD " + f);
          for (Method m : c.getDeclaredMethods()) System.out.println("METHOD " + m);
        } catch (Throwable t) {
          System.out.println("MISS " + name);
        }
      }
      System.out.println("SEARCH:");
      jf.stream().map(java.util.jar.JarEntry::getName)
        .filter(n -> n.toLowerCase().contains("packetwatch") || n.toLowerCase().contains("syncinteraction") || n.toLowerCase().contains("interactionchain"))
        .sorted().forEach(System.out::println);
    }
  }
}
