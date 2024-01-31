boolean speeded;

void onPreUpdate() {
    if (client.isMouseDown(1) && !client.isKeyDown(57) && client.keybinds.isPressed("forward") && modules.isEnabled("Scaffold") && client.getPlayer().onGround()) {
        client.keybinds.setPressed("use", false);
        client.keybinds.setPressed("jump", false);
        if (!isDiagonal() && !(client.keybinds.isPressed("right") || client.keybinds.isPressed("left"))) {
                client.setSprinting(true);
                client.jump();
        } else if (isDiagonal() || (client.keybinds.isPressed("right") || client.keybinds.isPressed("left"))) {
                client.jump();
        }
        speeded = true;
    } else if (modules.isEnabled("Scaffold") && !client.isMouseDown(1) && !client.isKeyDown(57) && speeded) {
        client.setMotion(0, client.getMotion().y, 0);
        speeded = false;
    }
}

boolean isDiagonal() {
    float yaw = client.getPlayer().getYaw();
    yaw = (yaw + 360) % 360;
    final float threshold = 9;
    boolean isNorth = Math.abs(yaw - 0) < threshold || Math.abs(yaw - 360) < threshold;
    boolean isSouth = Math.abs(yaw - 180) < threshold;
    boolean isEast = Math.abs(yaw - 90) < threshold;
    boolean isWest = Math.abs(yaw - 270) < threshold;
    return !(isNorth || isSouth || isEast || isWest);
}