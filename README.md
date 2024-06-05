Davao Oriental Dagmay Weavers Directory and Output Monitoring Portal

Welcome to the Davao Oriental Dagmay Weavers Directory and Output Monitoring Portal! This system is designed to help manage the inventory, sales, and staff activities of a Dagmay weaving business.

**Features

User Management:
    - Secure login and signup for customers, staff, and administrators.
    - Different access levels and functionalities based on user roles.
Inventory Management:
    - Staff and administrators can add, update, and delete Dagmay items.
    - Customers can view available items and their details.
Sales and Shopping Cart:
    - Customers can browse items, add them to their cart, and proceed to checkout.
    - The system tracks sales records for monitoring and analysis.
Output Monitoring (Admin):
    - Administrators can monitor items added by staff, sold items, and total sales value.
Data Persistence:
    - User credentials, item details, sales records, and staff activities are saved to text files for persistence.

Getting Started

1. Clone the Repository:
   ```bash
   git clone https://github.com/your-username/dagmay-weaver-portal.git
   ```

2. **Compile and Run:
   ```bash
   javac DagmayWeaverMonitorouput.java
   java DagmayWeaverMonitorouput
   ```

### Usage

* **Customers:**
    - Sign up or log in to your account.
    - Browse the list of Dagmay items.
    - Add items to your cart and proceed to checkout.
* **Staff:**
    - Log in with your staff credentials.
    - Add or update Dagmay items in the inventory.
* **Administrators:**
    - Log in with your admin credentials.
    - Manage Dagmay items (add, update, delete).
    - Monitor system output (items added by staff, sales data).

File Structure

* `DagmayWeaverMonitorouput.java`: Main source code file.
* `UserCredentials.txt`: Stores user information (username, password, email, role).
* `DagmayItems.txt`: Stores details of Dagmay items (ID, name, price, description, stock).
* `SalesRecords.txt`: Stores sales transaction data.
* `StaffAddedItems.txt`: Stores records of items added or updated by staff.
* `UserSaveCart/`: Directory to store individual customer carts.

### Future Enhancements

* **Database Integration:** Replace text file storage with a database for better data management and scalability.
* **User Interface:** Develop a graphical user interface (GUI) for a more user-friendly experience.
* **Reporting:** Generate detailed reports on sales, inventory, and staff activity.
* **Payment Gateway:** Integrate a secure payment gateway for online transactions.

### Contributing

Contributions are welcome! Please fork the repository and submit pull requests with your enhancements.

### License

This project is licensed under the MIT License.

---

Let me know if you'd like any adjustments or have more questions!
