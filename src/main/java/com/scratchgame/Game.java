package com.scratchgame;

import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private final Config config;
    private final Random random = new Random();

    public Game(Config config) {
        this.config = config;
    }

    public GameResult play(int betAmount) {
        String[][] matrix = generateMatrix();

        Map<String, List<String>> winningCombinations = evaluateWinningCombinations(matrix);
        double reward = calculateReward(matrix, betAmount, winningCombinations);

        String bonusSymbol = null;
        if (!winningCombinations.isEmpty()) {
            bonusSymbol = applyBonus(matrix, reward);
            reward = applyBonusReward(bonusSymbol, reward);
        }

        GameResult result = new GameResult();
        result.matrix = matrix;
        result.reward = reward;
        result.applied_winning_combinations = winningCombinations;
        result.applied_bonus_symbol = bonusSymbol;
        return result;
    }

    private String[][] generateMatrix() {
        int rows = config.rows;
        int cols = config.columns;
        String[][] matrix = new String[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                matrix[r][c] = selectSymbolForCell(c, r);
            }
        }

        return matrix;
    }

    private String selectSymbolForCell(int col, int row) {
        Map<String, Integer> probabilities = null;

        for (Config.CellProbability cell : config.probabilities.standard_symbols) {
            if (cell.column == col && cell.row == row) {
                probabilities = cell.symbols;
                break;
            }
        }

        if (probabilities == null) {
            probabilities = config.probabilities.standard_symbols.get(0).symbols;
        }

        return weightedRandomChoice(probabilities);
    }

    private String applyBonus(String[][] matrix, double reward) {
        List<String> bonusCandidates = new ArrayList<>();
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                String bonus = weightedRandomChoice(config.probabilities.bonus_symbols.symbols);
                if (!"MISS".equals(bonus)) {
                    matrix[r][c] = bonus;
                    return bonus;
                }
            }
        }
        return "MISS";
    }

    private double applyBonusReward(String bonusSymbol, double reward) {
        Symbol bonus = config.symbols.get(bonusSymbol);
        if (bonus == null || bonus.type != SymbolType.bonus) return reward;

        switch (bonus.impact) {
            case multiply_reward:
                return reward * bonus.reward_multiplier;
            case extra_bonus:
                return reward + (bonus.extra != null ? bonus.extra : 0);
            case miss:
            default:
                return reward;
        }
    }

    private Map<String, List<String>> evaluateWinningCombinations(String[][] matrix) {
        Map<String, List<String>> result = new HashMap<>();
        Map<String, Integer> symbolCounts = new HashMap<>();

        for (String[] row : matrix) {
            for (String s : row) {
                if (config.symbols.get(s).type == SymbolType.standard) {
                    symbolCounts.put(s, symbolCounts.getOrDefault(s, 0) + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : symbolCounts.entrySet()) {
            String symbol = entry.getKey();
            int count = entry.getValue();
            List<String> applied = new ArrayList<>();

            for (Map.Entry<String, WinCombination> combo : config.win_combinations.entrySet()) {
                WinCombination wc = combo.getValue();
                if ("same_symbols".equals(wc.when) && count >= wc.count) {
                    applied.add(combo.getKey());
                } else if ("linear_symbols".equals(wc.when)) {
                    for (List<String> area : wc.covered_areas) {
                        boolean match = true;
                        for (String pos : area) {
                            String[] split = pos.split(":");
                            int r = Integer.parseInt(split[0]);
                            int c = Integer.parseInt(split[1]);
                            if (!matrix[r][c].equals(symbol)) {
                                match = false;
                                break;
                            }
                        }
                        if (match) {
                            applied.add(combo.getKey());
                            break;
                        }
                    }
                }
            }

            if (!applied.isEmpty()) {
                result.put(symbol, applied);
            }
        }

        return result;
    }

    private double calculateReward(String[][] matrix, int betAmount, Map<String, List<String>> winningCombinations) {
        double totalReward = 0.0;

        for (Map.Entry<String, List<String>> entry : winningCombinations.entrySet()) {
            String symbol = entry.getKey();
            Symbol s = config.symbols.get(symbol);
            double base = betAmount * s.reward_multiplier;
            double multiplier = 1.0;
            for (String comboKey : entry.getValue()) {
                multiplier *= config.win_combinations.get(comboKey).reward_multiplier;
            }
            totalReward += base * multiplier;
        }

        return totalReward;
    }

    private String weightedRandomChoice(Map<String, Integer> weights) {
        int total = weights.values().stream().mapToInt(i -> i).sum();
        int rand = random.nextInt(total);
        int sum = 0;
        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            sum += entry.getValue();
            if (rand < sum) {
                return entry.getKey();
            }
        }
        return weights.keySet().iterator().next(); // Fallback
    }
}