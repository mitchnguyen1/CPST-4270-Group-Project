# CPST-4270-Group-Project
Mitchell Nguyen and Ian Kreger group project for CPST-4270 Advanced Application Development for Industry.

Features in Development:
1.	Transaction history feature - Allows the user to view the last n transactions they made. This will act as a receipt and will mimic a real banking experience.
2.	Mitchell: Money transfers between different users – Extends the current transfer system beyond just the current user’s checking and savings accounts. Allows sending money to another user’s account in the system.


## ATM Machine
### A functional ATM project written in Java
This project generates an automated teller machine (ATM) that stores the user's bank account, acount number, and password. With this ATM, users will be able to:
* Make deposits
* Withdraw money
* View their account balance 

---
## Installation

### Dependencies

This project requires Java 8 or a later version to run

### User Installation

To run the project:
1. Clone the ATM-Machine repo to your local machine (forking first is recommended for contributors 
2. Make sure all Java files (ATM.java, Account.java, OptionMenu.java, etc.) are located in the same folder (or in a src/ folder if you are using one). 
3. Download the PostgreSQL JDBC driver: postgresql-42.x.x.jar 
4. Create a lib/ folder in your project and place the jar inside it. 
5. Open the project in a Java IDE (Eclipse, IntelliJ, NetBeans) or use your command prompt/terminal. 
6. In the terminal, cd into your project directory. 
7. Edit the file named db.properties.examples and rename it to db.properties (You will need to contact the developer for login info)
8. Compile the project with the PostgreSQL driver on the classpath by running this command:
* Mac: javac -cp ".:lib/postgresql-42.x.x.jar" *.java
* Windows: javac -cp ".;lib\postgresql-42.x.x.jar" *.java 
9. Run the ATM program, making sure to include the PostgreSQL jar on the classpath:
* Mac: java -cp ".:lib/postgresql-42.x.x.jar" ATM
* Windows: java -cp ".;lib\postgresql-42.x.x.jar" ATM 



---
## Visual
After running the command in #9, you should see this interface:

![ATM interface](https://user-images.githubusercontent.com/77065772/218245894-caabfd9b-3fa4-4833-81ba-07a2eadaf648.PNG)

---
# For Developers

## Contribution Guidelines

### Working on Issues

If you see an issue that has not been raised, create an issue first, and discuss the changes you want to make with the project's lead developers

### Writing Your First Pull Request

First you'll want to fork ATM-Machine on Github

Then create a branch for the issue you want to work on

When finished, push from your branch, then use the "Compare and Pull request" button on Github to create a pull request

Make sure to reference the issue your pull request addresses
