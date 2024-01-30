Map<String, String> moduleList = new HashMap<>();
Map<String, Map<String, String>> settingsList = new HashMap<>();
Map<String, Map<String, Map<String, Double>>> valuesList = new HashMap<>();

boolean onPacketSent(CPacket packet) {
    if (!(packet instanceof C01)) return true;

    C01 c01 = (C01) packet;
    String message = c01.message;
    if (!message.startsWith(".")) return true;

    String[] parts = message.split(" ");
    if (parts.length < 1 || parts.length > 3) return false;

    String input = parts[0].substring(1).toLowerCase();
    if (input.isEmpty()) return false;

    String module = moduleList.get(input);
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

    if (parts.length == 2) {
        String command = parts[1];
        switch (command) {
            case "help":
                Map<String, String> settings = settingsList.get(module);
                if (settings == null) return false; 
                for (Map.Entry<String, String> entry : settings.entrySet()) {
                    double valueSlider = modules.getSlider(module, entry.getValue());
                    String valueButton = Boolean.toString(modules.getButton(module, entry.getValue()));
                    String value;
                    if (valueButton.equals("true")) value = "true";
                    else if (valueSlider == 0 && valueButton.equals("false")) value = "false";
                    else value = String.valueOf(valueSlider);
                    client.print(entry.getKey() + ": " + value);
                }
                break;
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
            Double listValue = valuesList.get(module).get(setting).get(value);
            if (listValue == null) return false;
            modules.setSlider(module, setting, listValue);
            client.print("Set " + setting + " to " + value);
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
    
    // combat

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
    settings.put("clicks", "Clicks");
    settings.put("delay", "Delay");
    settings.put("delay-randomizer", "Delay randomizer");
    settings.put("place-with-blocks", "Place with blocks");
    settingsList.put("BurstClicker", settings);

    settings = new HashMap<>();
    settings.put("disable-in-creative", "Disable in creative");
    settings.put("left-click", "Left click");
    settings.put("chance-left", "Chance left");
    settings.put("weapon-only", "Weapon only");
    settings.put("only-while-targeting", "Only while targeting");
    settings.put("right-click", "Right click");
    settings.put("chance-right", "Chance right");
    settings.put("blocks-only", "Blocks only");
    settings.put("above-5-cps", "Above 5 cps");
    settingsList.put("ClickAssist", settings);
    
    settings = new HashMap<>();
    settings.put("multiplier", "Multiplier");
    settings.put("players-only", "Players only");
    settings.put("show-modified-hitbox", "Show modified hitbox");
    settings.put("weapon-only", "Weapon only");
    settingsList.put("Hitbox", settings);
    
    settings = new HashMap<>();
    settings.put("chance", "Chance");
    settings.put("jump-motion", "Jump motion");
    settingsList.put("Jump Reset", settings);
    
    settings = new HashMap<>();
    settings.put("aps", "APS");
    settings.put("autoblock", "Autoblock");
    settings.put("fov", "FOV");
    settings.put("range-attack", "Range (attack)");
    settings.put("range-swing", "Range (swing)");
    settings.put("range-block", "Range (block)");
    settings.put("rotation-mode", "Rotation mode");
    settings.put("rotation-smoothing", "Rotation smoothing");
    settings.put("sort-mode", "Sort mode");
    settings.put("switch-delay", "Switch delay");
    settings.put("targets", "Targets"); // suffix maybe idk miight need to add (max)
    settings.put("aim-invis", "Aim invis");
    settings.put("disable-in-inventory", "Disable in inventory");
    settings.put("disable-while-blocking", "Disable while blocking");
    settings.put("disable-while-mining", "Disable while mining");
    settings.put("fix-ghosting", "Fix ghosting");
    settings.put("hit-through-blocks", "Hit through blocks");
    settings.put("ignore-teammates", "Ignore teammates");
    settings.put("prioritize-enemies", "Prioritize enemies");
    settings.put("require-mouse-down", "Require mouse down");
    settings.put("silent-swing-while-blocking", "Silent swing while blocking");
    settings.put("weapon-only", "Weapon only");
    settingsList.put("KillAura", settings);
    
    Map<String, Map<String, Double>> value = new HashMap<>();
    Map<String, Double> values = new HashMap<>();
    values.put("manual", 0d);
    values.put("vanilla", 1d);
    values.put("damage", 2d);
    values.put("fake", 3d);
    values.put("partial", 4d);
    values.put("swap", 5d);
    values.put("interact-a", 6d);
    values.put("interact-b", 7d);
    value.put("Autoblock", values);
    valuesList.put("KillAura", value);
    
    settings = new HashMap<>();
    settings.put("min", "Min");
    settings.put("max", "Max");
    settings.put("weapon-only", "Weapon only");
    settings.put("moving-only", "Moving only");
    settings.put("sprint-only", "Sprint only");
    settings.put("hit-through-blocks", "Hit through blocks");
    settingsList.put("Reach", settings);
    
    settings = new HashMap<>();
    settings.put("attack-reduction", "Attack reduction");
    settings.put("chance", "Chance");
    settingsList.put("Reduce", settings);
    
    settings = new HashMap<>();
    settings.put("fov", "FOV");
    settings.put("predicted-ticks", "Predicted ticks");
    settings.put("range", "Range");
    settings.put("aim-invis", "Aim invis");
    settings.put("ignore-teammates", "Ignore teammates");
    settingsList.put("RodAimbot", settings);
    
    settings = new HashMap<>();
    settings.put("range", "Range");
    settings.put("weapon-only", "Weapon only");
    settingsList.put("TPAura", settings);
    
    settings = new HashMap<>();
    settings.put("horizontal", "Horizontal");
    settings.put("vertical", "Vertical");
    settings.put("chance", "Chance");
    settings.put("only-while-targeting", "Only while targeting");
    settings.put("disable-while-holding-s", "Disable while holding S");
    settingsList.put("Velocity", settings);
    
    settings = new HashMap<>();
    settings.put("chance", "Chance");
    settings.put("players-only", "Players only");
    settingsList.put("WTap", settings);
    
    // movement
    
    settings = new HashMap<>();
    settings.put("mode", "Mode");
    settings.put("speed", "Speed");
    settings.put("auto-jump", "Auto jump");
    settings.put("disable-in-water", "Disable in water");
    settings.put("disable-while-sneaking", "Disable while sneaking");
    settings.put("stop-motion", "Stop motion");
    settingsList.put("Bhop", settings);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("fast", 0d);
    values.put("ground", 1d);
    values.put("damage", 2d);
    value.put("Mode", values);
    valuesList.put("Bhop", value);
    
    settings = new HashMap<>();
    settings.put("boost-ticks", "Boost ticks");
    settings.put("multiplier", "Multiplier");
    settingsList.put("Boost", settings);
    
    settings = new HashMap<>();
    settings.put("horizontal-speed", "Horizontal speed");
    settings.put("vertical-speed", "Vertical speed");
    settings.put("buoyant", "Buoyant");
    settings.put("disable-while-using", "Disable while using");
    settings.put("disable-vertical-while-moving", "Disable vertical while moving");
    settings.put("forward-only", "Forward only");
    settingsList.put("Dolphin", settings);
    
    settings = new HashMap<>();
    settings.put("value", "Value");
    settings.put("horizontal-speed", "Horizontal speed");
    settings.put("vertical-speed", "Vertical speed");
    settings.put("show-bps", "Show BPS");
    settings.put("stop-motion", "Stop motion");
    settingsList.put("Fly", settings);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("1", 0d); // add names?
    values.put("2", 1d); // add names?
    values.put("3", 2d); // add names?
    value.put("Value", values);
    valuesList.put("Fly", value);
    
    settings = new HashMap<>();
    settings.put("slow", "Slow");
    settings.put("disable-while-jumping", "Disable while jumping");
    settings.put("only-reduce-reach-hits", "Only reduce reach hits");
    settingsList.put("KeepSprint", settings);
    
    settings = new HashMap<>();
    settings.put("mode", "Mode");
    settings.put("horizontal-speed", "Horizontal speed");
    settings.put("vertical-speed", "Vertical speed");
    settings.put("boost-ticks", "Boost ticks");
    settings.put("hurt-time-boost", "Hurt time boost");
    settings.put("invert-yaw", "Invert yaw");
    settingsList.put("Long Jump", settings);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("1", 0d); // add names
    values.put("auto-fireball", 1d); // add names
    value.put("Mode", values);
    valuesList.put("Long Jump", value);
    
    settings = new HashMap<>();
    settings.put("mode", "Mode");
    settings.put("slow", "Slow"); // fix this idk yet might need the %
    settings.put("disable-bow", "Disable bow");
    settings.put("sword-only", "Sword only");
    settings.put("vanilla-sword", "Vanilla sword");
    settingsList.put("NoSlow", settings);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("vanilla", 0d);
    values.put("bleh", 1d); // add names
    value.put("Mode", values);
    valuesList.put("NoSlow", value);
    
    settings = new HashMap<>();
    settings.put("speed", "Speed");
    settings.put("strafe-only", "Strafe only");
    settingsList.put("Speed", settings);
    
    settings = new HashMap<>();
    settings.put("display-text", "Display text");
    settings.put("rainbow", "Rainbow");
    settingsList.put("Sprint", settings);
    
    settings = new HashMap<>();
    settings.put("stop-x", "Stop X");
    settings.put("stop-y", "Stop Y");
    settings.put("stop-z", "Stop Z");
    settingsList.put("Stop Motion", settings);
    
    settings = new HashMap<>();
    settings.put("right-click-teleport", "Right click teleport");
    settings.put("highlight-target", "Highlight target");
    settings.put("highlight-path", "Highlight path");
    settingsList.put("Teleport", settings);
    
    settings = new HashMap<>();
    settings.put("speed", "Speed");
    settings.put("strafe-only", "Strafe only");
    settingsList.put("Timer", settings);
    
    settings = new HashMap<>();
    settings.put("distance", "Distance");
    settings.put("send-message", "Send message");
    settings.put("send-packets", "Send packets");
    settingsList.put("VClip", settings);
    
    // player 
    
    settings = new HashMap<>();
    settings.put("afk", "AFK");
    settings.put("jump", "Jump");
    settings.put("jump-only-when-collided", "Jump only when collided");
    settings.put("random-clicks", "Random clicks");
    settings.put("swap-item", "Swap item");
    settings.put("spin", "Spin");
    settings.put("randomize-delta", "Randomize delta");
    settings.put("randomize-pitch", "Randomize pitch");
    settings.put("minimum-delay-ticks", "Minimum delay ticks");
    settings.put("maximum-delay-ticks", "Maximum delay ticks");
    settingsList.put("AntiAFK", settings);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("1", 0d);
    values.put("2", 1d); // add names
    value.put("AFK", values);
    valuesList.put("AntiAFK", value);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("1", 0d);
    values.put("2", 1d); // add names
    value.put("Spin", values);
    valuesList.put("AntiAFK", value);
    
    settings = new HashMap<>();
    settings.put("fov", "FOV");
    settings.put("range", "Range");
    settings.put("disable-while-flying", "Disable while flying");
    settings.put("rotate-with-blocks", "Rotate with blocks");
    settings.put("rotate-with-projectiles", "Rotate with projectiles");
    settingsList.put("AntiFireball", settings);
    
    settings = new HashMap<>();
    settings.put("cancel-when-shifting", "Cancel when shifting");
    settingsList.put("AutoJump", settings);
    
    settings = new HashMap<>();
    settings.put("frame-delay", "Frame delay");
    settings.put("min-place-delay", "Min place delay");
    settings.put("min-post-delay", "Min post delay");
    settings.put("motion", "Motion");
    settings.put("disable-left", "Disable left");
    settings.put("hold-right", "Hold right");
    settings.put("fast-place-on-jump", "Fast place on jump");
    settings.put("pitch-check", "Pitch check");
    settingsList.put("AutoPlace", settings);
    
    settings = new HashMap<>();
    settings.put("only-same-type", "Only same type");
    settings.put("swap-to-greater-stack", "Swap to greater stack");
    settingsList.put("AutoSwap", settings);
    
    settings = new HashMap<>();
    settings.put("hover-delay", "Hover delay");
    settings.put("disable-while-right-click", "Disable while right click");
    settings.put("require-mouse-down", "Require mouse down");
    settings.put("swap-to-previous-slot", "Swap to previous slot");
    settingsList.put("AutoTool", settings);
    
    settings = new HashMap<>();
    settings.put("break-mode", "Break mode");
    settings.put("fov", "FOV");
    settings.put("range", "Range");
    settings.put("rate", "Rate");
    settings.put("allow-autoblock", "Allow autoblock");
    settings.put("allow-killaura", "Allow killaura");
    settings.put("break-block-above", "Break block above");
    settings.put("ground-spoof", "Ground spoof");
    settings.put("only-while-visible", "Only while visible");
    settings.put("render-block-outline", "Render block outline");
    settings.put("silent-swing", "Silent swing");
    settingsList.put("BedAura", settings);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("legit", 0d);
    values.put("2", 1d); // add names
    value.put("Break mode", values);
    valuesList.put("BedAura", value);
    
    settings = new HashMap<>();
    settings.put("show-initial-position", "Show initial position");
    settingsList.put("Blink", settings);
    
    settings = new HashMap<>();
    settings.put("1.7-hitreg", "1.7 hitreg");
    settings.put("remove-jump-ticks", "Remove jump ticks");
    settingsList.put("Delay Remover", settings);
    
    settings = new HashMap<>();
    settings.put("packet-delay", "Packet delay");
    settings.put("cancel-serverbound", "Cancel serverbound");
    settings.put("test", "Test");
    settingsList.put("Fake Lag", settings);
    
    settings = new HashMap<>();
    settings.put("break-delay-ticks", "Break delay ticks");
    settings.put("break-speed-multiplier", "Break speed multiplier");
    settings.put("mode", "Mode");
    settingsList.put("FastMine", settings);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("1", 0d);
    values.put("2", 1d); // add names
    value.put("Mode", values);
    valuesList.put("FastMine", value);
    
    settings = new HashMap<>();
    settings.put("tick-delay", "Tick delay");
    settings.put("blocks-only", "Blocks only");
    settings.put("pitch-check", "Pitch check");
    settingsList.put("FastPlace", settings);
    
    settings = new HashMap<>();
    settings.put("mode", "Mode");
    settings.put("speed", "Speed");
    settings.put("allow-chatting", "Allow chatting");
    settings.put("allow-digging", "Allow digging");
    settings.put("allow-interacting", "Allow interacting");
    settings.put("allow-placing", "Allow placing");
    settings.put("disable-on-damage", "Disable on damage");
    settings.put("show-arm", "Show arm");
    settingsList.put("Freecam", settings);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("1", 0d);
    values.put("2", 1d); // add names
    value.put("Mode", values);
    valuesList.put("Freecam", value);
    
    settings = new HashMap<>();
    settings.put("auto-armor", "Auto armor");
    settings.put("auto-armor-delay", "Auto armor delay");
    settings.put("auto-sort", "Auto sort");
    settings.put("sort-delay", "Sort delay");
    settings.put("steal-chests", "Steal chests");
    settings.put("custom-chest", "Custom chest");
    settings.put("close-after-stealing", "Close after stealing");
    settings.put("stealer-delay", "Stealer delay");
    settings.put("inventory-cleaner", "Inventory cleaner");
    settings.put("middle-click-to-clean", "Middle click to clean");
    settings.put("cleaner-delay", "Cleaner delay");
    settings.put("sword-slot", "Sword slot");
    settings.put("blocks-slot", "Blocks slot");
    settings.put("golden-apple-slot", "Golden apple slot");
    settings.put("projectile-slot", "Projectile slot");
    settings.put("speed-potion-slot", "Speed potion slot");
    settings.put("pearl-slot", "Pearl slot");
    settingsList.put("InvManager", settings);
        
    settings = new HashMap<>();
    settings.put("mode", "Mode");
    settings.put("minimum-fall-distance", "Minimum fall distance");
    settings.put("ignore-void", "Ignore void");
    settingsList.put("NoFall", settings);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("1", 0d);// add names
    values.put("single", 1d);
    values.put("extra", 2d);
    value.put("Mode", values);
    valuesList.put("NoFall", value);
    
    settings = new HashMap<>();
    settings.put("delay-until-next-shift", "Delay until next shift");
    settings.put("blocks-only", "Blocks only");
    settings.put("disable-on-forward", "Disable on forward");
    settings.put("pitch-check", "Pitch check");
    settings.put("shift", "Shift");
    settings.put("tower", "Tower");
    settingsList.put("Safewalk", settings);
    
    settings = new HashMap<>();
    settings.put("motion", "Motion");
    settings.put("rotation", "Rotation");
    settings.put("fast-scaffold", "Fast scaffold");
    settings.put("precision", "Precision");
    settings.put("autoswap", "AutoSwap");
    settings.put("highlight-blocks", "Highlight blocks");
    settings.put("multi-place", "Multi-place");
    settings.put("safewalk", "Safewalk");
    settings.put("show-block-count", "Show block count");
    settings.put("silent-swing", "Silent swing");
    settings.put("tower", "Tower");
    settingsList.put("Scaffold", settings);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("simple", 0d);
    values.put("strict", 1d);
    values.put("lazy", 2d);
    value.put("Rotation", values);
    valuesList.put("Scaffold", value);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("disabled", 0d);
    values.put("vanilla", 1d);
    values.put("border", 2d);
    value.put("Fast scaffold", values);
    valuesList.put("Scaffold", value);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("low", 0d);
    values.put("medium", 1d);
    values.put("high", 2d);
    values.put("very high", 3d);
    value.put("Precision", values);
    valuesList.put("Scaffold", value);
    
    settings = new HashMap<>();
    settings.put("mode", "Mode");
    settings.put("ground-ticks", "Ground ticks");
    settings.put("speed", "Speed");
    settings.put("diagonal-speed", "Diagonal speed");
    settings.put("slowed-speed", "Slowed speed");
    settings.put("slowed-ticks", "Slowed ticks");
    settings.put("disable-while-hurt", "Disable while hurt");
    settingsList.put("Tower", settings);
    
    value = new HashMap<>();
    values = new HashMap<>();
    values.put("normal", 0d);
    values.put("vanlla", 1d);
    value.put("Mode", values);
    valuesList.put("Tower", value);
    
    settings = new HashMap<>();
    settings.put("pickup-water", "Pickup water");
    settings.put("silent-aim", "Silent aim");
    settings.put("switch-to-item", "Switch to item");
    settingsList.put("Water Bucket", settings);
}