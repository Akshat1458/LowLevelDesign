# LLD Interview Review — Tic-Tac-Toe

## Overall Verdict: 6.5 / 10
Solid foundation with the right instincts (Strategy pattern, separation of concerns), but has gaps that an interviewer would probe on.

---

## ✅ What You Did Well

### 1. Strategy Pattern — Good Use of OOP
Using `PlayerStrategy` interface with `HumanStrategy` and `BotStrategy` is exactly what interviewers want to see. It shows you understand **Open/Closed Principle** — adding a new player type (e.g., `MinimaxStrategy`) requires zero changes to existing code.

### 2. Separation of Concerns
- `Board` owns the grid and game state logic
- `Player` delegates move selection to strategy
- `Game` orchestrates the flow
- `Main` is just a driver

### 3. Working Code
The game runs end-to-end, handles draws, alternates players correctly.

---

## 🔴 Issues an Interviewer Would Flag

### 1. Board Has Too Many Responsibilities (SRP Violation)

`Board` currently handles:
- Grid management (`makeMove`, `isValidMove`)
- Win detection (`getWinner`)
- Game state (`isGameFinished`)
- Display (`display`)

That's **4 responsibilities**. Separate these:

| Class | Responsibility |
|-------|---------------|
| `Board` | Grid storage, `makeMove`, `isValidMove` |
| `WinChecker` / `GameEvaluator` | `getWinner`, `isGameFinished` |
| `BoardPrinter` / `DisplayService` | `display` |

### 2. Missing `GameStatus` Enum

The design diagram shows a `GameStatus` enum (`IN_PROGRESS | X_WON | O_WON | DRAW`), but it's never implemented. There is a **gap between design and code**.

### 3. Win-Check is Hardcoded to 3×3

`getWinner()` uses hardcoded indices like `grid[row][0]`, `grid[row][1]`, `grid[row][2]`. Even though the constructor accepts `rows` and `columns`, the win logic **breaks for any size other than 3×3**.

Either:
- Hardcode `3` and remove the parameters, or
- Make `getWinner()` use loops based on `rows`/`columns`

### 4. `BotStrategy` Returns `null` — Will Crash at Runtime

```java
public Cell getMove(Board board){
    return null;  // NPE when used
}
```

At least implement a **random valid move**.

### 5. No Access Modifiers on Fields

```java
Scanner scanner;  // package-private, should be private
```

Always use `private` for fields — basic encapsulation.

### 6. `Player.getMove()` Has UI Concern

```java
System.out.println("MOVE FOR " + symbol);  // UI concern in a model class
```

The `Player` class shouldn't print to console — that belongs in `Game` or a display layer.

### 7. Both Players Share the Same Strategy Instance

```java
PlayerStrategy playerStrategy = new HumanStrategy(scanner);
Game game = new Game(playerStrategy, playerStrategy);  // same object!
```

Each player should have its own strategy instance.

---

## 🟡 Design Improvements to Discuss

### 1. Support N Players (Not Just 2)
Currently `Game` hardcodes `playerX` and `playerO`. Use a `List<Player>` instead:
```java
private List<Player> players;
private int currentPlayerIndex;
```

### 2. Undo Support
Currently `makeMove` replaces the cell. There's no move history. Adding a `Stack<Move>` would enable undo.

### 3. Missing `@Override` Annotations
`HumanStrategy` and `BotStrategy` implement the interface method but don't use `@Override`.

### 4. No Input Validation in `makeMove`
`Board.makeMove()` blindly places the cell without checking `isValidMove()`. Defensive programming:
```java
public void makeMove(Cell cell) {
    if (!isValidMove(cell)) {
        throw new IllegalArgumentException("Invalid move");
    }
    grid[cell.getRow()][cell.getColumn()] = cell;
}
```

---

## 📊 Scorecard

| Criteria | Score | Notes |
|----------|-------|-------|
| **Design Patterns** | 8/10 | Strategy pattern ✅ |
| **SOLID Principles** | 5/10 | SRP violated in Board, OCP good |
| **Encapsulation** | 6/10 | Missing `private`, mutable Cell |
| **Extensibility** | 4/10 | Hardcoded 3×3, hardcoded 2 players |
| **Error Handling** | 3/10 | No validation in makeMove, BotStrategy returns null |
| **Code Quality** | 7/10 | Clean, readable, consistent style |
| **Working Demo** | 9/10 | Fully functional game |

---

## 🎯 Priority Fixes

1. Implement `BotStrategy` — even a random-move bot shows completeness
2. Add `GameStatus` enum — match the design diagram
3. Fix win-check to work for N×N — or remove the `rows`/`columns` parameters
4. Move display logic out of `Board` — separate presentation from data
5. Use `List<Player>` instead of `playerX`/`playerO` — shows extensibility
6. Add `@Override` annotations — basic Java convention
