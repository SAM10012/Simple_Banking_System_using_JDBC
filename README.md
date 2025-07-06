# 💰 Simple Banking System (Java + JDBC + MySQL)

A console-based banking application developed in **Java** using **JDBC** for database connectivity with **MySQL**. This project simulates real-world banking operations such as account creation, balance check, deposit, withdrawal, fund transfer, and account deletion.

---------------------------------------------------------------------------------------------------------------------------------------------------------------

## 🧠 Features

- ✅ Create new customer accounts with validation (email, phone, balance).
- 💸 Deposit and withdraw funds with amount checks.
- 🔁 Transfer funds between accounts (with rollback on failure).
- 🔐 Custom exception handling for business rules.
- 📞 Validates phone numbers and email addresses using regex.
- 🧾 View updated account balances after transactions.
- 🗑️ Delete customer accounts.
- 🔄 Repeats operations until explicitly exited.

--------------------------------------------------------------------------------------------------------------------------------------------------------------

## 🛠️ Tech Stack

- **Language**: Java.
- **Database**: MySQL.
- **Connector**: JDBC.
- **Database Driver**: `com.mysql.cj.jdbc.Driver.

--------------------------------------------------------------------------------------------------------------------------------------------------------------

## 🗃️ Database Schema

```sql
CREATE TABLE customers (
  acc_no INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  phone VARCHAR(10) UNIQUE NOT NULL,
  email VARCHAR(100) NOT NULL,
  balance DOUBLE NOT NULL
);

============================================================================================================================================================

How to Run This Project (Prerequisites: Make sure you have MySQL and Java installed)

1. Clone this repository: git clone https://github.com/yourusername/simple-banking-system.git
2. Create the simplebankingsystem MySQL database and run the schema above.
3. Update the database credentials inside the Java file:
      private static final String url = "jdbc:mysql://localhost:3306/simplebankingsystem";
      private static final String username = "root";
      private static final String password = "YOUR_DB_PASSWORD";
4. Compile and run:
      javac SimpleBankingSystem.java
      java SimpleBankingSystem

===========================================================================================================================================================

📚 Concepts Used

1. JDBC API

2. SQL Queries (SELECT, INSERT, UPDATE, DELETE)

3. Exception Handling

4. Regex-based Input Validation

5. Method Overloading

6. Transaction Management (commit() and rollback())

==========================================================================================================================================================

🙌 Acknowledgements
This project was built as a beginner-friendly hands-on exercise to strengthen JDBC and MySQL integration in Java.

==========================================================================================================================================================

📝 License
This project is open source and free to use under the MIT License.

==========================================================================================================================================================

✍️ Author
Made with ❤️ by Samadrita

==========================================================================================================================================================


