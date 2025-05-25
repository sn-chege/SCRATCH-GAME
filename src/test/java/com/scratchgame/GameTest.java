package com.scratchgame;

import com.scratchgame.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    public void testConfigLoadsCorrectly() {
        Config config = JsonUtils.loadConfig(Paths.get("config.json"));
        assertNotNull(config);
        assertTrue(config.columns > 0);
        assertTrue(config.rows > 0);
        assertFalse(config.symbols.isEmpty());
    }

    @Test
    public void testMatrixGeneration() {
        Config config = JsonUtils.loadConfig(Paths.get("config.json"));
        Game game = new Game(config);
        String[][] matrix = game.play(100).matrix;

        assertEquals(config.rows, matrix.length);
        assertEquals(config.columns, matrix[0].length);
    }

    @Test
    public void testRewardIsNonNegative() {
        Config config = JsonUtils.loadConfig(Paths.get("config.json"));
        Game game = new Game(config);
        GameResult result = game.play(100);

        assertTrue(result.reward >= 0);
    }

    @Test
    public void testBonusSymbolIsValid() {
        Config config = JsonUtils.loadConfig(Paths.get("config.json"));
        Game game = new Game(config);
        GameResult result = game.play(100);

        if (result.applied_bonus_symbol != null) {
            assertTrue(config.symbols.containsKey(result.applied_bonus_symbol));
        }
    }
}
