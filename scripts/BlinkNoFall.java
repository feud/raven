List<CPacket> nofallPackets = new ArrayList<>();
List<CPacket> regularPackets = new ArrayList<>();
boolean allPackets = true, blinking = false, KillAura = false, BedAura = false, doneBlinking = false;
Vec3 blinkPos, lastDeath, deathPos;
int ticks = 0, stopTicks = 0, maxFall = 30, minFall = 5, dticks = 0, bedauraDelay = 3, killauraDelay = 6;

void onEnable() {
    lastDeath = client.getPlayer().getPosition();
    deathPos = lastDeath;
    nofallPackets.clear();
    regularPackets.clear();
    ticks = 0;
    KillAura = BedAura = blinking = false;
}

void onDisable() {
    if (blinking) {
        if (client.getPlayer().onGround() && !client.allowFlying()) {
            for (CPacket packet : nofallPackets) {
                client.sendPacketNoEvent(packet);
            }
        } else {
            for (CPacket packet : regularPackets) {
                client.sendPacketNoEvent(packet);
            }
        }
    }
    nofallPackets.clear();
    regularPackets.clear();
    ticks = 0;
    if (KillAura) modules.enable("KillAura");
    if (BedAura) modules.enable("BedAura");
    KillAura = BedAura = blinking = false;
}

void onRenderTick(float partialTicks) {
    if (!blinking || ticks <= 1 || !client.getScreen().isEmpty()) {
        return;
    }
    String text = "blinking: " + client.colorSymbol;
    if (ticks > 50) {
        text += "c";
    } else if (ticks > 30) {
        text += "6";
    } else if (ticks > 20) {
        text += "e";
    } else {
        text += "a";
    }
    text += ticks;
    int[] disp = client.getDisplaySize();
    int wid = client.getFontWidth(text) / 2 - 2;
    client.render.text(text, disp[0] / 2 - wid, disp[1] / 2 + 13, -1, true);
}

boolean onPacketSent(CPacket packet) {
    if (!blinking) return true;
    if (packet instanceof C03) {
        regularPackets.add(packet);
        C03 nofallPacket = (C03) packet;
        boolean groundlol = !voidCheck18(nofallPacket.pos);
        switch(nofallPacket.name.substring(0, 3)) {
            case "C03":
                nofallPackets.add(new C03(true));
                break;
            case "C04":
                nofallPackets.add(new C03(nofallPacket.pos, true));
                break;
            case "C05":
                nofallPackets.add(new C03(nofallPacket.yaw, nofallPacket.pitch, true));
                break;
            case "C06":
                nofallPackets.add(new C03(nofallPacket.pos, nofallPacket.yaw, nofallPacket.pitch, true));
                break;
        }
        return false;
    }
    nofallPackets.add(packet);
    regularPackets.add(packet);
    return false;
}

void onPreUpdate() {
    dticks++;
    Entity player = client.getPlayer();
    boolean dead = isDead();
    Vec3 pos = player.getPosition();

    if (!blinking) {
        handlePostBlinkActions();
        initiateBlinkingCondition(player, pos);
        bridge.remove("noFallBlinking");
    } else {
        handleBlinkingActions(player, pos, dead);
        bridge.add("noFallBlinking");
    }
}

void handlePostBlinkActions() {
    if (doneBlinking) {
        if (KillAura) {
            modules.enable("KillAura");
            KillAura = doneBlinking = false;
        }
        if (BedAura) {
            modules.enable("BedAura");
            BedAura = false;
        }
    }
}

void initiateBlinkingCondition(Entity player, Vec3 pos) {
    if (player.getHurtTime() == 0 && !client.allowFlying() && !modules.isEnabled("Scaffold") && player.onGround() && player.isOnEdge() && client.keybinds.isPressed("forward") && !client.keybinds.isPressed("back") && !client.keybinds.isPressed("sneak") && !client.keybinds.isPressed("jump") && fallDistance()/*  && !voidCheck() */) {
        blinkPos = player.getPosition();
        KillAura = modules.isEnabled("KillAura");
        BedAura = modules.isEnabled("BedAura");
        modules.disable("KillAura");
        modules.disable("BedAura");
        blinking = true;
        doneBlinking = false;
    }
}

void handleBlinkingActions(Entity player, Vec3 pos, boolean dead) {
    ++ticks;
    if (modules.isEnabled("KillAura")) {
        KillAura = true;
        modules.disable("KillAura");
    }
    if (modules.isEnabled("BedAura")) {
        BedAura = true;
        modules.disable("BedAura");
    }
    if (dead || pos.y - blinkPos.y > 0 || blinkPos.y - pos.y > maxFall || client.isFlying() || player.getHurtTime() != 0 || (player.onGround() && (!player.isOnEdge() || Math.abs(pos.y - blinkPos.y) != 0))/*  || voidCheck() */) {
        if (player.onGround() && !client.allowFlying()/*  && blinkPos.y - pos.y > minFall */ && player.getHurtTime() == 0) {
            synchronized (nofallPackets) {
                for (CPacket packet : nofallPackets) {
                    client.sendPacketNoEvent(packet);
                }
            }
        } else {
            synchronized (regularPackets) {
                for (CPacket packet : regularPackets) {
                    client.sendPacketNoEvent(packet);
                }
            }
        }
        blinking = false;
        doneBlinking = true;
        nofallPackets.clear();
        regularPackets.clear();
        ticks = 0;
        stopTicks = dticks;
    }
}

int gc(long speed, long... delay) {
    long time = client.time() + (delay.length > 0 ? delay[0] : 0);
    return Color.getHSBColor(time % (15000L / speed) / (15000.0f / speed), 1.0f, 1.0f).getRGB();
}

boolean voidCheck() {
    Vec3 pos = client.getPlayer().getPosition();
    for (int i = (int) Math.floor(pos.y); i > -1; i--) {
        Block block = client.getWorld().getBlockAt((int) Math.floor(pos.x), i, (int) Math.floor(pos.z));
        if (!block.name.equals("air")) {
            return false;
        }
    }
    return true;
}

boolean fallDistance() {
    int fallDist = -1;
    Vec3 pos = client.getPlayer().getPosition();
    int y = (int) Math.floor(pos.y);
    if (pos.y % 1 == 0) y--;
    for (int i = y; i > -1; i--) {
        Block block = client.getWorld().getBlockAt((int) Math.floor(pos.x), i, (int) Math.floor(pos.z));
        if (!block.name.equals("air") && !block.name.contains("sign")) {
            fallDist = y - i;
            break;
        }
    }
    if (fallDist < minFall && fallDist != -1) return false;
    if (fallDist > maxFall) return false;
    return true;
}

boolean isDead() {
    Entity player = client.getPlayer();
    lastDeath = deathPos;
    deathPos = player.getPosition();
    double deltaY = Math.abs(deathPos.y - lastDeath.y);
    if (deltaY > 20) return true;
    return false;
}

boolean voidCheck18(Vec3 pos) {
    for (int i = (int) Math.floor(pos.y); i > -1; i--) {
        Block block = client.getWorld().getBlockAt((int) Math.floor(pos.x), i, (int) Math.floor(pos.z));
        if (!block.name.equals("air")) {
            return false;
        }
    }
    return true;
}