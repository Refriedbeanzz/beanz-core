public class TmpInspect {
  public static void main(String[] args) throws Exception {
    InspectRuntimeJump.main(new String[]{
      "com.hypixel.hytale.server.core.modules.entity.player.PlayerInput",
      "com.hypixel.hytale.server.core.modules.entity.player.PlayerInput$SetClientVelocity",
      "com.hypixel.hytale.server.core.modules.entity.player.PlayerInput$SetMovementStates",
      "com.hypixel.hytale.server.core.modules.entity.component.TransformComponent",
      "com.hypixel.hytale.server.core.modules.entity.component.PositionDataComponent",
      "com.hypixel.hytale.protocol.Vector3d"
    });
  }
}
