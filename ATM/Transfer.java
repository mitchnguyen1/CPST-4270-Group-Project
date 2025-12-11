

import javax.xml.crypto.Data;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Transfer {
    static Scanner input = new Scanner(System.in);
    static DecimalFormat moneyFormat = new DecimalFormat("'$'###,##0.00");

    /**
     * Function to transfer from one user account to another user in the system
     * 1) The account type is determined to where to transfer from
     * 2) User input the account number to transfer to
     * 3) User input the amount to send
     * @param senderAcc - Current User's account object
     * @param senderAccType - Checking or Savings account to transfer from
     */

    public static void transferToUsers(Account senderAcc, String senderAccType){
        //Save the sender account type
        Integer senderAccountType = 0;
        if (senderAccType.equalsIgnoreCase("Checkings")) {
            senderAccountType = 1;
        } else if (senderAccType.equalsIgnoreCase("Savings")) {
            senderAccountType = 2;
        }


        // Ask for receiver account number to transfer until valid (0 = cancel)
        int receiverAccNumber;
        Account receiverAcc; // Account object for the receiver

        while (true) {
            System.out.print("\nEnter the user's account number to transfer to (0 to cancel): ");

            try {
                receiverAccNumber = input.nextInt();

                // Cancel option
                if (receiverAccNumber == 0) {
                    System.out.println("Transfer cancelled.");
                    return;
                }

                // Prevent transfer to self
                if (receiverAccNumber == senderAcc.getCustomerNumber()) {
                    System.out.println("You cannot transfer funds to the same account. Please enter a different account number.");
                    continue; // ask again
                }

                // Validate account exists
                if (DatabaseConnection.validateDBAccount(receiverAccNumber)) {

                    // Confirm account number
                    System.out.print("\nYou entered account number " + receiverAccNumber + ". Is this correct? (Y/N): ");
                    String confirm = input.next().trim();

                    if (confirm.equalsIgnoreCase("Y")) {
                        receiverAcc = DatabaseConnection.loadAccount(receiverAccNumber);
                        break; // done, move forward
                    } else {
                        continue;
                    }

                } else {
                    System.out.println("User account does not exist. Please try again.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter digits only.");
                input.next(); // clear the buffer
            }
        }




        while (true) {
            try {
                //Call function to display balance of sender
                displayBalance(senderAcc,senderAccountType);

                //Prompt user the amount to send and validate if user has enough
                double amount = amountToSend(senderAcc,senderAccountType);

                //Determine the receiver account type to transfer to
                int receiverAccountType = receiverAccountTypeSelection();

                System.out.println(); // adds a blank line

                //initial status of loading bar
                showProgress(40);
                //call function to withdrawal from sender's account and deposit into receivers account
                transferBetweenAccounts(senderAcc, senderAccountType, receiverAcc, receiverAccountType, amount);

                //completed status of loading bar
                showProgress(100);

                //Display the sender balance and success message
                displayBalance(senderAcc, senderAccountType);
                System.out.println("\nTransfer of $" +amount + " to Account Number: " + receiverAccNumber + " is successful!");
                System.out.println("\n-----------------------------------------------------------------");
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter digits only.");
                input.next();
            }
        }
    }

    /**
     * Function to withdrawal from the sender's correct account
     * @param senderAcc Sender's account object
     * @param senderAccountType  1/2 for Checking or Saving
     * @param receiverAcc Receiver's account object
     * @param receiverAccType  1/2 for Checking or Saving
     * @param amount Amount to withdrawal
     */
    public static void transferBetweenAccounts(Account senderAcc, Integer senderAccountType, Account receiverAcc, Integer receiverAccType, Double amount){
        //Subtract from correct sender's account
        if(senderAccountType == 1){
            //Subtract the amount from the senders checking and Update in database
            DatabaseConnection.updateCheckingBalance(senderAcc.getCustomerNumber(), senderAcc.calcCheckingWithdraw(amount));
            if(receiverAccType == 1){
                //update status of loading bar
                showProgress(75);
                //Add the amount to the receiver checking and Update in database
                DatabaseConnection.updateCheckingBalance(receiverAcc.getCustomerNumber(), receiverAcc.calcCheckingDeposit(amount));
            }
            else{
                //update status of loading bar
                showProgress(75);
                //Add the amount to the receiver saving and Update in database
                DatabaseConnection.updateSavingBalance(receiverAcc.getCustomerNumber(), receiverAcc.calcSavingDeposit(amount));

            }
        }
        else {
            //Subtract the amount from the senders savings and Update in database
            DatabaseConnection.updateSavingBalance(senderAcc.getCustomerNumber(), senderAcc.calcSavingWithdraw(amount));
            if (receiverAccType == 1) {
                //update status of loading bar
                showProgress(75);
                //Add the amount to the receiver checkings and Update in database
                DatabaseConnection.updateCheckingBalance(receiverAcc.getCustomerNumber(), receiverAcc.calcCheckingDeposit(amount));
            } else {
                //update status of loading bar
                showProgress(75);
                //Add the amount to the receiver saving and Update in database
                DatabaseConnection.updateSavingBalance(receiverAcc.getCustomerNumber(), receiverAcc.calcSavingDeposit(amount));
            }
        }
    }

    /**
     * Function to display the sender's balance based on the account type
     * @param senderAcc - Sender's account object to retrieve balance
     * @param senderAccountType - Checking or Saving
     */
    public static void displayBalance(Account senderAcc, Integer senderAccountType){
        //Print the sender balance-from either checking or savings
        if (senderAccountType == 1) {
            System.out.println();
            System.out.println("\nCurrent Checking Account ("+senderAcc.getCustomerNumber()+") has a balance: "
                    + moneyFormat.format(senderAcc.getCheckingBalance()));
        } else {
            System.out.println();
            System.out.println("\nCurrent Saving Account ("+senderAcc.getCustomerNumber()+") has a balance: "
                    + moneyFormat.format(senderAcc.getSavingBalance()));
        }
    }

    /**
     * Prompt to select the receiver's account type
     * @return return int of account type
     */
    public static Integer receiverAccountTypeSelection(){
        //Prompt to enter which account to send to the user's account
        System.out.println("\nSelect the receiver's account type you want to send: ");
        System.out.println(" Type 1 - Checking Account");
        System.out.println(" Type 2 - Savings Account");
        System.out.print("\nChoice: ");
        return input.nextInt();
    }


    /**
     * Function to validate amount to send is enough in sender's account
     * @param senderAcc
     * @param senderAccountType
     * @return amount to send
     */
    public static double amountToSend(Account senderAcc, int senderAccountType) {
        while (true) {
            try {
                System.out.print("\nAmount you want to transfer: ");
                double amount = input.nextDouble();

                if (amount <= 0) {
                    System.out.println("Amount cannot be negative.");
                    continue; //continue loop
                }

                // Check for sufficient balance based on account type, checking/saving
                if (senderAccountType == 1 && (senderAcc.getCheckingBalance() - amount) >= 0) {
                    return amount;
                }
                else if (senderAccountType == 2 && (senderAcc.getSavingBalance() - amount) >= 0) {
                    return amount;
                }
                else {
                    System.out.println("\nBalance cannot be negative or insufficient funds.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter numbers only.");
                input.next();
            }
        }
    }

    public static void showProgress(int percent) {
        // Clamp percent between 0–100
        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;

        int totalBars = 20; // Length of bar
        int filledBars = (percent * totalBars) / 100;

        String bar = "=".repeat(filledBars) + " ".repeat(totalBars - filledBars);

        // Updates the same line
        System.out.print("\rProcessing transfer... [" + bar + "] " + percent + "%");
    }


}
