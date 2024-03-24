final String hypixelApiKey = "";
final String antisniperApiKey = "";

final Map<String, Integer> encounters = new ConcurrentHashMap<>();
final Map<String, String> queueData = new LinkedHashMap<>();
final List<String> partyMembers = new ArrayList<>();
final int labelColor = new Color(24, 176, 248).getRGB();
final int backgroundColor = new Color(0, 0, 0, 145).getRGB();
boolean toggled, canShow, scanPlayers, setTeamColor;
final String[] sortBy = {"Custom", "Star", "FKDR", "Session", "Ping", "Encounters"};
final String[] sortMode = {"Ascending", "Descending"};
int prevMode, partySize = 0;

void onLoad() {
    modules.registerButton("Tags", true);
    modules.registerButton("Winstreaks", true);
    modules.registerButton("Session", true);
    modules.registerButton("Ping", true);
    modules.registerButton("Teams", true);
    modules.registerButton("Team prefix", false);
    modules.registerButton("Show yourself", false);
    modules.registerButton("Sort by team", false);
    modules.registerButton("Clear data", false);
    modules.registerButton("Hide if empty", false);
    modules.registerButton("Auto hide", false);
    modules.registerSlider("Sort by", 0, sortBy);
    modules.registerSlider("Sort mode", 0, sortMode);
}

void onRenderTick(float partialTicks) {
    if (!canProcess()) {
        return;
    }
    if (!client.getScreen().isEmpty()) {
        return;
    }
    if (modules.getButton("Overlay", "Hide if empty") && queueData.isEmpty()) {
        return;
    }
    if (modules.getButton("Overlay", "Auto hide") && getBedwarsStatus() != 1) {
        return;
    }

     try {
        int endX = 5 + getBoxWidth();

        drawOutlineRect(5, 5, endX, 7 + client.getFontHeight() + ((queueData.size() != 0) ? 5 : 0) + 11 * queueData.size());

        // Labels
        int stringWidth = client.getFontWidth("[E] ") + 2;

        client.render.text("[E]", 8, 7, 1, labelColor, true);
        client.render.text("[PLAYER]", 9 + stringWidth, 7, 1, labelColor, true);
        drawCenteredText("[STAR]", 127 + stringWidth, 7, 1, labelColor, true);
        drawCenteredText("[FKDR]", 164 + stringWidth, 7, 1, labelColor, true);
        int extraWidth = 195 + stringWidth;
        int check = 0;
        int tagsX = 0;
        int wsX = 0;
        int sessionX = 0;
        int pingX = 0;
        if (modules.getButton("Overlay", "Winstreaks")) {
            drawCenteredText("[WS]", extraWidth, 7, 1, labelColor, true);
            wsX = extraWidth;
            if (modules.getButton("Overlay", "Tags")) {
                extraWidth += 31;
            }
            else {
                extraWidth += 31;
            }
            check++;
        }
        if (modules.getButton("Overlay", "Tags")) {
            if (check != 0) {
                drawCenteredText("[TAGS]", extraWidth, 7, 1, labelColor, true);
                tagsX = extraWidth;
            }
            else {
                drawCenteredText("[TAGS]", extraWidth + 4, 7, 1, labelColor, true);
                tagsX = extraWidth;
                extraWidth += 4;
            }
            check++;
            if (modules.getButton("Overlay", "Session")) {
                extraWidth += 44;
            }
            else {
                extraWidth += 36;
            }
        }
        if (modules.getButton("Overlay", "Session")) {
            if (check != 0) {
                drawCenteredText("[SESSION]", extraWidth, 7, 1, labelColor, true);
                sessionX = extraWidth;
                extraWidth += 43;
            }
            else {
                sessionX = extraWidth + 12;
                drawCenteredText("[SESSION]", extraWidth + 12, 7, 1, labelColor, true);
                extraWidth += 55;
            }
            check++;
        }
        if (modules.getButton("Overlay", "Ping")) {
            pingX = extraWidth;
            if (check == 0) {
                drawCenteredText("[PING]", extraWidth + 2, 7, 1, labelColor, true);
            }
            else {
                drawCenteredText("[PING]", extraWidth, 7, 1, labelColor, true);
            }
        }

        // Player data
        int interval = 1;
        for (String data : queueData.values()) {
            int y = 9 + (11 * interval++);
            String displayName;
            if (data.startsWith("NICK") && getBedwarsStatus() == 1) {
                displayName = client.colorSymbol + "e" + client.util.strip(getData(data, 7));
            }
            else {
                displayName = getData(data, 7);
            }
            client.render.text(getData(data, 5), 8, y, 1, -1, true);
            client.render.text(displayName, 9 + stringWidth, y, 1, -1, true);
            drawCenteredText(getData(data, 1), 127 + stringWidth, y, 1, -1, true);
            drawCenteredText(getData(data, 3), 164 + stringWidth, y, 1, -1, true);
            if (modules.getButton("Overlay", "Winstreaks")) {
                drawCenteredText(getData(data, 2), wsX, y, 1, -1, true);
            }
            if (modules.getButton("Overlay", "Tags")) {
                drawCenteredText(getData(data, 6), tagsX, y, 1, -1, true);
            }
            if (modules.getButton("Overlay", "Session")) {
                drawCenteredText(getData(data, 4), sessionX, y, 1, -1, true);
            }
            if (modules.getButton("Overlay", "Ping")) {
                drawCenteredText(getData(data, 9), pingX, y, 1, -1, true);
            }
        }
    } 
    catch (Exception e) {}
}

int getBoxWidth() {
    int width = 201;
    if (modules.getButton("Overlay", "Winstreaks")) {
        width += client.getFontWidth("[WS]") + 3;
    }
    if (modules.getButton("Overlay", "Tags")) {
        width += client.getFontWidth("[TAGS]") + 3;
    }
    if (modules.getButton("Overlay", "Session")) {
        width += client.getFontWidth("[SESSION]") + 3;
    }
    if (modules.getButton("Overlay", "Ping")) {
        width += client.getFontWidth("[PING]") + 3;
    }
    if (width == 201) {
        width = 199;
    }
    return width;
}

void onWorldJoin(Entity entity) {
    if (client.getPlayer() == null || entity == null) {
        return;
    }
    if (entity == client.getPlayer()) {
        queueData.clear();
        setTeamColor = false;
    }
}

boolean onChat(String msg) {
    msg = client.util.strip(msg);
    if (msg.startsWith("ONLINE: ")) {
        setTeamColor = false;
        scanPlayers = true;
    }
    else if (msg.startsWith("Party Leader:")) {
        String[] split3;
        String[] split = split3 = msg.split("Party Leader: ")[1].split(" ");
        for (String s : split3) {
            if (!s.contains("[")) {
                if (s.length() > 1) {
                    partyMembers.add(s);
                }
            }
        }
        if (partyMembers.size() == partySize) {
            client.print("&eFinished syncing party list.");
        }
    }
    else if (msg.startsWith("Party Moderators:")) {
        String[] split4;
        String[] split = split4 = msg.split("Party Moderators: ")[1].split(" ");
        for (String s : split4) {
            if (!s.contains("[")) {
                if (s.length() > 1) {
                    partyMembers.add(s);
                }
            }
        }
        if (partyMembers.size() == partySize) {
            client.print("&eFinished syncing party list.");
        }
    }
    else if (msg.startsWith("Party Members")) {
        String next = msg.replaceFirst("Party Members", "");
        if (next.startsWith(":")) {
            String[] split5;
            String[] split2 = split5 = msg.split("Party Members: ")[1].split(" ");
            for (String s2 : split5) {
                if (!s2.contains("[")) {
                    if (s2.length() > 1) {
                        partyMembers.add(s2);
                    }
                }
            }
            client.print("&eFinished syncing party list.");
        }
        else if (next.startsWith(" (")) {
            partySize = Integer.parseInt(next.replace(" (", "").replace(")", ""));
        }
    }
    else if (msg.startsWith("You are not currently")) {
        client.print("&eYou're not in a party!");
        partyMembers.clear();
    }
    return true;
}

boolean onPacketSent(CPacket packet) {
    if (packet instanceof C01) {
        if (((C01) packet).message.equals("/psync")) {
            client.chat("/pl");
            return false;
        }
    }
    return true;
}

void onPreUpdate() { // where the requests will be sent
    if (modules.getButton("Overlay", "Clear data")) {
        queueData.clear();
        client.print("&7Cleared &b" + encounters.size() + " &7player data.");
        encounters.clear();
        modules.setButton("Overlay", "Clear data", false);
        setTeamColor = false;
        partyMembers.clear();
        client.chat("/who");
    }

    if (!canProcess()) {
        return;
    }

    try {
        if (client.getScreen().isEmpty()) {
            if (prevMode != modules.getSlider("Overlay", "Sort by")) {
                sortByStats();
                prevMode = (int) modules.getSlider("Overlay", "Sort by");
            }
        }
        
        if (scanPlayers || getBedwarsStatus() == 1) {
            if (!scanPlayers){
                List<String> loadedUUIDs = new ArrayList<>();
                Iterator<NetworkPlayer> iterator = client.getWorld().getNetworkPlayers().iterator();
                while (iterator.hasNext()) {
                    NetworkPlayer networkPlayer = iterator.next();
                    if (networkPlayer.getUUID().replace("-", "").equals(client.getPlayer().getNetworkPlayer().getUUID().replace("-", "")) && !modules.getButton("Overlay", "Show yourself")) {
                        iterator.remove();
                    }
                    else if (partyMembers.contains(networkPlayer.getName())) {
                        iterator.remove();
                    }
                    else {
                        loadedUUIDs.add(networkPlayer.getUUID().replace("-", ""));
                    }
                }
                
                Iterator<Map.Entry<String, String>> it = queueData.entrySet().iterator();
                while (it.hasNext()) {
                    String uuid = it.next().getKey();
                    if (!loadedUUIDs.contains(uuid)) {
                        it.remove();
                    }
                }

            }
        
            for (NetworkPlayer networkPlayer : client.getWorld().getNetworkPlayers()) {
                if (networkPlayer.getUUID().equals(client.getPlayer().getNetworkPlayer().getUUID()) && !modules.getButton("Overlay", "Show yourself")) {
                    continue;
                }
                if ((networkPlayer.getDisplayName().startsWith("ยงc") || !networkPlayer.getDisplayName().contains("ยง")) && networkPlayer.getPing() != 1) { // bot check
                    continue;
                }
                String uuid = networkPlayer.getUUID().replace("-", "");
                if (queueData.containsKey(uuid)) {
                    continue;
                }
                if (partyMembers.contains(networkPlayer.getName())) {
                    continue;
                }
                queueData.put(uuid, "LOAD|1|" + networkPlayer.getDisplayName());
                client.async(() -> {
                    String playerStats = getHypixelStats(uuid, hypixelApiKey, networkPlayer);
                    if (encounters.containsKey(uuid)) {
                        int count = encounters.get(uuid);
                        encounters.put(uuid, ++count);
                        playerStats = setEncounters(playerStats, count);
                    } 
                    else {
                        encounters.put(uuid, 1);
                    }

                    if (getBedwarsStatus() == 2) {
                        playerStats = setName(playerStats, networkPlayer.getDisplayName().substring(8));
                    }
                    queueData.put(uuid, playerStats);
                    sortByStats();
                });
            }
        }
        if (modules.getButton("Overlay", "Teams")) {
            if (getBedwarsStatus() == 2 && !setTeamColor) {
                for (int i = 0; i < client.getWorld().getNetworkPlayers().size(); i++) {
                    NetworkPlayer networkPlayer = client.getWorld().getNetworkPlayers().get(i);
                    for (Map.Entry<String, String> entry : queueData.entrySet()) {
                        String data = entry.getValue();
                        if (!entry.getKey().equals(networkPlayer.getUUID().replace("-", ""))) {
                            continue;
                        }
                        String displayName = networkPlayer.getDisplayName();
                        if (!modules.getButton("Overlay", "Team prefix")) {
                            displayName = displayName.substring(8);
                        }
                        queueData.put(entry.getKey(), setName(data, displayName));
                    }
                }
                if (modules.getButton("Overlay", "Sort by team")) {
                    sortByTeams();
                }
                setTeamColor = true;
            }
        }
        scanPlayers = false;
    } catch (Exception e) {}
}

void sortByStats() {
    List<Map.Entry<String, String>> entryList = new ArrayList<>(queueData.entrySet());

    if (modules.getSlider("Overlay", "Sort mode") == 0)
        entryList.sort(Comparator.comparing((Map.Entry<String, String> entry) -> entry.getValue().startsWith("NICK")).thenComparing(entry -> getPriority(entry.getValue())).reversed());
    else {
        entryList.sort(Comparator.comparing((Map.Entry<String, String> entry) -> entry.getValue().startsWith("NICK")).thenComparing(entry -> getPriority(entry.getValue())));
    }
    
    queueData.clear();
    entryList.forEach(entry -> queueData.put(entry.getKey(), entry.getValue()));
}

void sortByTeams() {
    List<Map.Entry<String, String>> entryList = new ArrayList<>(queueData.entrySet());

    if (modules.getSlider("Overlay", "Sort mode") == 0) {
        entryList.sort(Comparator.comparing((Map.Entry<String, String> entry) -> getData(entry.getValue(), 7).substring(0, 2)).reversed());
    } else {
        entryList.sort(Comparator.comparing((Map.Entry<String, String> entry) -> getData(entry.getValue(), 7).substring(0, 2)));
    }
    
    queueData.clear();
    entryList.forEach(entry -> queueData.put(entry.getKey(), entry.getValue()));
}

void drawCenteredText(String text, float x, float y, double scale, int color, boolean shadow) {
    client.render.text(text, x - client.getFontWidth(text) / 2, y, scale, color, shadow);
}

void drawOutlineRect(int posX, int posY, int posX2, int posY2) {
    // Main rect
    client.render.rect(posX, posY, posX2, posY2, backgroundColor);

    // Outline
    client.render.rect(posX - 1, posY - 1, posX2 + 1, posY, getChroma(2)); // Top
    client.render.rect(posX - 1, posY2, posX2 + 1, posY2 + 1, getChroma(2)); // Bottom
    client.render.rect(posX - 1, posY, posX, posY2, getChroma(2)); // Left
    client.render.rect(posX2, posY, posX2 + 1, posY2, getChroma(2)); // Right
}

int getChroma(long speed) {
    float hue = client.time() % (15000L / speed) / (15000f / speed);
    return Color.getHSBColor(hue, 1f, 1f).getRGB();
}

boolean isNull() {
    return client.getPlayer() == null || client.getWorld() == null;
}

boolean canProcess() {
    return !isNull() && client.getServerIP().contains("hypixel");
}

String toPlayerData(String error, NetworkPlayer networkPlayer) {
    return error + "|1|" + networkPlayer.getDisplayName() + "|" + networkPlayer.getUUID().replace("-", "");
}

String getHypixelStats(String uuid, String hypixelApiKey, NetworkPlayer networkPlayer) {
    if (uuid.isEmpty()) {
        return toPlayerData("ID", networkPlayer);
    }
    
    Request request = new Request("GET", "https://api.hypixel.net/v2/player?key=" + hypixelApiKey + "&uuid=" + uuid.replace("-", ""));
    request.setConnectTimeout(11000);
    request.setReadTimeout(11000);
    
    Response response = request.fetch();
    if (response.code() != 200) {
        client.print("&cError fetching stats: " + response.code());
        return toPlayerData("E", networkPlayer);
    }
    if (response == null) {
        return toPlayerData("E", networkPlayer);
    }
    else if (response.string().equals("{\"success\":true,\"player\":null}")) {
        return toPlayerData("NICK", networkPlayer);
    }
    Json json = response.json();
    if (json == null && response.code() == 200) {
        return toPlayerData("NICK", networkPlayer);
    }
    else if (json == null) {
        return toPlayerData("E", networkPlayer);
    }
    Json profile = json.object("player");
    if (profile == null) {
        return toPlayerData("NICK", networkPlayer);
    }
    String responseUUID = profile.get("uuid", "NICK");
    if (responseUUID.equals("NICK")) {
        return toPlayerData("NICK", networkPlayer);
    }
    String lastLogin = profile.get("lastLogin", "-505");
    Json stats = profile.object("stats");
    Json achievements = profile.object("achievements");
    Json bedwars = null;

    if (stats != null) {
        bedwars = stats.object("Bedwars");
    }

    int bedwarsLevel = 0;
    if (achievements != null) {
        bedwarsLevel = Integer.parseInt(achievements.get("bedwars_level", "0"));
    }

    double finalKills = 0;
    double finalDeaths = 0;
    int winstreak = 0;
    int lossesBedwars = 0;
    
    if (bedwars != null) {
        finalKills = Double.parseDouble(bedwars.get("final_kills_bedwars", "0"));
        finalDeaths = Double.parseDouble(bedwars.get("final_deaths_bedwars", "0"));
        winstreak = Integer.parseInt(bedwars.get("winstreak", "-500"));
        lossesBedwars = Integer.parseInt(bedwars.get("losses_bedwars", "0"));
    }

    if (modules.getButton("Overlay", "Winstreaks")) {
        if (winstreak == -500) {
            winstreak = getEstimatedWinstreak(uuid, antisniperApiKey);
        }
        else {
            winstreak = 0;
        }
    }
    else if (winstreak == -500) {
        winstreak = 0;
    }

    if (!lastLogin.equals("-505")){
        lastLogin = String.valueOf(Math.abs(client.time() - Long.parseLong(lastLogin)) / 1000);
    }

    double fkdr = finalDeaths == 0 ? finalKills : client.util.round(finalKills / finalDeaths, 2);

    String userLanguage = profile.get("userLanguage", "");
    String firstLoginStr = profile.get("firstLogin", "-1");
    String lastLogoutStr = profile.get("lastLogout", "-1");

    long firstLogin = Long.parseLong(firstLoginStr);
    long lastLogout = Long.parseLong(lastLogoutStr);

    boolean party = profile.get("channel", "").equals("PARTY");

    String tags = "";

    if (finalDeaths == 0 || lossesBedwars == 0) {
        tags += client.colorSymbol + "5Z";
    }
    if (!userLanguage.equalsIgnoreCase("ENGLISH") && !userLanguage.isEmpty()) {
        tags += client.colorSymbol + "3L";
    }
    if (firstLogin > 0 && Math.abs(client.time() - firstLogin) < 172800000) {
        tags += client.colorSymbol + "6F";
    }
    if (lastLogout > 0 && Math.abs(client.time() - lastLogout) > 1210000000) {
        tags += client.colorSymbol + "6T";
    }
    if (party) {
        tags += client.colorSymbol + "9P";
    }

    int ping = -1;

    if (modules.getButton("Overlay", "Ping")) {
        ping = getPing(uuid, antisniperApiKey);
    }

    String name = networkPlayer.getDisplayName();

    return "true" + "|" + bedwarsLevel + "|" + winstreak + "|" + fkdr + "|" + lastLogin + "|" + "1" + "|" + tags + "|" + name + "|" + uuid + "|" + ping;
}


int getEstimatedWinstreak(String uuid, String apiKey) {
    if (uuid.isEmpty() || apiKey.isEmpty()) {
        return -3;
    }

    Request request = new Request("GET", "https://api.antisniper.net/v2/player/winstreak?key=" + antisniperApiKey + "&player=" + uuid);
    request.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
    request.setConnectTimeout(10000);
    request.setReadTimeout(10000);

    Response response = request.fetch();
    if (response == null) {
        return -5;
    }
    Json json = response.json();
    if (json == null) {
        return -5;
    }
    return Integer.parseInt(json.get("overall_winstreak", "0"));
}

int getPing(String uuid, String apiKey) {
    if (uuid.isEmpty() || apiKey.isEmpty()) {
        return -3;
    }

    Request request = new Request("GET", "https://api.antisniper.net/v2/player/ping?key=" + antisniperApiKey + "&player=" + uuid);
    request.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
    request.setConnectTimeout(10000);
    request.setReadTimeout(10000);

    Response response = request.fetch();
    if (response.code() != 200) {
        return -4;
    }
    if (response == null) {
        return -5;
    }
    Json json = response.json();
    if (json == null) {
        return -5;
    }
    List<Json> pingData = json.array("data");
    if (pingData.isEmpty()) {
        return -5;
    }
    Json latestData = pingData.get(pingData.size() - 1);
    return Integer.parseInt(latestData.get("avg", "-1"));
}

double getPriority(String data) {
    if (data.isEmpty() || data.startsWith("ID") || data.startsWith("E") || data.startsWith("NICK") || data.startsWith("LOAD")) {
        return 0;
    }
    String[] splitData = data.split("\\|");
    if (splitData != null) {
        if (splitData.length <= 9) {
            return 0;
        }
        int sortBy = (int) modules.getSlider("Overlay", "Sort by");
        if (sortBy == 3) {
            return Long.parseLong(splitData[4]); // session
        }
        else if (sortBy == 4) {
            return Integer.parseInt(splitData[9]); // ping
        }
        else if (sortBy == 5) {
            return Integer.parseInt(splitData[5]); // encounters
        }
        else {
            int bedwarsLevel = Integer.parseInt(splitData[1]);
            double fkdr = Double.parseDouble(splitData[3]);
            if (sortBy == 0) {
                if (fkdr == 0) {
                    return bedwarsLevel;
                }
                return bedwarsLevel * fkdr;
            }
            else if (sortBy == 1) {
                return bedwarsLevel;
            }
            else if (sortBy == 2) {
                return fkdr;
            }
        }
    }
    return 0;
}

String getData(String data, int index) {
    if (data.isEmpty()) {
        return client.colorSymbol + "cE";
    }
    if (data.startsWith("ID")) {
        return client.colorSymbol + "cID";
    }
    if (index != 5 && index != 7 && (data.startsWith("NICK") || data.startsWith("LOAD"))) {
        return client.colorSymbol + "7-";
    }
    String[] splitData = data.split("\\|");
    if (splitData != null) {
        if (!data.startsWith("true") && index == 5) {
            return getEncountersStr(Integer.parseInt(splitData[1]));
        }
        if (!data.startsWith("true") && index == 7) {
            return splitData[2];
        }
        if (!data.startsWith("true") && index == 8) {
            return splitData[3];
        }
        if (index < 0 || index >= splitData.length) {
            return client.colorSymbol + "cE";
        }
        switch (index) {
            case 0:
                return splitData[0];
            case 1: 
                return getStarStr(Integer.parseInt(splitData[1]));
            case 2:
                return getWinStreakStr(Integer.parseInt(splitData[2]));
            case 3:
                return getFKDRStr(Double.parseDouble(splitData[3]));
            case 4:
                return parseSession(Long.parseLong(splitData[4]));
            case 5:
                return getEncountersStr(Integer.parseInt(splitData[5]));
            case 6:
                return splitData[6]; // tags
            case 7:
                return splitData[7]; // name
            case 8:
                return splitData[8]; // uuid
            case 9:
                return getPingStr(Integer.parseInt(splitData[9]));
        }
    }
    return client.colorSymbol + "cE";
}

String setEncounters(String data, int encounters) {
    String[] splitData = data.split("\\|");
    if (splitData != null) {
        if (data.startsWith("NICK|")) {
            splitData[1] = String.valueOf(encounters);
            return String.join("|", splitData);
        }
        if (9 >= splitData.length) {
            return data;
        }
        splitData[5] = String.valueOf(encounters);

        return String.join("|", splitData);
    }
    return data;
}

String setName(String data, String newName) {
    String[] splitData = data.split("\\|");
    if (splitData != null) {
        if (data.startsWith("NICK|")) {
            splitData[2] = newName;
            return String.join("|", splitData);
        }
        if (9 >= splitData.length) {
            return data;
        }
        splitData[7] = newName;

        return String.join("|", splitData);
    }
    return data;
}

String parseSession(long lastLogin) {
    if (lastLogin == -505) {
        return client.colorSymbol + "cAPI";
    }

    String color = lastLogin >= 32400 ? "4" : lastLogin >= 21600 ? "c" : lastLogin >= 10800 ? "6" : lastLogin >= 7560 ? "e" : lastLogin >= 1260 ? "a" : lastLogin >= 330 ? "e" : lastLogin >= 150 ? "c" : "4";
    String parsedString = parseSessionTime(lastLogin);

    return client.colorSymbol + color + parsedString;
}

String getFKDRStr(double fkdr) {
    if (fkdr == 0) {
       return client.colorSymbol + "7" + (int) fkdr; 
    }
    String color = fkdr >= 25 ? "4" : fkdr >= 10 ? "c" : fkdr >= 5 ? "6" : fkdr >= 2.25 ? "e" :fkdr >= 1.2 ? "f" : "7";
    return client.colorSymbol + color + fkdr;
}

String getStarStr(int star) {
    String color = star >= 1000 ? "c" : (star >= 900 ? "5" : (star >= 800 ? "9" : (star >= 700 ? "d" : (star >= 600 ? "4" : (star >= 500 ? "3" : (star >= 400 ? "2" : (star >= 300 ? "b" : (star >= 200 ? "6" : (star >= 100 ? "f" : "7")))))))));
    return client.colorSymbol + color + star;
}

String getEncountersStr(int encounters) {
    String color = encounters >= 6 ? "4" : encounters >= 5 ? "c" : encounters >= 4 ? "6" : encounters >= 2 ? "e" : encounters <= 1 ? "a" : "a";
    return client.colorSymbol + color + encounters;
}

String getWinStreakStr(int winstreak) {
    if (winstreak == 0) {
        return "";
    }
    if (winstreak == -3) {
        return client.colorSymbol + "cKEY";
    }
    String color = winstreak >= 500 ? "4" : winstreak >= 200 ? "c" : winstreak >= 150 ? "6" : winstreak >= 90 ? "e" : winstreak >= 40 ? "f" : "7";
    return client.colorSymbol + color + winstreak;
}

String getPingStr(int ping) {
    if (ping <= 0) {
        return "";
    }
    String color = "e";
    if (ping >= 300) {
        color = "4";
    }
    else if (ping >= 200) {
        color = "c";
    }
    else if (ping >= 170 ) {
        color = "a"; 
    }
    else if (ping >= 120) {
        color = "2";
    }
    else if (ping >= 30) {
        color = "a";
    }
    return client.colorSymbol + color + ping;
}

String parseSessionTime(long sessionTime) {
    long absSeconds = Math.abs(sessionTime);

    long days = absSeconds / (60 * 60 * 24);
    long hours = (absSeconds % (60 * 60 * 24)) / (60 * 60);
    long minutes = (absSeconds % (60 * 60)) / 60;
    long remainingSeconds = absSeconds % 60;
    int length = 0;
    String formattedTime = "";

    if (days > 0) {
        formattedTime += days + "d";
        length++;
    }
    if (hours > 0) {
        formattedTime += hours + "h";
        length++;
    }
    if (minutes > 0 && length <= 1) {
        formattedTime += minutes + "m";
        length++;
    }
    if (remainingSeconds > 0 && length <= 1) {
        formattedTime += remainingSeconds + "s";
        length++;
    }

    return formattedTime;
}

int getBedwarsStatus() { // from autosnipe by pugrilla
    List<String> sidebar = client.getWorld().getScoreboard();
    if (sidebar != null && client.util.strip(sidebar.get(0)).startsWith("BED WARS")) {
        for (String line : sidebar) {
            line = client.util.strip(line);
            String[] parts = line.split("  ");
            if (parts.length > 1) {
                if (parts[1].startsWith("L")) {
                    return 0;
                }
            }
            else if (line.equals("Waiting...") || line.startsWith("Starting in")) {
                return 1;
            }
            else if (line.startsWith("R Red:") || line.startsWith("B Blue:")) {
                return 2;
            }
        }
    }
    return -1;
}

// unloged
