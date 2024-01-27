// Made by Syuto

boolean strafe, cooldown;
int airTicks, cooldownticks;

void onLoad() {
    modules.registerButton("AirStrafe", false);
    modules.registerSlider("AirStrafeSpeed", 1.2, 1, 10, 0.01);
    modules.registerSlider("AirStrafeTicks", 11, 1, 11, 1);
}

void onPreUpdate() {
    Entity player = client.getPlayer();
    Vec3 motion = client.getMotion();
    
    if (client.isMoving() && player.onGround()) {
        client.jump();
        if (!strafe) {
            client.setSpeed(0.42);
        }
    }
    if (player.getHurtTime() == 9 && !player.onGround() && !cooldown && client.isMoving()) {
        strafe = true;
        client.setSpeed(player.getSpeed() * 1.2);
        cooldown = true;
    } else {
        strafe = false;
    }
    if (cooldown) {
        cooldownticks++; 
    }
    if (cooldownticks == 10) {
        cooldown = false;
        client.print("&7[&dR&7] &aCooldown expired!");
        cooldownticks = 0;
    }
    
    if (!player.onGround()) {
        airTicks++;
    } else {
        airTicks = 0;
    }
    if (client.isMoving() && !player.onGround() && modules.getButton(scriptName, "AirStrafe") && !strafe) {
        if (airTicks == modules.getSlider(scriptName, "AirStrafeTicks")) {
            client.setSpeed(player.getSpeed() * modules.getSlider(scriptName, "AirStrafeSpeed"));
        }
    }
}


boolean onPacketReceived(SPacket packet) {
    Entity player = client.getPlayer();
    Vec3 motion = client.getMotion();
    if (packet instanceof S12) {
        S12 s12 = (S12) packet;
        if (s12.entityID == player.entityID) { 
            if (player.getHurtTime() <= 1 && s12.motion.y > 0d) {
                client.setMotion(-(s12.motion.x) / 8000d, s12.motion.y / 8000d, -(s12.motion.z) / 8000d); 
                return true;
            } else { 
                client.setMotion(motion.x, s12.motion.y / 8000d, motion.z);
                return false;
            }
        }
    }
    return true;
}


void onPostPlayerInput() {
    client.setJump(false);
}


void onEnable() {
    if (modules.isEnabled("AirVelo")) {
        modules.disable("AirVelo");
    }
    cooldownticks = 0;
    cooldown = false;
    airTicks = 0;
    strafe = false;
}


void onDisable() {
    if (!modules.isEnabled("AirVelo")) {
        modules.enable("AirVelo");
    }
    cooldownticks = 0;
    cooldown = false;
    airTicks = 0;
    strafe = false;
}
