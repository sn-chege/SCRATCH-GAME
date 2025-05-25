package com.scratchgame;

import java.util.List;
import java.util.Map;

public class Config {
    public int columns = 3;
    public int rows = 3;
    public Map<String, Symbol> symbols;
    public Probabilities probabilities;
    public Map<String, WinCombination> win_combinations;

    public static class Probabilities {
        public List<CellProbability> standard_symbols;
        public BonusProbability bonus_symbols;
    }

    public static class CellProbability {
        public int column;
        public int row;
        public Map<String, Integer> symbols;
    }

    public static class BonusProbability {
        public Map<String, Integer> symbols;
    }
}