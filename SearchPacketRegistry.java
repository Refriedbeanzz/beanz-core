import java.io.*;
import java.net.*;
import java.lang.reflect.*;
public class SearchPacketRegistry {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()}); java.util.jar.JarFile jf = new java.util.jar.JarFile(jar)) {
      var e = jf.entries();
      while (e.hasMoreElements()) {
        var je = e.nextElement();
        if (!je.getName().endsWith(".class") || je.getName().contains("$")) continue;
        String name = je.getName().replace('/', '.').replaceAll("\\.class$", "");
        try {
          Class<?> c = Class.forName(name, false, cl);
          for (Method m : c.getDeclaredMethods()) {
            String s = m.toString();
            if (s.contains("Packet") && (s.contains("Watcher") || s.contains("Watch") || s.contains("register"))) {
              System.out.println(name + " :: " + s);
            }
          }
        } catch (Throwable t) {}
      }
    }
  }
}
