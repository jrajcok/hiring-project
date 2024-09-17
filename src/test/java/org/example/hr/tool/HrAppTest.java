package org.example.hr.tool;

import org.example.model.DeviationEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HrAppTest {

    private static final String filename = "src/test/resources/input/employees-input.csv";

    private static HrApp hrApp;

    @BeforeAll
    public static void setUp() {
        hrApp = new HrApp();
        hrApp.reportDeviations(filename);
    }

    @Test
    public void testLoadEmployees() {
        assertEquals(9, hrApp.getEmployees().size());
    }

    @Test
    public void testBuildManagerSubordinateRelationships() {
        assertEquals(2, hrApp.getEmployees().get(123).getSubordinates().size());
        assertEquals(2, hrApp.getEmployees().get(124).getSubordinates().size());
        assertEquals(0, hrApp.getEmployees().get(125).getSubordinates().size());
        assertEquals(1, hrApp.getEmployees().get(300).getSubordinates().size());
        assertEquals(0, hrApp.getEmployees().get(301).getSubordinates().size());
        assertEquals(1, hrApp.getEmployees().get(305).getSubordinates().size());
        assertEquals(1, hrApp.getEmployees().get(306).getSubordinates().size());
        assertEquals(1, hrApp.getEmployees().get(307).getSubordinates().size());
        assertEquals(0, hrApp.getEmployees().get(308).getSubordinates().size());
    }

    @Test
    public void testCheckManagerSalaries() {
        var deviations = hrApp.getDeviations();
        assertEquals(4, deviations.size());

        // with org.hamcrest could be better (assert that contains Object)
        assertEquals(305, deviations.get(0).empId());
        assertEquals(DeviationEnum.SALARY_TOO_LOW, deviations.get(0).deviationEnum());
        assertEquals(7999.5, deviations.get(0).deviationValue());

        assertEquals(306, deviations.get(1).empId());
        assertEquals(DeviationEnum.SALARY_TOO_HIGH, deviations.get(1).deviationEnum());
        assertEquals(5000, deviations.get(1).deviationValue());

        assertEquals(124, deviations.get(2).empId());
        assertEquals(DeviationEnum.SALARY_TOO_LOW, deviations.get(2).deviationEnum());
        assertEquals(6000, deviations.get(2).deviationValue());

        assertEquals(308, deviations.get(3).empId());
        assertEquals(DeviationEnum.SUBORDINATES_TOO_MANY, deviations.get(3).deviationEnum());
        assertEquals(1, deviations.get(3).deviationValue());
    }

    // could be more tests (negative ones, but I would need more dependencies than just JUnit, Maven and pure Java)
}
