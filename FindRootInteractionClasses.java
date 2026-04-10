import java.io.*;
import java.util.jar.*;
public class FindRootInteractionClasses {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (JarFile jf = new JarFile(jar)) {
      jf.stream().map(JarEntry::getName)
        .filter(n -> n.contains("RootInteraction") || n.contains("InteractionRoot") || n.contains("interaction") && n.toLowerCase().contains("root"))
        .sorted().forEach(System.out::println);
    }
  }
}
