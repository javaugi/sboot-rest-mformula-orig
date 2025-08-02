/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.payroll;

import com.spring5.empbilpayroll.Employee;
import com.spring5.empbilpayroll.PayrollData;

/**
 *
 * @author javaugi
 */
public class PayrollProcessor {
    
    public void processPayroll(Employee employee, PayrollData data) throws Exception {
        // Build the validation chain
        PayrollChainOfRespValidator validatorChain = new OvertimeValidator()
            .linkWith(new TaxComplianceValidator())
            .linkWith(new MinimumWageValidator());

        // Run validation
        if (validatorChain.validate(employee, data)) {
            System.out.println("Payroll validated successfully!");
            // Proceed with payroll calculation
        }
    }    
}

/*
Key Features
Chain Construction:
    new OvertimeValidator()
      .linkWith(new TaxComplianceValidator())
      .linkWith(new MinimumWageValidator());
Early Termination: If any validator fails, the chain stops (throws exception).
Extensibility: Add new validators without modifying existing code (Open/Closed Principle).

When to Use This Pattern
    Payroll Validation: Ensure compliance with labor laws, tax rules, etc.
    Multi-Step Approvals: For workflows like timesheet approval.
    Modular Validation: When rules may vary by region/employee type.
*/
