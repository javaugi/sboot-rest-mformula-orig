/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

import com.spring5.empbilpayroll.Employee;
import com.spring5.empbilpayroll.PayrollResult;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author javaugi
 */
@Service
public class PayrollService {

    private PayrollRuleStrategy rule;
    private List<PayrollObserver> observers;

    @Autowired
    private DroolsPayrollService payrollRules;

    public PayrollResult calculatePayroll(Employee employee) {
        PayrollResult result = new PayrollResult();

        // Step 1: Apply business rules (modifies employee)
        payrollRules.applyDrools(employee);

        // Step 2: Use rules-processed employee for calculations
        result.setOvertimePay(rule.calculateOvertime(employee.getHoursWorked(), employee));
        result.setTax(rule.calculateTax(employee));
        result.setNetSalary(employee.getSalary() - result.getTax());
        // ... other calculations
        observers.forEach(obs -> obs.onPayrollProcessed(employee, result));
        return result;
    }
}
/*
Key Interactions
Component	Responsibility
Drools (PayrollRule)	Dynamically adjusts Employee fields (e.g., sets eligibleForOvertime=true).
PayrollService	Uses the modified Employee to compute values (e.g., overtimePay).
PayrollResult	Holds the final calculated values (e.g., netSalary = salary - tax).

Example Output Flow
Input Employee:

java
Employee(name="John", hoursWorked=45, salary=5000)
After applyDrools():

java
Employee(name="John", hoursWorked=45, salary=5000, eligibleForOvertime=true)  // Modified by Drools
After calculatePayroll():

java
PayrollResult(overtimePay=225.0, tax=1000.0, netSalary=4000.0)
🚀 Why This Design?
Separation of Concerns:

Rules (Drools) handle business logic (e.g., "Who gets overtime?").
Service handles calculations (e.g., "How much overtime pay?").
Flexibility: Change rules in .drl files without touching Java code.
Auditability: Observers can log payroll events.

⚠️ Common Pitfalls
Missing kSession.dispose() → Memory leaks.
Rules not firing → Check kSession.fireAllRules() and rule conditions in .drl.
Stale data → Ensure Employee is re-inserted into the session if recalculating.

🔧 Extending the System
Add More Rules:

drl
rule "Bonus for Tenure"
    when
        $emp : Employee(yearsEmployed >= 5)
    then
        $emp.setBonus(1000);
end
Add More Calculations:

result.setBonusPay(employee.getBonus());
Let me know if you'd like to dive deeper into any p
 */
