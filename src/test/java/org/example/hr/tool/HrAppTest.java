package org.example.hr.tool;

import org.example.model.DeviationEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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
        Map<Integer, Integer> expectedSubordinateCounts = Map.of(
            123, 2,
            124, 2,
            125, 0,
            300, 1,
            301, 0,
            305, 1,
            306, 1,
            307, 1,
            308, 0
        );

        expectedSubordinateCounts.forEach((empId, expectedCount) -> 
            assertEquals(expectedCount, hrApp.getEmployees().get(empId).getSubordinates().size())
        );
    }

    @Test
    public void testCheckManagerSalaries() {
        List<Deviation> expectedDeviations = List.of(
            new Deviation(305, DeviationEnum.SALARY_TOO_LOW, 7999.5),
            new Deviation(306, DeviationEnum.SALARY_TOO_HIGH, 5000.0),
            new Deviation(124, DeviationEnum.SALARY_TOO_LOW, 6000.0),
            new Deviation(308, DeviationEnum.SUBORDINATES_TOO_MANY, 1.0)
        );

        List<Deviation> actualDeviations = hrApp.getDeviations();
        assertEquals(expectedDeviations.size(), actualDeviations.size());

        for (int i = 0; i < expectedDeviations.size(); i++) {
            assertEquals(expectedDeviations.get(i).empId(), actualDeviations.get(i).empId());
            assertEquals(expectedDeviations.get(i).deviationEnum(), actualDeviations.get(i).deviationEnum());
            assertEquals(expectedDeviations.get(i).deviationValue(), actualDeviations.get(i).deviationValue());
        }
    }
}