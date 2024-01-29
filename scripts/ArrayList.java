List<String> mainModules = Arrays.asList("BedAura", "KillAura", "Scaffold", "Bhop", "AirVelo", "FBFly", "Fences", "InvManager");
List<String> showModules = new ArrayList<>();

int offset = 10;
int top_x = -2;
int top_y = 5;
long startTime = System.currentTimeMillis();

void onRenderTick(float partialTicks) {
    int[] size = client.getDisplaySize();
    updateShowModules();

    showModules.sort((o1, o2) -> Integer.compare(client.getFontWidth(o2), client.getFontWidth(o1)));

    int y = top_y;
    for (String module : showModules) {
        int moduleWidth = client.getFontWidth(module);
        renderGradientText(module, (top_x + size[0]) - moduleWidth - 3, y, new Color(242, 26, 235), new Color(92, 4, 231));
        y += 10;
    }
}

void updateShowModules() {
    for (String module : mainModules) {
        boolean isEnabled = modules.isEnabled(module);
        boolean isDisplayed = showModules.contains(module);

        if (isEnabled && !isDisplayed) {
            showModules.add(module);
        } else if (!isEnabled && isDisplayed) {
            showModules.remove(module);
        }
    }
}

void renderGradientText(String text, int x, int y, Color startColor, Color endColor) {
    long elapsed = System.currentTimeMillis() - startTime;
    double offset = 0.5 * (Math.sin(elapsed * 0.001 - y * 0.05) + 1); // Inverted phase adjustment
    
    Color currentColor = blendColors(startColor, endColor, offset);
    client.render.text(text, x, y, currentColor.getRGB(), true);
}

Color blendColors(Color color1, Color color2, double ratio) {
    int r = clamp((int) (color1.getRed() * ratio + color2.getRed() * (1 - ratio)), 0, 255);
    int g = clamp((int) (color1.getGreen() * ratio + color2.getGreen() * (1 - ratio)), 0, 255);
    int b = clamp((int) (color1.getBlue() * ratio + color2.getBlue() * (1 - ratio)), 0, 255);
    return new Color(r, g, b);
}

int clamp(int val, int min, int max) {
    return Math.max(min, Math.min(max, val));
}

// by mic and yogi
