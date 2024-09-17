package org.example.hr.tool;

import org.example.model.DeviationEnum;
import org.example.model.Deviation;
import org.example.model.Employee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HrApp {

    // could be also with Java Docs, but this way is more readable

    private final Map<Integer, Employee> employees = new HashMap<>();
    private final List<Deviation> deviations = new ArrayList<>();

    public void reportDeviations(String fileName) {
        this.readCsv(fileName);
        this.buildManagerSubordinateRelationships();
        this.checkManagerSalaries();
        this.checkReportingLines();
    }

    private void readCsv(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                int id = Integer.parseInt(data[0]);
                String firstName = data[1];
                String lastName = data[2];
                int salary = Integer.parseInt(data[3]);
                Integer managerId = data.length > 4 && !data[4].isEmpty() ? Integer.parseInt(data[4]) : null;

                Employee employee = new Employee(id, firstName, lastName, salary, managerId);
                employees.put(id, employee);
            }
        } catch (IOException e) {
            System.out.printf("An error occurred while reading the file: %s", e.getMessage());
        }
    }

    private void buildManagerSubordinateRelationships() {
        for (Employee emp : employees.values()) {
            if (emp.getManagerId() != null) {
                Employee manager = employees.get(emp.getManagerId());
                if (manager != null) {
                    manager.addSubordinate(emp);
                }
            }
        }
    }

    private void checkManagerSalaries() {
        for (Employee emp : employees.values()) {
            if (!emp.getSubordinates().isEmpty()) {
                int avgSubordinateSalary = emp.getSubordinates().stream().mapToInt(Employee::getSalary).sum() / emp.getSubordinates().size();
                double lowerBound = avgSubordinateSalary * 1.2;
                double upperBound = avgSubordinateSalary * 1.5;

                if (emp.getSalary() < lowerBound) {
                    deviations.add(new Deviation(emp.getId(), DeviationEnum.SALARY_TOO_LOW, (int) (lowerBound - emp.getSalary())));
                    System.out.printf("Manager %s %s earns %.2f less than they should.\n", emp.getFirstName(), emp.getLastName(), lowerBound - emp.getSalary());
                } else if (emp.getSalary() > upperBound) {
                    deviations.add(new Deviation(emp.getId(), DeviationEnum.SALARY_TOO_HIGH, (int) (emp.getSalary() - upperBound)));
                    System.out.printf("Manager %s %s earns %.2f more than they should.\n", emp.getFirstName(), emp.getLastName(), emp.getSalary() - upperBound);
                }
            }
        }
    }

    // excluding CEO and employee (between them and CEO)
    private void checkReportingLines() {
        for (Employee emp : employees.values()) {
            int levels = calculateLevelsToCEO(emp, employees);
            if (levels > 5) {
                deviations.add(new Deviation(emp.getId(), DeviationEnum.SUBORDINATES_TOO_MANY, levels - 5));
                System.out.printf("Employee %s %s has a reporting line too long by %d level(s).\n", emp.getFirstName(), emp.getLastName(), levels - 5);
            }
        }
    }

    // Recursion to calculate manager levels
    private static int calculateLevelsToCEO(Employee emp, Map<Integer, Employee> employees) {
        if (emp.getManagerId() == null) {
            return 0;
        }
        Employee manager = employees.get(emp.getManagerId());
        return 1 + calculateLevelsToCEO(manager, employees);
    }

    public Map<Integer, Employee> getEmployees() {
        return employees;
    }

    public List<Deviation> getDeviations() {
        return deviations;
    }
}
