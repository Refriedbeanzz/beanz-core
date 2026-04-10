import java.lang.reflect.*;
import java.net.*;
import java.io.*;
public class InspectInteractionRelatedComponents {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      String[] names = {
        "com.hypixel.hytale.server.core.entity.InteractionManager",
        "com.hypixel.hytale.server.core.entity.entities.player.data.PlayerData",
        "com.hypixel.hytale.server.core.entity.entities.player.data.PlayerInputData",
        "com.hypixel.hytale.server.core.modules.entity.player.PlayerInput",
        "com.hypixel.hytale.server.core.entity.entities.Player"
      };
      for (String name : names) {
        try {
          Class<?> c = Class.forName(name, false, cl);
          System.out.println("CLASS " + c.getName());
          for (Field f : c.getDeclaredFields()) {
            String s = f.toString();
            if (s.contains("Interaction") || s.contains("Ability") || s.contains("Root") || s.contains("Map")) System.out.println("FIELD " + s);
          }
          for (Method m : c.getDeclaredMethods()) {
            String s = m.toString();
            if (s.contains("Interaction") || s.contains("Ability") || s.contains("Root")) System.out.println("METHOD " + s);
          }
        } catch (Throwable t) {
          System.out.println("MISS " + name);
        }
      }
    }
  }
}
