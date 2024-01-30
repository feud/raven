Map<String, String> moduleList = new HashMap<>();
Map<String, Map<String, String>> settingsList = new HashMap<>();

boolean onPacketSent(CPacket packet) {
    if (!(packet instanceof C01)) return true;

    C01 c01 = (C01) packet;
    String message = c01.message;
    if (!message.startsWith(".")) return true;

    String[] parts = message.split(" ");
    if (parts.length < 1 || parts.length == 2 || parts.length > 3) return false;

    String command = parts[0].substring(1).toLowerCase();
    if (command.isEmpty()) return false;

    String module = moduleList.get(command);
    if (module == null) return false;

    if (parts.length == 1) {
        boolean enabled = modules.isEnabled(module);
        if (enabled) {
            modules.disable(module);
            client.print("Disabled " + module);
        } else {
            modules.enable(module);
            client.print("Enabled " + module);
        }
        return false;
    }

    if (parts.length == 3) {
        String settingKey = parts[1].toLowerCase();
        String setting = settingsList.get(module).get(settingKey);
        if (setting == null) return false;

        String value = parts[2];
        try {
             if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                boolean valueBool = Boolean.parseBoolean(value);
                modules.setButton(module, setting, valueBool);
                client.print("Set " + setting + " to " + valueBool);
            } else {
                double valueDouble = Double.parseDouble(value);
                modules.setSlider(module, setting, valueDouble);
                client.print("Set " + setting + " to " + valueDouble);
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    return false;
}

void onLoad() {
    moduleList.put("autoclicker", "AutoClicker");
    moduleList.put("aimassist", "AimAssist");
    moduleList.put("antiknockback", "AntiKnockback");
    moduleList.put("burstclicker", "BurstClicker");
    moduleList.put("clickassist", "ClickAssist");
    moduleList.put("hitbox", "Hitbox");
    moduleList.put("jump-reset", "Jump Reset");
    moduleList.put("killaura", "KillAura");
    moduleList.put("reach", "Reach");
    moduleList.put("reduce", "Reduce");
    moduleList.put("rodaimbot", "RodAimbot");
    moduleList.put("tpaura", "TPAura");
    moduleList.put("velocity", "Velocity");
    moduleList.put("wtap", "WTap");
    moduleList.put("bhop", "Bhop");
    moduleList.put("boost", "Boost");
    moduleList.put("dolphin", "Dolphin");
    moduleList.put("fly", "Fly");
    moduleList.put("invmove", "InvMove");
    moduleList.put("keepsprint", "KeepSprint");
    moduleList.put("long-jump", "Long Jump");
    moduleList.put("noslow", "NoSlow");
    moduleList.put("speed", "Speed");
    moduleList.put("sprint", "Sprint");
    moduleList.put("stopmotion", "Stop Motion");
    moduleList.put("teleport", "Teleport");
    moduleList.put("timer", "Timer");
    moduleList.put("vclip", "VClip");
    moduleList.put("antiafk", "AntiAFK");
    moduleList.put("antifireball", "AntiFireball");
    moduleList.put("autojump", "AutoJump");
    moduleList.put("autoplace", "AutoPlace");
    moduleList.put("autoswap", "AutoSwap");
    moduleList.put("autotool", "AutoTool");
    moduleList.put("bedaura", "BedAura");
    moduleList.put("blink", "Blink");
    moduleList.put("delay-remover", "Delay Remover");
    moduleList.put("fake-lag", "Fake Lag");
    moduleList.put("fastmine", "FastMine");
    moduleList.put("fastplace", "FastPlace");
    moduleList.put("freecam", "Freecam");
    moduleList.put("invmanager", "InvManager");
    moduleList.put("nofall", "NoFall");
    moduleList.put("norotate", "NoRotate");
    moduleList.put("safewalk", "Safewalk");
    moduleList.put("scaffold", "Scaffold");
    moduleList.put("tower", "Tower");
    moduleList.put("water-bucket", "Water Bucket");
    moduleList.put("antishuffle", "AntiShuffle");
    moduleList.put("bedesp", "BedESP");
    moduleList.put("breakprogress", "BreakProgress");
    moduleList.put("chams", "Chams");
    moduleList.put("chestesp", "ChestESP");
    moduleList.put("indicators", "Indicators");
    moduleList.put("itemesp", "ItemESP");
    moduleList.put("mobesp", "MobESP");
    moduleList.put("nametags", "Nametags");
    moduleList.put("offscreenesp", "OffScreenESP");
    moduleList.put("playeresp", "PlayerESP");
    moduleList.put("radar", "Radar");
    moduleList.put("shaders", "Shaders");
    moduleList.put("targethud", "TargetHUD");
    moduleList.put("tracers", "Tracers");
    moduleList.put("trajectories", "Trajectories");
    moduleList.put("hud", "HUD");
    moduleList.put("xray", "Xray");
    moduleList.put("autowho", "AutoWho");
    moduleList.put("bedwars-helper", "Bedwars Helper");
    moduleList.put("bridge-info", "Bridge Info");
    moduleList.put("duels-stats", "Duels Stats");
    moduleList.put("murder-mystery", "Murder Mystery");
    moduleList.put("sumo-fences", "Sumo Fences");
    moduleList.put("woolwars", "WoolWars");
    moduleList.put("quakecraft", "Quakecraft");
    moduleList.put("anticheat", "Anticheat");
    moduleList.put("latency-alerts", "Latency Alerts");
    moduleList.put("name-hider", "Name Hider");
    moduleList.put("antibot", "AntiBot");
    moduleList.put("gui", "Gui");

    Map<String, String> settings = new HashMap<>();
    settings.put("min-cps", "Min CPS");
    settings.put("max-cps", "Max CPS");
    settings.put("jitter", "Jitter");
    settings.put("block-hit-chance", "Block hit chance");
    settings.put("left-click", "Left click");
    settings.put("right-click", "Right click");
    settings.put("break-blocks", "Break blocks");
    settings.put("inventory-fill", "Inventory fill");
    settings.put("weapon-only", "Weapon only");
    settings.put("blocks-only", "Blocks only");
    settingsList.put("AutoClicker", settings);

    settings = new HashMap<>();
    settings.put("speed", "Speed");
    settings.put("fov", "FOV");
    settings.put("distance", "Distance");
    settings.put("click-aim", "Click aim");
    settings.put("weapon-only", "Weapon only");
    settings.put("aim-invis", "Aim invis");
    settings.put("blatant-mode", "Blatant mode");
    settingsList.put("AimAssist", settings);

    settings = new HashMap<>();
    settings.put("horizontal", "Horizontal");
    settings.put("vertical", "Vertical");
    settings.put("cancel-explosion-packet", "Cancel explosion packet");
    settings.put("damage-boost", "Damage boost");
    settings.put("boost-multiplier", "Boost multiplier");
    settings.put("ground-check", "Ground check");
    settingsList.put("AntiKnockback", settings);

    settings = new HashMap<>();
    settings.put("horizontal", "Horizontal");
    settings.put("vertical", "Vertical");
    settings.put("cancel-explosion-packet", "Cancel explosion packet");
    settings.put("damage-boost", "Damage boost");
    settings.put("boost-multiplier", "Boost multiplier");
    settings.put("ground-check", "Ground check");
    settingsList.put("AntiKnockback", settings);

    settings = new HashMap<>();
    settings.put("clicks", "Clicks");
    settings.put("delay", "Delay (ms)");
    settings.put("delay-randomizer", "Delay randomizer");
    settings.put("Place-with-blocks", "Place with blocks");
    settingsList.put("BurstClicker", settings);

    settings = new HashMap<>();
    settings.put("disable-in-creative", "Disable in creative");
    settings.put("left-click", "Left click");
    settings.put("delay-randomizer", "Delay randomizer");
    settings.put("Place-with-blocks", "Place with blocks");
    settingsList.put("ClickAssist", settings);
}

// Started by pug, finished by mic.