# Queue management simulation app
## Description
Java application for simulation of queue management by multiple parallel threads (parameters given by user). UI done with Java Swing.

## Technical
The application follows the MVC architecture and uses the Strategy design pattern for selecting different management strategies. Thread safe Java classes are used for ensuring synchronization between threads (e.g. BlockingQueue). \
Each simulation session is logged in a .txt file (examples included in repo).
