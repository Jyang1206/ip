# UberSuper (Chatbot)

Welcome to **UberSuper**, your friendly assistant for managing tasks and clients.

![App Screenshot](/docs/Ui.png)
---

## Quick Start
1. Download the latest release from [GitHub Releases](../../releases).
2. Open a terminal and run:
   ```bash
   java -jar ubersuper.jar
Start typing commands!

## Features
### Task Management
 - **listtask**                        - Show all tasks 
 - **todo `desc`**                     - Add a todo 
 - **deadline `desc` /by `time`**            - Add a deadline (e.g. 2025-12-31 18:00)
 - **eevent `desc` /from `start` /to `end`**     - Add an event with start/end
 - **deletetask** `idx`                 - Delete task by number
 - **mark `idx`**                      - Mark task as done
 - **unmark `idx`**                    - Mark task as not done
 - **onDate `yyyy-mm-dd`**             - Show items on a specific date
 - **findtask `keywords`**             - Search tasks by description

### Client Management
- **listclient**                                - Show all clients
- **addclient `name` /phone `p` /email `e`**    - Add a client
- **deleteclient `idx`**                        - Delete client by number
- **findclient `name`**                         - Search clients by name


Other Commands
- **bye** - Exit the app
