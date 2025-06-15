# STAG - Simple Text Adventure Game Engine

A versatile, multiplayer text-based adventure game engine built in Java using Object-Oriented Programming principles. This game engine can run any text adventure game that conforms to the specified configuration format, supporting multiple simultaneous players through socket-based networking.

## 🎮 Features

### Core Gameplay
- **Multiplayer Support**: Multiple players can play simultaneously using different usernames
- **Dynamic Game Loading**: Games are loaded from configuration files (entities and actions)
- **Flexible Command Interpretation**: Natural language processing with case insensitivity and partial commands
- **Real-time Networking**: Socket-based client-server architecture

### Game Mechanics
- **Inventory System**: Players can collect, carry, and drop artifacts
- **Location-based Movement**: Navigate between interconnected game locations
- **Custom Actions**: Extensible action system defined through XML configuration
- **Health System**: Player health management with consequences for death
- **Entity Interactions**: Rich interactions with characters, furniture, and artifacts

### Built-in Commands
- **`look`** - Examine current location and its contents
- **`inventory` / `inv`** - List items in player's inventory
- **`get <item>`** - Pick up an artifact from current location
- **`drop <item>`** - Drop an item from inventory to current location
- **`goto <location>`** - Move to a connected location
- **`health`** - Check current health level

## 🏗️ Architecture

### Object-Oriented Design
The game engine is built using OOP principles with the following key components:

- **GameServer**: Main server class handling client connections and command processing
- **GameEntity**: Abstract base class for all game entities
- **Player**: Represents individual players with inventory and location
- **Location**: Game areas with paths, entities, and environmental details
- **GameAction**: Custom actions defined in configuration files

### Data Structures
- Uses Java Collections Framework (avoiding arrays/ArrayLists as per constraints)
- Entity management through unique identifier system
- Graph-based location connectivity

## 📁 Project Structure

```
src/
├── main/java/edu/uob/
│   ├── GameServer.java          # Main server implementation
│   ├── GameEntity.java          # Abstract entity class
│   ├── Player.java              # Player management
│   ├── Location.java            # Location handling
│   ├── GameAction.java          # Action processing
│   └── [Other entity classes]
├── test/                        # JUnit test files
├── libs/                        # External libraries (JPGD parser)
└── config/
    ├── basic-entities.dot       # Basic game entities
    ├── basic-actions.xml        # Basic game actions
    ├── extended-entities.dot    # Extended game scenario
    └── extended-actions.xml     # Extended actions
```

## 🚀 Getting Started

### Prerequisites
- Java JDK 8 or higher
- Maven 3.6+

### Running the Game

1. **Start the Server**:
   ```bash
   ./mvnw exec:java@server
   ```

2. **Connect a Client** (in a new terminal):
   ```bash
   ./mvnw exec:java@client -Dexec.args="your_username"
   ```

3. **Run Tests**:
   ```bash
   ./mvnw test
   ```

### Configuration Files

The game engine loads scenarios from two configuration files:

- **Entities File** (`.dot` format): Defines locations, characters, artifacts, and furniture using GraphViz DOT notation
- **Actions File** (`.xml` format): Defines custom game actions with triggers, subjects, and effects

## 🎯 Command Examples

### Basic Commands
```
look                    # Examine current location
inv                     # Check inventory
get sword               # Pick up sword
drop shield             # Drop shield
goto forest             # Move to forest location
health                  # Check health level
```

### Custom Actions (game-specific)
```
open door with key      # Use key to open door
chop tree with axe      # Chop down tree using axe
drink potion            # Consume health potion
attack goblin with sword # Combat action
```

### Natural Language Flexibility
The engine supports flexible command input:
```
# These are all equivalent:
chop tree with axe
please chop the tree using the axe
use axe to chop tree
chop the tree with the axe please
```

## 🔧 Technical Constraints

This project was developed under specific constraints to encourage exploration of different programming approaches:

- ❌ No Lambda expressions
- ❌ No Arrays or ArrayLists
- ❌ No Ternary operators
- ❌ No String concatenation (+ operator)
- ❌ No unqualified method calls
- ✅ All alternatives implemented using Java Collections and StringBuilder

## 🌐 Networking

- **Protocol**: Socket-based TCP communication
- **Message Format**: `username: command`
- **Concurrent Players**: Multiple simultaneous connections supported
- **State Management**: Per-player game state maintenance

## 🏥 Health System

- Players start with 3 health points
- Health can be affected by actions (potions, traps, etc.)
- Death results in:
  - Loss of all inventory items (dropped at death location)
  - Respawn at start location
  - Health restored to maximum

## 🧪 Testing

The project includes comprehensive JUnit tests covering:
- Entity loading and parsing
- Action interpretation and execution
- Command processing and validation
- Multiplayer functionality
- Error handling and edge cases

Run the code quality checker:
```bash
./mvnw exec:java@strange -Dexec.args=src/main/java/edu/uob/GameServer.java
```

## 📝 Game Creation

### Creating Custom Games

1. **Design your world** in DOT format for entities
2. **Define actions** in XML format
3. **Test configuration** files with the provided parsers
4. **Load and play** your custom game

### Entity Types
- **Locations**: Rooms, areas, environments
- **Artifacts**: Collectible items
- **Furniture**: Non-collectible location features
- **Characters**: NPCs and creatures
- **Players**: Human players

## 🤝 Contributing

This project was developed as an academic assignment with specific requirements and constraints. The codebase demonstrates:

- Socket programming in Java
- XML and DOT file parsing
- Object-oriented design patterns
- Natural language command processing
- Multiplayer game state management

## 📚 Dependencies

- **JPGD Library**: For parsing DOT entity files
- **JAXP**: Java API for XML Processing
- **JUnit**: Testing framework
- **Maven**: Build and dependency management

## 🎯 Learning Outcomes

This project demonstrates proficiency in:
- Advanced Java programming concepts
- Network programming with sockets
- File parsing and data structure management
- Object-oriented design and inheritance
- Natural language processing basics
- Concurrent programming for multiplayer support

---

*This project represents a complete implementation of a text adventure game engine, showcasing modern Java development practices while working within specific technical constraints.*