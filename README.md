# SCRATCH-GAME

A simple scratch card game implemented in Java, using a configurable JSON-based setup for symbols, probabilities, and win conditions. (Requirements defined in detail in "problem_description.md")

## Features

- Configurable matrix size, symbol probabilities, and win combinations
- Bonus symbol mechanics with different reward impacts
- Randomized matrix generation with fair weighting
- Simple reward calculation logic
- JSON-based result output

## Getting Started

### Prerequisites

- Java 8 (or higher)
- Maven

### Build

```bash
mvn clean install
```

### Run

```bash
java -jar target/scratch-game.jar --config config.json --betting-amount 100
```

## Configuration

Edit `config.json` in the root directory to customize:
- Grid dimensions (rows and columns)
- Symbol definitions and types (standard or bonus)
- Probabilities for each symbol in each cell
- Winning combinations and reward multipliers

## Tests

Run unit tests with:

```bash
mvn test
```

Tests cover:
- Config loading
- Matrix generation
- Reward calculation
- Bonus logic