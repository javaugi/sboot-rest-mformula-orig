/*
 * Copyright (C) 2019 Strategic Information Systems, LLC.
 *
 */
package com.spring5.empbilpayroll;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NamedQueries({
    @NamedQuery(name = "Employee.findByDepartment", query = "FROM Employee WHERE department = :dept"),
    @NamedQuery(name = "Employee.countAll", query = "SELECT COUNT(*) FROM Employee")
})
@NamedEntityGraphs({
    @NamedEntityGraph(
            name = "employee.withDepartmentAndProfile",
            attributeNodes = {
                @NamedAttributeNode("department"),
                @NamedAttributeNode("profile")}),
    @NamedEntityGraph(
            name = "employee.withAllRelations",
            attributeNodes = {
                @NamedAttributeNode("department"),
                @NamedAttributeNode("profile"),
                @NamedAttributeNode(value = "projects", subgraph = "projectSubgraph")
            },
            subgraphs = {
                @NamedSubgraph(
                        name = "projectSubgraph",
                        attributeNodes = {
                            @NamedAttributeNode("tasks"),
                            @NamedAttributeNode("teamMembers")})
            })
})
public class Employee implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;
    private String firstName;
    private String lastName;
    private String middleInitial;
    private String deptCode;
    private String departName;
    private double salary;
    private boolean nightShift;
    private Region region;
    double hoursWorked;

    // ManyToOne with Eager loading (default for single-valued associations)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private EmployeeProfile profile;
}
