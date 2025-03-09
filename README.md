# **Salary Processor Application**

## **Overview**
This application leverages **Kafka Streams** to consume JSON messages representing `Employee` models and applies salary increase strategies based on the employee's role. It follows the **Factory** and **Strategy** design patterns to ensure scalability and maintainability.

## **Architecture**

### **Factory Pattern**
- **SalaryProcessorFactory**
    - An abstract class that maintains a map of strategies and provides a method to retrieve the appropriate strategy based on the employee role.

- **SalaryProcessorFactoryProvider**
    - Acts as a provider to fetch the required salary processor strategy at runtime.

### **Strategy Pattern**
Implements different salary increase strategies based on the employee type. Each strategy implements the common interface **SalaryIncreaseStrategy**:
- **DeveloperSalaryIncreaseStrategy**
- **DirectorSalaryIncreaseStrategy**
- **ManagerSalaryIncreaseStrategy**
- **TeamLeadSalaryIncreaseStrategy**

All strategies extend the abstract class **Calculatable**, which contains two injected Spring beans:
- **AppConfig** – Holds a map of **Bonus Properties** (`salaryLimit`, `percentage`).
- **SalaryCalculator** – Implements the **salary increase formula** using the defined configurations.

## **Kafka Streams Integration**
- The application consumes messages from **Kafka topics**, where each message is a **JSON representation of an Employee**.
- Based on the employee's role, the corresponding salary increase strategy is selected.
- The new salary is calculated dynamically using the appropriate strategy.

## **Getting Started**
1. **Configure Kafka Streams**
    - Set up Kafka Streams properties (`application ID`, `bootstrap servers`, and `serdes` for serialization/deserialization).
2. **Run the Application**
    - Start the **Spring Boot** application to begin processing employee salary updates.
3. **Extend and Customize**
    - Add or modify strategies by implementing the **SalaryIncreaseStrategy** interface and extending **Calculatable**.

## **Contributing**
Contributions are welcome! Feel free to open **issues** or **pull requests** for enhancements and improvements.
https://www.linkedin.com/in/martinmiloshev/