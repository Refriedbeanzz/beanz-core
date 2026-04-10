import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
public class FindInteractionSubclass {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()}); JarFile jf = new JarFile(jar)) {
      Class<?> base = Class.forName("com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction", false, cl);
      int count=0;
      Enumeration<JarEntry> en = jf.entries();
      while (en.hasMoreElements() && count < 20) {
        JarEntry e = en.nextElement();
        if (!e.getName().endsWith(".class") || e.getName().contains("$") ) continue;
        String name = e.getName().replace('/', '.').replaceAll("\\.class$", "");
        try {
          Class<?> c = Class.forName(name, false, cl);
          if (c != base && base.isAssignableFrom(c)) {
            System.out.println(name);
            count++;
          }
        } catch (Throwable t) {
        }
      }
    }
  }
}
