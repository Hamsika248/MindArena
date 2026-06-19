# 🧠 MindArena — Console Quiz Application

> *Test Your Knowledge. Claim the Arena.*

MindArena is a Java console-based quiz application that challenges players with timed questions across multiple categories and difficulty levels. Built with core Java concepts — OOP, multithreading, file I/O, and collections.

---

## ✨ Features

- 🎯 **5 Categories** — Java, Python, Data Structures, Computer Networks, General Knowledge
- ⚡ **3 Difficulty Levels** — Easy (5 pts), Medium (10 pts), Hard (15 pts)
- ⏱️ **15-second Countdown Timer** per question — colour changes green → yellow → red as time runs out
- 🔀 **Questions shuffle** every session for a fresh experience
- 🏆 **Scoreboard** with streak tracking and performance rating
- 🎨 **Colour-coded console UI** using ANSI escape codes
- 📂 **External questions file** — easily add your own questions to `resources/questions.txt`

---

## 📸 Preview

```
  ███╗   ███╗██╗███╗   ██╗██████╗      █████╗ ██████╗ ███████╗███╗   ██╗ █████╗
  ...
        🧠  Test Your Knowledge. Claim the Arena.  🧠

  Enter your name: Hamsika

  ── Choose a Category ───────────────────
  0) All Categories
  1) Java
  2) Python
  3) Data Structures
  4) Computer Networks
  5) General Knowledge

  Q1 / 5   [Java]   (MEDIUM)

  Which Java collection maintains insertion order and allows duplicates?

    A)  HashSet
    B)  TreeSet
    C)  ArrayList
    D)  HashMap

  ⏱  Time remaining: 12s
  Your answer: C
  ✔  Correct! +10 pts
```

---

## 🗂️ Project Structure

```
MindArena/
├── src/
│   └── mindarena/
│       ├── Main.java             # Entry point — welcome screen, menu, game loop
│       ├── Question.java         # Model — text, options, answer, difficulty, points
│       ├── QuestionLoader.java   # Reads & parses questions.txt, supports filtering
│       ├── QuizEngine.java       # Core game loop — displays questions, handles input
│       ├── QuizTimer.java        # 15s countdown on a background thread
│       ├── ScoreBoard.java       # Tracks score, streaks, and question history
│       └── Colour.java           # ANSI colour constants
├── resources/
│   └── questions.txt             # All quiz questions (easily extendable)
├── out/                          # Compiled .class files (auto-generated)
├── run.sh                        # One-click compile + run script
└── README.md
```

---

## 🚀 How to Run

### Prerequisites
- Java 17 or higher — [Download here](https://adoptium.net/)

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/Hamsika248/MindArena.git
cd MindArena

# 2. Run using the script (compiles + runs automatically)
./run.sh
```

### Manual compile & run

```bash
mkdir -p out
javac -d out src/mindarena/*.java
java -cp out mindarena.Main
```

---

## ➕ Adding Your Own Questions

Open `resources/questions.txt` and add a new question block in this format:

```
CATEGORY|DIFFICULTY|Your question text here
A) Option A
B) Option B
C) Option C
D) Option D
ANSWER: B
```

- `DIFFICULTY` must be `EASY`, `MEDIUM`, or `HARD`
- Leave a **blank line** between question blocks
- Lines starting with `#` are treated as comments

---

## 🧩 Key Concepts Used

| Concept | Where |
|---|---|
| OOP (Encapsulation, Abstraction) | `Question.java`, `ScoreBoard.java` |
| Multithreading | `QuizTimer.java` — daemon thread for countdown |
| File I/O | `QuestionLoader.java` — BufferedReader |
| Collections & Generics | `ArrayList`, `List<Question>` throughout |
| Stream API | Filtering by category and difficulty in `Main.java` |
| Switch Expressions | `Question.getPoints()`, `QuizEngine` colour logic |
| Records | `ScoreBoard.Entry` — immutable history entry |
| Exception Handling | `QuestionLoader`, `Main`, `QuizEngine` |

---

## 📊 Scoring

| Difficulty | Points per Correct Answer |
|---|---|
| Easy | 5 pts |
| Medium | 10 pts |
| Hard | 15 pts |
| Timed Out / Wrong | 0 pts |

### Performance Ratings

| Score % | Rating |
|---|---|
| 100% | 🌟 Perfect! Genius! |
| 80–99% | 🔥 Excellent! |
| 60–79% | 👍 Good job! |
| 40–59% | 📚 Keep practising |
| Below 40% | 💪 Don't give up! |

---

## 🛠️ Tech Stack

- **Language:** Java 21
- **Build:** Manual `javac` (no Maven/Gradle — intentionally simple)
- **UI:** ANSI escape codes for colour console output
- **Storage:** Plain text file for questions

---

## 👩‍💻 Author

**Hamsika Kancharla**  
[GitHub](https://github.com/Hamsika248)
