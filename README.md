# Siedler von Catan – JavaFX Edition

A modern desktop adaptation of the classic board game "The Settlers of Catan," implemented in Java with JavaFX for an interactive GUI. Play Catan with friends on your computer, trade, build, and conquer to victory!

---

## Features

* 🎲 Full Catan base game logic (dice, trading, bandit, building, resources, victory)
* 🖱️ Mouse-driven JavaFX interface
* 🔄 Trade system with bank and other players
* 🏰 Road and settlement placement logic
* 💡 Visual feedback for all in-game actions

---

## Getting Started

### Prerequisites

* **Java 17** or higher (recommended for JavaFX 17+)
* **[JavaFX SDK](https://gluonhq.com/products/javafx/)** matching your Java version
* **Maven** (for building)

---

### Building

Clone the repository, then build the project with Maven:

```bash
  mvn clean package
```

This will produce a runnable JAR file in the `target/` directory.

---

### Running

**Important:**
JavaFX libraries are *not* bundled into the jar file by default due to licensing.
You must **download the JavaFX SDK** and add it to your Java classpath when running.

1. **Download** the JavaFX SDK for your platform from:
   [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/)

2. **Extract** the SDK somewhere on your system.

3. **Run the game** using the following command (update path as needed):

```bash
  java --module-path /path/to/javafx-sdk-XX/lib --add-modules javafx.controls,javafx.fxml -jar target/catan-1.0-SNAPSHOT.jar
```

Replace `/path/to/javafx-sdk-XX/lib` with the actual path to your extracted JavaFX SDK.

---

### Developer Notes

* Make sure to set up your IDE to recognize JavaFX libraries for development and debugging.
* For packaging JavaFX inside the jar (not default), consider using Maven plugins like `javafx-maven-plugin` or `jlink`.

---



## Credits

Built by Sophie Lazarjan, Isabella Schwarz, Julian Stengele, Lucius Lechner \
Inspired by Klaus Teuber's "Die Siedler von Catan".
