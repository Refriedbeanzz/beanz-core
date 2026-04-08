public class RunInspect9 {
  public static void main(String[] args) throws Exception {
    InspectRuntimeJump.main(new String[]{
      "org.narwhals.plugin.doublejump.DoubleJumpInteraction",
      "org.narwhals.plugin.walljump.WallJumpInteraction",
      "org.narwhals.plugin.doublejump.DoubleJumpComponent",
      "org.narwhals.plugin.walljump.WallJumpComponent"
    });
  }
}
