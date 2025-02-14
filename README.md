# J. POO Morgan Chase & Co. (Stage 2)

**Author**: Mihai-Ionuț Păunescu

## Design Patterns Used
I used four design patterns in the development of this project:

1. **Factory Pattern**  
   - **Location**: Object creation for commands, cashback strategies, and service plans in the `command`, `model.plan`, and `model.commerciant` packages.  
   - **Motivation**: Simplifies the process of initializing objects and provides flexibility to add new command types, strategies, or plans without modifying existing code. It centralizes object creation, reducing duplication and improving maintainability.

2. **Singleton**  
   - **Location**: Managing service instances (e.g., `AccountService`, `CardService`).  
   - **Motivation**: Ensures that only one instance of each service exists to reduce resource usage and centralize logic. This avoids multiple instances of critical services, providing a global, consistent point of access.

3. **Visitor Pattern**  
   - **Location**: Handling the required operations for each command and generating specific output for each transaction type in the `visitor` package.  
   - **Motivation**: Allows adding new operations without modifying existing structures. This pattern decouples operation logic from data structures, making it easier to add new command or transaction types with minimal impact on existing code.

4. **Strategy Pattern**  
   - **Location**: Implementing cashback strategies and service plans in the `model.plan` and `model.commerciant` packages.  
   - **Motivation**: Facilitates dynamic changes in behavior for cashback or plans based on user or merchant type. This pattern uses separate classes for each strategy, making it easier to manage and add new behaviors without affecting existing code.

## New Features

### Users
I added extra details for users:
- **Birth Date** (`birthDate`)
- **Occupation** (`occupation`)
- **Account Management Plan**

### Merchants
I introduced merchants with the following characteristics:
- **Cashback Strategy**:
  - **NrOfTransactions**: Progressive cashback based on the number of transactions.
  - **SpendingThreshold**: Percentage-based cashback influenced by total spending and the user’s plan type.

### Service Plans
I enabled users to benefit from different service plans:
- **Standard**
- **Student**
- **Silver**
- **Gold**

**Plan Upgrades**:
- Users can upgrade to higher plans for a fee.
- There is an automatic upgrade to Gold if the user makes 5 payments of at least 300 RON.

### New Accounts
- **Savings Account**:
  - Allows withdrawals to classic accounts, with certain age restrictions.
  - Includes a currency transfer option.
- **Business Account**:
  - Shared account for entrepreneurs with three user roles:
    - **Owner**: Full rights, including setting transaction limits.
    - **Manager**: Can perform transactions and manage cards.
    - **Employee**: Can perform transactions within predefined limits.

### New Transactions
- **Cash Withdrawal**: Allows cash withdrawals with fees based on the user’s plan.
- **Custom Split Payment**: Enables custom split payments based on each user’s share.

### New Reports
- **Business Report**:
  - Types: “Transaction” and “Commerciant”
  - Includes information about managers, employees, and merchants.

### Updates to Existing Commands
- **addFunds**: Aligned with limits for business users.
- **createCard**: Creates cards associated with business users, managed by their roles.
- **deleteAccount**: Only the Owner can delete business accounts.
- **sendMoney**: Supports transfers to merchants.
- **payOnline**: Includes validation for invalid merchants.
