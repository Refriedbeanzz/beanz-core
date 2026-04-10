import java.lang.reflect.*;
import java.net.*;
import java.io.*;
public class InspectBuilderCodecBuilderApi {
  public static void main(String[] args) throws Exception {
    File jar = new File(System.getenv("APPDATA") + "\\Hytale\\install\\release\\package\\game\\latest\\Server\\HytaleServer.jar");
    try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()})) {
      Class<?> c = Class.forName("com.hypixel.hytale.codec.builder.BuilderCodec$Builder", false, cl);
      for (Method m : c.getDeclaredMethods()) System.out.println(m);
    }
  }
}
