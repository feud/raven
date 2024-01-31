Map<String, String> moduleList = new HashMap<>();
Map<String, Map<String, String>> settingsList = new HashMap<>();
Map<String, Map<String, Map<String, Double>>> valuesList = new HashMap<>();

String chatPrefix = client.colorSymbol + "7[" + client.colorSymbol + "dR" + client.colorSymbol + "7]" + client.colorSymbol + "r ";

boolean onPacketSent(CPacket packet) {
    if (!(packet instanceof C01)) return true;

    C01 c01 = (C01) packet;
    String message = c01.message.toLowerCase();
    if (!message.startsWith(".")) return true;

    String[] parts = message.split(" ");
    if (parts.length < 1 || parts.length > 3) return false;

    String input = parts[0].substring(1);
    if (input.isEmpty()) return false;

    String module = moduleList.get(input);
    if (module == null) return false;

    if (parts.length == 1) {
        boolean enabled = modules.isEnabled(module);
        if (enabled) {
            modules.disable(module);
            client.print(chatPrefix + client.colorSymbol + "7Disabled " + client.colorSymbol + "c" + module);
        } else {
            modules.enable(module);
            client.print(chatPrefix + client.colorSymbol + "7Enabled " + client.colorSymbol + "a" + module);
        }
        return false;
    }

    if (parts.length == 2) {
        String command = parts[1];
        switch (command) {
            case "help":
                Map<String, String> settings = settingsList.get(module);
                if (settings == null) return false;

                List<Map.Entry<String, String>> list = new ArrayList<>(settings.entrySet());

                for (int i = 0; i < list.size() - 1; i++) {
                    for (int j = 0; j < list.size() - i - 1; j++) {
                        if (list.get(j).getKey().compareToIgnoreCase(list.get(j + 1).getKey()) > 0) {
                            Map.Entry<String, String> temp = list.get(j);
                            list.set(j, list.get(j + 1));
                            list.set(j + 1, temp);
                        }
                    }
                }

                for (Map.Entry<String, String> entry : list) {
                    double valueSlider = modules.getSlider(module, entry.getValue());
                    String valueButton = Boolean.toString(modules.getButton(module, entry.getValue()));
                    String value = "";

                    if (valueButton.equals("true")) value = client.colorSymbol + "atrue";
                    else if (valuesList.get(module).get(entry.getValue()) != null) {
                        for (Map.Entry<String, Double> entry2 : valuesList.get(module).get(entry.getValue()).entrySet()) {
                            if (entry2.getValue() == valueSlider) {
                                value = client.colorSymbol + "e" + entry2.getKey();
                                break;
                            }
                        }
                    }
                    else if (valueSlider == 0 && valueButton.equals("false")) value = client.colorSymbol + "cfalse";
                    else value = client.colorSymbol + "e" + String.valueOf(valueSlider);
                    client.print(chatPrefix + client.colorSymbol + "7" + entry.getKey() + client.colorSymbol + "7: " + value);
                }
                break;
        }
    }

    if (parts.length == 3) {
        String setting = settingsList.get(module).get(parts[1]);
        if (setting == null) return false;

        String value = parts[2];
        try {
             if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                boolean valueBool = Boolean.parseBoolean(value);
                modules.setButton(module, setting, valueBool);
                String boolString = valueBool == true ? client.colorSymbol + "atrue" : client.colorSymbol + "cfalse";
                client.print(chatPrefix + client.colorSymbol + "7Set " + parts[1] + client.colorSymbol + "7 to " + boolString);
            } else {
                double valueDouble = Double.parseDouble(value);
                modules.setSlider(module, setting, valueDouble);
                client.print(chatPrefix + client.colorSymbol + "7Set " + parts[1] + client.colorSymbol + "7 to " + client.colorSymbol + "e" + valueDouble);
            }
        } catch (NumberFormatException e) {
            Double listValue = valuesList.get(module).get(setting).get(value);
            if (listValue == null) return false;
            modules.setSlider(module, setting, listValue);
            client.print(chatPrefix + client.colorSymbol + "7Set " + parts[1] + client.colorSymbol + "7 to " + client.colorSymbol + "e" + value);
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
    values.put("1", 0d); // Add mode names
    values.put("2", 1d);
    values.put("3", 2d);
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
    values.put("1", 0d); // Add mode names
    values.put("auto-fireball", 1d);
    value.put("Mode", values);
    valuesList.put("Long Jump", value);

    settings = new HashMap<>();
    settings.put("mode", "Mode");
    settings.put("slow", "Slow"); // Will possibly need a % at the end.
    settings.put("disable-bow", "Disable bow");
    settings.put("sword-only", "Sword only");
    settings.put("vanilla-sword", "Vanilla sword");
    settingsList.put("NoSlow", settings);

    value = new HashMap<>();
    values = new HashMap<>();
    values.put("vanilla", 0d); // Add mode names
    values.put("bleh", 1d);
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
    values.put("1", 0d); // Add mode names
    values.put("2", 1d);
    value.put("AFK", values);
    valuesList.put("AntiAFK", value);

    value = new HashMap<>();
    values = new HashMap<>();
    values.put("1", 0d); // Add mode names
    values.put("2", 1d);
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
    values.put("1", 0d); // Add mode names
    values.put("2", 1d);
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
    values.put("1", 0d); // Add mode names
    values.put("2", 1d);
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
    values.put("1", 0d); // Add mode names
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
    
    settings = new HashMap<>();
    settings.put("entity-spawn-delay", "Entity spawn delay");
    settings.put("delay", "Delay");
    settings.put("pit-spawn-check", "Pit spawn check");
    settings.put("tab-list", "Tab list");
    settingsList.put("AntiBot", settings);

    settings = new HashMap<>();
    settings.put("theme", "Theme");
    settings.put("range", "Range");
    settings.put("rate", "Rate");
    settings.put("only-render-first-bed", "Only render first bed");
    settingsList.put("BedESP", settings);

    settings = new HashMap<>();
    settings.put("mode", "Mode"); // Add mode names
    settings.put("show-manual", "Show Manual");
    settings.put("show-bedaura", "Show BedAura");
    settingsList.put("BreakProgress", settings);

    settings = new HashMap<>();
    settings.put("ignore-bots", "Ignore bots");
    settingsList.put("Chams", settings);

    settings = new HashMap<>();
    settings.put("red", "Red");
    settings.put("green", "Green");
    settings.put("blue", "Blue");
    settings.put("rainbow", "Rainbow");
    settings.put("outline", "Outline");
    settings.put("shade", "Shade");
    settings.put("disable-if-opened", "Disable if opened");
    settingsList.put("ChestESP", settings);

    settings = new HashMap<>();
    settings.put("render-arrows", "Render arrows");
    settings.put("render-ender-pearls", "Render ender pearls");
    settings.put("render-fireballs", "Render fireballs");
    settings.put("arrow", "Arrow"); // add value
    settings.put("circle-radius", "Circle radius"); // add value?
    settings.put("item-colors", "Item colors");
    settings.put("render-item", "Render item");
    settings.put("render-only-threats", "Render only threats");
    settings.put("show-in-chat", "Show in chat");
    settingsList.put("Indicators", settings);

    settings = new HashMap<>();
    settings.put("disable-iron", "Disable iron");
    settingsList.put("ItemESP", settings);

    settings = new HashMap<>();
    settings.put("blaze", "Blaze");
    settings.put("creeper", "Creeper");
    settings.put("enderman", "Enderman");
    settings.put("ghast", "Ghast");
    settings.put("silverfish", "Silverfish");
    settings.put("skeleton", "Skeleton");
    settings.put("slime", "Slime");
    settings.put("spider", "Spider");
    settings.put("zombie", "Zombie");
    settings.put("zombie-pigman", "Zombie Pigman");
    settingsList.put("MobESP", settings);

    settings = new HashMap<>();
    settings.put("y-offset", "Y-Offset");
    settings.put("scale", "Scale");
    settings.put("auto-scale", "Auto-scale");
    settings.put("draw-rect", "Draw rect");
    settings.put("show-health", "Show health");
    settings.put("show-hits-to-kill", "Show hits to kill");
    settings.put("show-invis", "Show invis");
    settings.put("remove-tags", "Remove tags");
    settings.put("render-friends", "Render friends"); // Could be "Render friends (green)"
    settings.put("render-enemies", "Render enemies"); // Could be "Render enemies (red)"
    settingsList.put("Nametags", settings);

    settings = new HashMap<>();
    settings.put("red", "Red");
    settings.put("green", "Green");
    settings.put("blue", "Blue");
    settings.put("rainbow", "Rainbow");
    settings.put("2d", "2D");
    settings.put("arrow", "Arrow");
    settings.put("box", "Box");
    settings.put("health", "Health");
    settings.put("ring", "Ring");
    settings.put("shaded", "Shaded");
    settings.put("expand", "Expand");
    settings.put("x-shift", "X-Shift");
    settings.put("red-on-damage", "Red on damage");
    settings.put("show-invis", "Show invis");
    settingsList.put("PlayerESP", settings);

    settings = new HashMap<>();
    settings.put("show-in-gui", "Show in GUI");
    settings.put("show-tracer-lines", "Show tracer lines");
    settingsList.put("Radar", settings);

    settings = new HashMap<>();
    settings.put("shader", "Shader"); // Add mode names
    settingsList.put("Shaders", settings);

    settings = new HashMap<>();
    settings.put("theme", "Theme");
    settings.put("render-esp", "Render ESP");
    settings.put("show-win-or-loss", "Show win or loss");
    settingsList.put("TargetHUD", settings);

    settings = new HashMap<>();
    settings.put("show-invis", "Show invis");
    settings.put("line-width", "Line Width");
    settings.put("red", "Red");
    settings.put("green", "Green");
    settings.put("blue", "Blue");
    settings.put("rainbow", "Rainbow");
    settingsList.put("Tracers", settings);

    settings = new HashMap<>();
    settings.put("auto-scale", "Auto-scale");
    settings.put("disable-uncharged-bow", "Disable uncharged bow");
    settings.put("highlight-on-entity", "Highlight on entity");
    settings.put("shorten-line", "Shorten line");
    settingsList.put("Trajectories", settings);

    settings = new HashMap<>();
    settings.put("theme", "Theme");
    settings.put("align-right", "Align right");
    settings.put("alphabetical-sort", "Alphabetical sort");
    settings.put("drop-shadow", "Drop shadow");
    settings.put("lowercase", "Lowercase");
    settings.put("remove-closet-modules", "Remove closet modules");
    settings.put("remove-render-modules", "Remove render modules");
    settings.put("remove-scripts", "Remove scripts");
    settings.put("show-module-info", "Show module info");
    settingsList.put("HUD", settings);

    settings = new HashMap<>();
    settings.put("range", "Range");
    settings.put("rate", "Rate");
    settings.put("coal", "Coal");
    settings.put("diamond", "Diamond");
    settings.put("emerald", "Emerald");
    settings.put("gold", "Gold");
    settings.put("iron", "Iron");
    settings.put("lapis", "Lapis");
    settings.put("obsidian", "Obsidian");
    settings.put("redstone", "Redstone");
    settings.put("spawner", "Spawner");
    settingsList.put("Xray", settings);

    value = new HashMap<>();
    values = new HashMap<>();
    values.put("rainbow", 0d);
    values.put("cherry", 1d);
    values.put("cotton-candy", 2d);
    values.put("flare", 3d);
    values.put("flower", 4d);
    values.put("gold", 5d);
    values.put("grayscale", 6d);
    values.put("royal", 7d);
    values.put("sky", 8d);
    values.put("vine", 9d);
    value.put("Theme", values);
    valuesList.put("BedESP", value);
    valuesList.put("TargetHUD", value);
    valuesList.put("HUD", value);

    settings = new HashMap<>();
    settings.put("artificial", "Artificial");
    settings.put("hide-message", "Hide message");
    settings.put("remove-bots", "Remove bots");
    settingsList.put("AutoWho", settings);

    settings = new HashMap<>();
    settings.put("obsidian-esp", "Obsidian ESP");
    settings.put("whitelist-own-bed", "Whitelist own bed");
    settings.put("diamond-armor", "Diamond armor");
    settings.put("invisible-players", "Invisible players");
    settings.put("obsidian-on-bed", "Obsidian on bed");
    settings.put("ender-pearl", "Ender pearl");
    settings.put("should-ping", "Should ping");
    settingsList.put("Bedwars Helper", settings);

    settings = new HashMap<>();
    settings.put("mode", "Mode"); // Add mode names
    settings.put("send-on-join", "Send on join");
    settings.put("threat-level", "Threat level");
    settingsList.put("Duels Stats", settings);

    settings = new HashMap<>();
    settings.put("alert", "Alert");
    settings.put("search-detectives", "Search detectives");
    settings.put("announce-murderer", "Announce murderer");
    settings.put("gold-esp", "Gold ESP");
    settingsList.put("Murder Mystery", settings);

    settings = new HashMap<>();
    settings.put("fence-height", "Fence height");
    settings.put("block-type", "Block type"); // add value
    settingsList.put("Sumo Fences", settings);

    settings = new HashMap<>();
    settings.put("break-speed", "Break speed");
    settings.put("delay-after-breaking", "Delay after breaking");
    settings.put("delay-after-placing", "Delay after placing");
    settings.put("range", "Range");
    settings.put("only-visible", "Only visible");
    settings.put("only-while-middle-clicking", "Only while middle clicking");
    settingsList.put("WoolWars", settings);

    settings = new HashMap<>();
    settings.put("aimbot", "Aimbot");
    settings.put("hitbox-esp", "Hitbox ESP");
    settingsList.put("Quakecraft", settings);

    settings = new HashMap<>();
    settings.put("level", "Level");
    settingsList.put("Extra bobbing", settings);

    settings = new HashMap<>();
    settings.put("range", "Range");
    settings.put("aim", "Aim");
    settings.put("play-sound", "Play sound");
    settings.put("players-only", "Players only");
    settingsList.put("SlyPort", settings);

    settings = new HashMap<>();
    settings.put("rotation-yaw", "Rotation yaw");
    settings.put("speed", "Speed");
    settingsList.put("Spin", settings);

    settings = new HashMap<>();
    settings.put("flag-interval", "Flag interval");
    settings.put("add-cheaters-as-enemy", "Add cheaters as enemy");
    settings.put("ignore-teammates", "Ignore teammates");
    settings.put("only-atlas-suspect", "Only atlas suspect");
    settings.put("should-ping", "Should ping");
    settings.put("autoblock", "AutoBlock");
    settings.put("noslow", "NoSlow");
    settings.put("nuker", "Nuker");
    settings.put("scaffold", "Scaffold");
    settings.put("aimassist", "AimAssist");
    settings.put("autoclicker", "AutoClicker");
    settings.put("fastmine", "FastMine");
    settings.put("legit-scaffold", "Legit Scaffold");
    settings.put("protocol", "Protocol");
    settingsList.put("Anticheat", settings);

    settings = new HashMap<>();
    settings.put("alert-interval", "Alert interval");
    settings.put("high-latency", "High latency");
    settingsList.put("Latency Alerts", settings);

    settings = new HashMap<>();
    settings.put("hide-all-names", "Hide all names");
    settingsList.put("Name Hider", settings);

    settings = new HashMap<>();
    settings.put("animate", "Animate");
    settingsList.put("Command Line", settings);

    settings = new HashMap<>();
    settings.put("rainbow-outlines", "Rainbow outlines");
    settings.put("remove-player-model", "Remove player model");
    settings.put("remove-watermark", "Remove watermark");
    settings.put("translucent-background", "Translucent background");
    settingsList.put("Gui", settings);

    settings = new HashMap<>();
    settings.put("set-axe-as-weapon", "Set axe as weapon");
    settings.put("set-rod-as-weapon", "Set rod as weapon");
    settings.put("set-stick-as-weapon", "Set stick as weapon");
    settings.put("middle-click-friends", "Middle click friends");
    settings.put("rotate-body", "Rotate body");
    settings.put("movement-fix", "Movement fix");
    settings.put("random-yaw-factor", "Random yaw factor");
    settings.put("send-message-on-enable", "Send message on enable");
    settings.put("offset", "Offset");
    settings.put("time-multiplier", "Time multiplier");
    settingsList.put("Settings", settings);
}