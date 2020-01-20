# Project: Mobile Agents

## Collaborators: Danan High, Hector Castillo Martinez

### Introduction 
Design a simulation that represents a wireless sensor network that upon discovery of a heated
node will map out fire continuously. Sensor node network is modeled as a planar graph with vertices representing
the sensor and is a independent concurrent thread. The edges represent a communication link among two
sensors. Mobile agents sit on top of the sensor and are represented as a green stroke on a node, these are also
independent concurrent threads. Mobile agent clones when heat is found and fire is represented as red node and any node
adjacent are considered and are yellow. Base station is a square and normal nodes are blue. Base Station keeps track
of agents created and that list is displayed along side simulation. Mobile agents have unique IDs.

#### Sensor Network and Fire Behavior
A sensor/node can only communicate with its neighbors and with mobile agent that currently exists on it.
Spreading of the fire is is through random intervals to mimic real life spreading. Fire starts at one location and 
spreads throughout the graph and continues until each node is on fire. When node turns red it can communicate one last 
time and its neighbors turn yellow.
#### Mobile Agent Behavior
One initial agent exists and randomly walks the sensor network until heated is found. Can only communicate to node
it is presently on. Once yellow node is found, the agent stops walking and is cloned to its current node's
neighbors. No two agents can coexist an a single a node. Mobile agent dies when its current node catches on fire.

### Contributions
Danan: Node, MobileAgent, Message, SensorObject, GraphReader

Hector: Coordinator, Display

### Usage
When program is run, user will be prompted to select a map text file. If no file is chosen program will close. Once file
is selected GUI will launch and user must hit start button for simulation to begin. 
### Project Assumptions
Once base station catches on fire agent list printed in GUI will stop updating, since base station isn't able to 
communicate. There is always only one base station and one fire. Mobile agents do not know fire exists so it just walks until
it happens to find the fire. Test files are txt files. 
## Versions
Jar is MobileAgents.jar and is in root directory.
#### V1
 Working version that fits specs.
## Docs
 Documentation is in doc folder.
## Status
### Implemented Features
 Base station's agent list is printed on the GUI. Start button is present so that user can decide when to start simulation.
 FileChooser allows user to test any number of graphs.
### Known Issues
none
