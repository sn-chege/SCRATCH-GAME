// Main.java
package com.scratchgame;

import com.scratchgame.utils.JsonUtils;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: java -jar scratch-game.jar --config <config-file> --betting-amount <amount>");
            return;
        }

        String configPath = null;
        int betAmount = 0;

        for (int i = 0; i < args.length; i++) {
            if ("--config".equals(args[i])) {
                configPath = args[++i];
            } else if ("--betting-amount".equals(args[i])) {
                betAmount = Integer.parseInt(args[++i]);
            }
        }

        if (configPath == null || betAmount <= 0) {
            System.err.println("Invalid config file path or betting amount.");
            return;
        }

        Config config = JsonUtils.loadConfig(Paths.get(configPath));
        if (config == null) {
            System.err.println("Failed to load configuration.");
            return;
        }

        Game game = new Game(config);
        GameResult result = game.play(betAmount);
        System.out.println(result.toJson());
    }
}