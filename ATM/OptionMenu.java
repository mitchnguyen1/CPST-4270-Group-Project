import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;


public class OptionMenu {
	Scanner menuInput = new Scanner(System.in);
	DecimalFormat moneyFormat = new DecimalFormat("'$'###,##0.00");
	static HashMap<Integer, Account> data = new HashMap<Integer, Account>();

	public void getLogin() throws IOException {
		boolean end = false;
		int customerNumber = 0;
		int pinNumber = 0;
		while (!end) {
			try {
				System.out.print("\nEnter your customer number: ");
				customerNumber = menuInput.nextInt();
				System.out.print("\nEnter your PIN number: ");
				pinNumber = menuInput.nextInt();

				if (DatabaseConnection.validateDBAccount(customerNumber) && DatabaseConnection.validatePin(pinNumber)) {
						getAccountType(DatabaseConnection.loadAccount(customerNumber));
						end = true;
						break;
				} else {
					System.out.println("\nWrong Customer Number or Pin Number");
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Character(s). Only Numbers.");
				menuInput.nextLine(); // clear bad input
			}
		}
	}

	public void getAccountType(Account acc) {
		boolean end = false;
		while (!end) {
			try {
				System.out.println("\nSelect the account you want to access: ");
				System.out.println(" Type 1 - Checkings Account");
				System.out.println(" Type 2 - Savings Account");
				System.out.println(" Type 3 - View Transaction History"); // Added history option
				System.out.println(" Type 4 - Exit");
				System.out.print("\nChoice: ");
				int selection = menuInput.nextInt();

				switch (selection) {
				case 1:
					getChecking(acc);
					break;
				case 2:
					getSaving(acc);
					break;
				case 3: //Go to transaction history view
					getTransactionHistory(acc);
                	break;
				case 4:
					System.out.println("Thank you for using this ATM. Goodbye!");
    				System.exit(0);	
					break;
				default:
					System.out.println("\nInvalid Choice.");
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice.");
				menuInput.next();
			}
		}
	}

	public void getChecking(Account acc) {
		boolean end = false;
		while (!end) {
			try {
				System.out.println("\nCheckings Account: ");
				System.out.println(" Type 1 - View Balance");
				System.out.println(" Type 2 - Withdraw Funds");
				System.out.println(" Type 3 - Deposit Funds");
				//Edited options to transfer to other users
				System.out.println(" Type 4 - Transfer Funds to Savings");
				System.out.println(" Type 5 - Transfer Funds to Another User");
				System.out.println(" Type 6 - Exit");
				System.out.print("\nChoice: ");

				int selection = menuInput.nextInt();

				switch (selection) {
				case 1:
					System.out.println("\nCheckings Account Balance: " + moneyFormat.format(DatabaseConnection.checkingBalance(acc)));
					break;
				case 2:
					acc.getCheckingWithdrawInput();
					break;
				case 3:
					acc.getCheckingDepositInput();
					break;
				case 4:
					acc.getTransferInput("Checkings");
					break;
				case 5:
					//Option to transfer to another user
					Transfer.transferToUsers(acc, "Checkings");
				case 6:
					end = true;
					break;
				default:
					System.out.println("\nInvalid Choice.");
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice.");
				menuInput.next();
			}
		}
	}

	public void getSaving(Account acc) {
		boolean end = false;
		while (!end) {
			try {
				System.out.println("\nSavings Account: ");
				System.out.println(" Type 1 - View Balance");
				System.out.println(" Type 2 - Withdraw Funds");
				System.out.println(" Type 3 - Deposit Funds");
				//Edited options to transfer to other users
				System.out.println(" Type 4 - Transfer Funds to Checkings");
				System.out.println(" Type 5 - Transfer Funds to Another User");
				System.out.println(" Type 6 - Exit");
				System.out.print("\nChoice: ");

				int selection = menuInput.nextInt();
				switch (selection) {
				case 1:
					System.out.println("\nSavings Account Balance: " + moneyFormat.format(DatabaseConnection.savingBalance(acc)));
					break;
				case 2:
					acc.getsavingWithdrawInput();
					break;
				case 3:
					acc.getSavingDepositInput();
					break;
				case 4:
					acc.getTransferInput("Savings");
					break;
				case 5:
					//Option to transfer to another user
					Transfer.transferToUsers(acc, "Savings");
				case 6:
					end = true;
					break;
				default:
					System.out.println("\nInvalid Choice.");
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice.");
				menuInput.next();
			}
		}
	}

	//Added the method to display recent transactions for the current account
    public void getTransactionHistory(Account acc) {
        System.out.print("Enter how many recent transactions you want to view: ");
        int n = menuInput.nextInt();

        //Displays the last n transactions for this logged-in account
        TransactionHistory.displayLastTransactions(String.valueOf(acc.getCustomerNumber()), n);

        //Returns to the main account menu after viewing
        getAccountType(acc);
    }
	
	public void createAccount() throws IOException {
		int cst_no = 0;
		boolean end = false;

		while (!end) {
			try {
				System.out.println("\nEnter your customer number ");
				cst_no = menuInput.nextInt();

				// Check directly in the database
				if (DatabaseConnection.validateDBAccount(cst_no)) {
					System.out.println("\nThis customer number is already registered");
				} else {
					// number is free to use
					end = true;
				}

			} catch (InputMismatchException e) {
				System.out.println("\nInvalid choice. Please enter numbers only.");
				menuInput.next(); // clear invalid input
			}
		}
		System.out.println("\nEnter PIN to be registered");
		int pin = menuInput.nextInt();
		Account acc = new Account(cst_no, pin);
		data.put(cst_no, acc);
		// save to the database
		DatabaseConnection.saveAccount(acc);
		System.out.println("\nYour new account has been successfuly registered!");
		System.out.println("\nRedirecting to login.............");
		getLogin();
	}

	public void mainMenu() throws IOException {
		boolean end = false;
		while (!end) {
			try {
				System.out.println("\n Type 1 - Login");
				System.out.println(" Type 2 - Create Account");
				System.out.print("\nChoice: ");
				int choice = menuInput.nextInt();
				switch (choice) {
				case 1:
					getLogin();
					end = true;
					break;
				case 2:
					createAccount();
					end = true;
					break;
				default:
					System.out.println("\nInvalid Choice.");
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice.");
				menuInput.next();
			}
		}
		System.out.println("\nThank You for using this ATM.\n");
		menuInput.close();
		System.exit(0);
	}

}





