import java.io.*;
import java.util.jar.*;
public class FindAbilityClasses {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (JarFile jf = new JarFile(jar)) {
      jf.stream().map(JarEntry::getName)
        .filter(n -> n.toLowerCase().contains("ability") || n.toLowerCase().contains("interaction") && n.toLowerCase().contains("component") || n.toLowerCase().contains("rootinteraction"))
        .sorted().forEach(System.out::println);
    }
  }
}
