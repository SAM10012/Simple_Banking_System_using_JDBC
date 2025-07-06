import java.util.*;
import java.sql.*;

// ============ SIMPLE BANKING SYSTEM USING JDBC AND MySQL ===========

public class SimpleBankingSystem {

    // Used across the entire Project
    private static final String url = "jdbc:mysql://localhost:3306/simplebankingsystem";
    private static final String username = "root";
    private static final String password = "YOUR_DB_PASSWORD";

    // Exception for Invalid Account Number
    public static class InvalidAccountNumberException extends Exception {
        public InvalidAccountNumberException() {
            super("Account Number does not exist.");
        }
    }

    // Exception for Insufficient Funds in Account
    public static class InsufficientFundsException extends Exception {
        public InsufficientFundsException() {
            super("Insufficient funds in the account.");
        }
    }

    // Exception for Invalid Amount of Funds to be deposited
    public static class InvalidDepositFundsException extends Exception {
        public InvalidDepositFundsException() {
            super("Invalid Deposit Funds Amount");
        }
    }

    // Exception for Invalid Phone Number
    public static class InvalidPhoneNumberException extends Exception {

        public InvalidPhoneNumberException() {
            super("Invalid Phone Number");
        }
    }

    // Exception for Invalid Email
    public static class InvalidEmailException extends Exception{
        public InvalidEmailException()
        {
            super("Invalid Email Id. Please retry.");
        }
    }

    // Exception for Invalid Initial Balance in an Account at the time of Account Creation
    public static class InvalidInitialBalanceException extends Exception {
        public InvalidInitialBalanceException() {
            super("Invalid Initial balance");
        }
    }


    // Checks Initial Balance in Account
    public static double checkBalance(double init_balance) throws InvalidInitialBalanceException {
        if (init_balance > 0)
            return init_balance;
        else {
            throw new InvalidInitialBalanceException();
        }

    }

    // Checks if the amount of money to be deposited is positive
    public static double checkDepositFundAmt(Double funds) throws InvalidDepositFundsException {
        if (funds > 0) {
            return funds;
        } else {
            throw new InvalidDepositFundsException();
        }
    }

    // Checks if the Account has sufficient balance before withdrawing or transferring funds
    public static double checkWithdrawFundAmt(Connection conn, int acct_num, Double funds) throws InsufficientFundsException {

        double curr_bal = getUpdatedBalance(conn, acct_num);
        if (curr_bal >= funds) {
            return funds;
        } else {
            System.out.println("\nCurrent Account Balance is: " + curr_bal + ".");
            throw new InsufficientFundsException();
        }

    }

    // Checks Validity of Phone Number
    public static String checkPhoneNumber(String phone) throws InvalidPhoneNumberException {
        if (phone.matches("^[6-9][0-9]{9}$")) {
            return phone;
        } else {
            throw new InvalidPhoneNumberException();
        }
    }

    // Checks validity of Email Id
    public static String checkEmail(String email) throws InvalidEmailException
    {
        String emp_str = "";
        if(email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$"))
        {
            return email;
        }
        else {
            throw new InvalidEmailException();
        }
    }

    // Checks whether a user provided Account Number exists
    public static int accountExists(Connection conn, int acct_num) {

        try {
            String acct_query = "SELECT acc_no FROM customers WHERE acc_no = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(acct_query);
            preparedStatement.setInt(1, acct_num);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                return acct_num;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }

    // Returns the updated Balance of an Account after performing any banking operation
    public static double getUpdatedBalance(Connection conn, int acct_num) {
        double updatedBalance = 0;
        try {
            String check_balance_query = "select balance from customers where acc_no = ?";
            PreparedStatement check_bal_preparedStatement = conn.prepareStatement(check_balance_query);
            check_bal_preparedStatement.setInt(1, acct_num);
            ResultSet resultSet = check_bal_preparedStatement.executeQuery();
            if (resultSet.next()) {
                updatedBalance = resultSet.getDouble("balance");
            } else {
                System.out.println("Data not updated successfully.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return updatedBalance;
    }

    // Case 1: Customer Account Creation
    public static void createNewAccount(Connection conn, Scanner sc) {
        try {
            String insert_query = "INSERT into customers(name,phone,email,balance) VALUES(?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insert_query);

            System.out.print("\nEnter Customer Name: ");
            String cust_name = sc.nextLine();

            String phone_no = "";
            while (true) {
                try {
                    System.out.print("\nEnter Customer Phone Number: ");
                    phone_no = checkPhoneNumber(sc.nextLine());
                    break;
                } catch (InvalidPhoneNumberException e) {
                    System.out.println("Please enter valid Phone Number");
                }

            }

            String email_id = "";
            while (true){
                try {
                    System.out.print("\nEnter Customer Email Id: ");
                    email_id = checkEmail(sc.nextLine());
                    break;
                } catch (InvalidEmailException e) {
                    System.out.println(e.getMessage());
                }
            }

            double balance = 0;
            while (true) {
                try {
                    System.out.print("\nEnter your starting balance: ");
                    balance = checkBalance(sc.nextDouble());
                    sc.nextLine();
                    break;
                } catch (InvalidInitialBalanceException e) {
                    System.out.println("Initial Balance must be more than Rs.0. Please retry.");
                }
            }


            preparedStatement.setString(1, cust_name);
            preparedStatement.setString(2, phone_no);
            preparedStatement.setString(3, email_id);
            preparedStatement.setDouble(4, balance);


            int rowsInserted = preparedStatement.executeUpdate();

            String get_acct_no_query = "select acc_no from customers where phone = ?";
            PreparedStatement acc_no_preparedStatement = conn.prepareStatement(get_acct_no_query);
            acc_no_preparedStatement.setString(1, phone_no);
            ResultSet resultSet = acc_no_preparedStatement.executeQuery();

            if (rowsInserted > 0 && resultSet.next()) {
                System.out.println("New Customer Account " + resultSet.getInt("acc_no") + " created successfully!");
            } else {
                System.out.println("Account not created.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Phone Number Already Exists! Please Retry.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    // Case 2: View Account Balance
    public static void viewBalance(Connection conn, Scanner sc) {
        while (true) {
            try {
                System.out.print("\nEnter the Account Number to view Balance: ");
                int acct_no = accountExists(conn, sc.nextInt());
                sc.nextLine();

                if (acct_no != 0) {
                    String check_balance_query = "select balance from customers where acc_no = ?";
                    PreparedStatement check_bal_preparedStatement = conn.prepareStatement(check_balance_query);
                    check_bal_preparedStatement.setInt(1, acct_no);
                    ResultSet resultSet = check_bal_preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        System.out.println("Customer Balance is: " + resultSet.getDouble("balance"));
                    } else {
                        System.out.println("Customer Account not found.");
                    }
                    break;
                } else {
                    throw new InvalidAccountNumberException();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } catch (InvalidAccountNumberException f) {
                System.out.println(f.getMessage());
            }
        }
    }

    // Case 5: Overloaded depositFunds Method for Crediting to Account (during Funds Transfer)
    public static void depositFunds(Connection conn, int acct_num, double funds) {

        try {

            String deposit_query = "UPDATE customers SET balance = balance + ? WHERE acc_no = ?";
            PreparedStatement deposit_preparedStatement = conn.prepareStatement(deposit_query);


            deposit_preparedStatement.setDouble(1, funds);
            deposit_preparedStatement.setInt(2, acct_num);

            int rowsAffected = deposit_preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    //Case 3: Deposit Funds to an Account
    public static void depositFunds(Connection conn, Scanner sc) {

        try {
            conn.setAutoCommit(false);
            String deposit_query = "UPDATE customers SET balance = balance + ? WHERE acc_no = ?";
            PreparedStatement deposit_preparedStatement = conn.prepareStatement(deposit_query);

            int acct_num = 0;
            while (true) {
                try {
                    System.out.print("\nEnter the Account Number for depositing funds: ");
                    acct_num = accountExists(conn, sc.nextInt());
                    sc.nextLine();
                    if (acct_num != 0) {
                        break;
                    } else {
                        throw new InvalidAccountNumberException();
                    }
                } catch (InvalidAccountNumberException e) {
                    System.out.println(e.getMessage());
                }
            }

            double funds = 0;
            while (true) {
                try {
                    System.out.print("\nEnter the deposit amount: ");
                    funds = checkDepositFundAmt(sc.nextDouble());
                    sc.nextLine();
                    break;
                } catch (InvalidDepositFundsException e) {
                    System.out.println(e.getMessage());
                }
            }

            deposit_preparedStatement.setDouble(1, funds);
            deposit_preparedStatement.setInt(2, acct_num);

            int rowsAffected = deposit_preparedStatement.executeUpdate();


            if (rowsAffected > 0) {
                conn.commit();
                System.out.println("Funds deposited successfully. Updated Balance: " + getUpdatedBalance(conn, acct_num));
            } else {
                conn.rollback();
                System.out.println("Funds not deposited successfully.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    // Case 5: Overloaded withdrawFunds Method for Depositing from Account (during Transfer Funds)
    public static void withdrawFunds(Connection conn, int acct_num, double funds) {

        try {

            String withdraw_query = "UPDATE customers SET balance = balance - ? WHERE acc_no = ?";
            PreparedStatement withdraw_preparedStatement = conn.prepareStatement(withdraw_query);

            withdraw_preparedStatement.setDouble(1, funds);
            withdraw_preparedStatement.setInt(2, acct_num);

            int rowsAffected = withdraw_preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    // Case 4: Withdraw Funds from an Account
    public static void withdrawFunds(Connection conn, Scanner sc) {

        try {
            conn.setAutoCommit(false);
            String withdraw_query = "UPDATE customers SET balance = balance - ? WHERE acc_no = ?";
            PreparedStatement withdraw_preparedStatement = conn.prepareStatement(withdraw_query);

            int acct_num = 0;
            while (true) {
                try {
                    System.out.print("\nEnter the Account Number for withdrawing funds: ");
                    acct_num = accountExists(conn, sc.nextInt());
                    sc.nextLine();
                    if (acct_num != 0) {
                        break;
                    } else {
                        throw new InvalidAccountNumberException();
                    }
                } catch (InvalidAccountNumberException e) {
                    System.out.println(e.getMessage());
                }

            }

            double funds = 0;
            while (true) {
                try {
                    System.out.print("\nEnter the withdraw amount: ");
                    funds = checkWithdrawFundAmt(conn, acct_num, sc.nextDouble());
                    sc.nextLine();

                    break;
                } catch (InsufficientFundsException e) {
                    conn.rollback();
                    System.out.println(e.getMessage());
                }
            }

            withdraw_preparedStatement.setDouble(1, funds);
            withdraw_preparedStatement.setInt(2, acct_num);

            int rowsAffected = withdraw_preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit();
                System.out.println("Funds withdrawn successfully. Updated Balance: " + getUpdatedBalance(conn, acct_num));
            } else {
                conn.rollback();
                System.out.println("Funds not withdrawn successfully.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    // Case 5: Transfer Funds (withdrawFunds + depositFunds) ==> Used Method Overloading
    public static void transferFunds(Connection conn, Scanner sc) {

        try {
            conn.setAutoCommit(false);

            int debit_acc_no = 0;
            while (true) {
                try {
                    System.out.print("\nEnter the Account Number from where funds will be debited: ");
                    debit_acc_no = accountExists(conn, sc.nextInt());
                    sc.nextLine();

                    if (debit_acc_no != 0) {
                        break;
                    } else {
                        throw new InvalidAccountNumberException();
                    }
                } catch (InvalidAccountNumberException e) {
                    System.out.println(e.getMessage());
                }
            }

            int credit_acc_no = 0;
            while (true) {
                try {
                    System.out.print("\nEnter the Account Number where funds will be credited: ");
                    credit_acc_no = accountExists(conn, sc.nextInt());
                    sc.nextLine();

                    if (credit_acc_no != 0) {
                        break;
                    } else {
                        throw new InvalidAccountNumberException();
                    }
                } catch (InvalidAccountNumberException e) {
                    System.out.println(e.getMessage());
                }
            }


            while (true) {
                try {
                    System.out.print("\nEnter the amount of funds to be transferred: ");
                    double funds = sc.nextDouble();
                    sc.nextLine();

                    if (checkWithdrawFundAmt(conn, debit_acc_no, funds) == funds) {
                        withdrawFunds(conn, debit_acc_no, funds);
                        depositFunds(conn, credit_acc_no, funds);

                        conn.commit();
                        System.out.println("\nTransaction is successful.");
                        System.out.println("Updated Balance in Debit Account: " + getUpdatedBalance(conn, debit_acc_no));
                        System.out.println("Updated Balance in Credit Account: " + getUpdatedBalance(conn, credit_acc_no));
                    } else {
                        conn.rollback();
                        System.out.println("Transaction not successful.");
                    }

                    break;
                } catch (InsufficientFundsException e) {
                    conn.rollback();
                    System.out.println(e.getMessage());
                }
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Case 6: Customer Account Deletion
    public static void deleteCustAccount(Connection conn, Scanner sc) {
        try {

            int del_acct_num = 0;
            while (true) {
                try {
                    System.out.print("\nEnter Customer Account Number to be deleted: ");
                    del_acct_num = accountExists(conn, sc.nextInt());
                    sc.nextLine();

                    if (del_acct_num != 0) {
                        break;
                    } else {
                        throw new InvalidAccountNumberException();
                    }
                } catch (InvalidAccountNumberException e) {
                    System.out.println(e.getMessage());
                }
            }

            String del_acct_query = "DELETE FROM customers WHERE acc_no = ?";
            PreparedStatement del_acct_preparedStatement = conn.prepareStatement(del_acct_query);

            del_acct_preparedStatement.setInt(1, del_acct_num);
            int rowsDeleted = del_acct_preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Customer Account deleted successfully.");
            } else {
                System.out.println("Customer Account not deleted successfully.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) {

        // Load JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        Scanner sc = new Scanner(System.in);

        // Loop to implement multiple banking operations
        while (true) {
            try {
                // Create Connection to DB
                Connection conn = DriverManager.getConnection(url, username, password);

                System.out.println("\n================== WELCOME TO ABC BANK ==========================");
                System.out.println("=================== TELLER POINT OF VIEW ==========================");
                System.out.println("\nPress 1 for Account Creation.\nPress 2 to Check Balance.\nPress 3 to Deposit Funds.\nPress 4 to Withdraw Funds.\nPress 5 to Transfer Funds.\nPress 6 to Delete a Customer Account.\nPress 7 to Exit.");
                System.out.print("\nSelect an option: ");
                int option = sc.nextInt();
                sc.nextLine();

                switch (option) {
                    case 1:
                        //================= CREATE NEW BANK ACCOUNT FOR NEW CUSTOMER ====================
                        while (true) {
                            createNewAccount(conn, sc);
                            System.out.print("\nAdd another account (Y/N): ");
                            String choice = sc.nextLine();
                            if (choice.toUpperCase().equals("N")) {
                                break;
                            }
                        }
                        // ============================================================================
                        break;
                    case 2:
                        // ========================== VIEW ACCOUNT BALANCE ==============================

                        while (true) {
                            viewBalance(conn, sc);
                            System.out.print("\nDo you want to check another balance(Y/N): ");
                            String choice = sc.nextLine();
                            if (choice.toUpperCase().equals(("N"))) {
                                break;
                            }

                        }
                        // ==============================================================================
                        break;
                    case 3:
                        // ========================= DEPOSIT FUNDS ===========================
                        while (true) {
                            depositFunds(conn, sc);
                            System.out.print("\nDo you want to add more funds(Y/N): ");
                            String choice = sc.nextLine();
                            if (choice.toUpperCase().equals(("N"))) {
                                break;
                            }
                        }
                        // ===================================================================
                        break;
                    case 4:
                        // ========================= WITHDRAWAL OF FUNDS ===========================
                        while (true) {
                            withdrawFunds(conn, sc);
                            System.out.print("\nDo you want to withdraw more funds(Y/N): ");
                            String choice = sc.nextLine();
                            if (choice.toUpperCase().equals(("N"))) {
                                break;
                            }
                        }
                        // ===================================================================
                        break;
                    case 5:
                        // ======================== TRANSFER FUNDS =============================
                        while (true) {
                            transferFunds(conn, sc);
                            System.out.print("\nDo you want to transfer more funds(Y/N): ");
                            String choice = sc.nextLine();
                            if (choice.toUpperCase().equals(("N"))) {
                                break;
                            }
                        }
                        // =====================================================================
                        break;
                    case 6:
                        // ======================== DELETE CUSTOMER ACCOUNT =============================
                        while (true) {
                            deleteCustAccount(conn, sc);
                            System.out.print("\nDo you want to delete another Customer Account(Y/N): ");
                            String choice = sc.nextLine();
                            if (choice.toUpperCase().equals(("N"))) {
                                break;
                            }
                        }
                        // ========================================================================
                        break;
                    case 7:
                        System.out.println("\n============ \uD83D\uDE0A THANK YOU AND HAVE A GREAT DAY! \uD83D\uDE0A ================");
                        System.exit(0);
                    default:
                        System.out.println("Please select from provided options.");


                }
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }


        }


    }

}
