package com.redko;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/*
@author   User
@project   lab1
@class  CompanyServiceImplTest
@version  1.0.0
@since 10.03.2026 - 22.10
*/


class CompanyServiceImplTest {

    private final Company main = new Company(null, 2);
    private final Company book = new Company(main, 3);
    private final Company manager = new Company(main, 4);
    private final Company developer = new Company(manager, 8);
    private final Company design = new Company(manager, 6);
    private final Company lawer = new Company(null, 1);

    private final List<Company> list = List.of(main, book, manager, developer, design);

    private final ICompanyService companyService = new CompanyServiceImpl();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void whenCompanyIsNullThenNull() {
        Company result = companyService.getTopLevelParent(null);
        Assertions.assertNull(result);
    }

    @Test
    void whenCompanyHasNoParentItIsOnTop() {
        Company result = companyService.getTopLevelParent(main);
        Assertions.assertEquals(main, result);
    }

    @Test
    void whenCompanyIsSingleItIsOnTop() {
        Company result = companyService.getTopLevelParent(lawer);
        Assertions.assertEquals(lawer, result);
    }

    @Test
    void whenCompanyHasOneStepToTheTopThenFindTop() {
        Assertions.assertEquals(main, companyService.getTopLevelParent(book));
    }

    @Test
    void whenCompanyHasTwoStepsToTheTopThenFindTop() {
        Assertions.assertEquals(main, companyService.getTopLevelParent(developer));
    }

    @Test
    void getEmployeeCount_WhenCompanyIsNull_ReturnsZero() {
        Assertions.assertEquals(0, companyService.getEmployeeCountForCompanyAndChildren(null, list));
    }

    @Test
    void getEmployeeCount_WhenListIsEmpty_ReturnsOnlyOwnEmployees() {
        Assertions.assertEquals(2, companyService.getEmployeeCountForCompanyAndChildren(main, Collections.emptyList()));
    }

    @Test
    void getEmployeeCount_ForLeafCompany_ReturnsOwnCount() {
        Assertions.assertEquals(8, companyService.getEmployeeCountForCompanyAndChildren(developer, list));
    }

    @Test
    void getEmployeeCount_ForBranch_ReturnsSumOfBranch() {
        Assertions.assertEquals(18, companyService.getEmployeeCountForCompanyAndChildren(manager, list));
    }

    @Test
    void getEmployeeCount_ForRoot_ReturnsSumOfAllHierarchy() {
        Assertions.assertEquals(23, companyService.getEmployeeCountForCompanyAndChildren(main, list));
    }

    @Test
    void whenDeepHierarchy_ThenFindTopLevelParent() {
        Company level1 = new Company(null, 1);
        Company level2 = new Company(level1, 1);
        Company level3 = new Company(level2, 1);
        Company level4 = new Company(level3, 1);
        Assertions.assertEquals(level1, companyService.getTopLevelParent(level4));
    }

    @Test
    void getEmployeeCount_WhenCircularReferenceCheck_ReturnsSum() {
        Company head = new Company(null, 10);
        Company sub1 = new Company(head, 5);
        Company sub2 = new Company(head, 5);
        List<Company> miniList = List.of(head, sub1, sub2);
        Assertions.assertEquals(20, companyService.getEmployeeCountForCompanyAndChildren(head, miniList));
    }


}