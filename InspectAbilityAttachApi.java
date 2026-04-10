import java.io.*;
import java.net.*;
import java.lang.reflect.*;
public class InspectAbilityAttachApi {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      for (String name : new String[]{
        "com.hypixel.hytale.server.core.entity.entities.Player",
        "com.hypixel.hytale.server.core.entity.LivingEntity",
        "com.hypixel.hytale.server.core.entity.InteractionManager"
      }) {
        Class<?> c = Class.forName(name, false, cl);
        System.out.println("CLASS " + c.getName());
        for (Method m : c.getDeclaredMethods()) {
          String s = m.toString();
          if (s.contains("Ability") || s.contains("Interaction") || s.contains("Root") || s.contains("input") || s.contains("Use")) System.out.println("METHOD " + s);
        }
        for (Field f : c.getDeclaredFields()) {
          String s = f.toString();
          if (s.contains("Ability") || s.contains("Interaction") || s.contains("Root")) System.out.println("FIELD " + s);
        }
      }
    }
  }
}
