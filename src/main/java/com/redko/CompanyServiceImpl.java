package com.redko;

/*
@author   User
@project   lab1
@class  CompanyServiceImpl
@version  1.0.0
@since 10.03.2026 - 22.10
*/


import java.util.List;

public class CompanyServiceImpl implements ICompanyService {

    @Override
    public Company getTopLevelParent(Company child) {
        if (child == null) {
            return null;
        }
        if (child.getParent() == null) {
            return child;
        }
        return getTopLevelParent(child.getParent());
    }

    @Override
    public long getEmployeeCountForCompanyAndChildren(Company company, List<Company> companies) {
        if (company == null) {
            return 0;
        }

        long totalCount = company.getEmployeesCount();

        if (companies == null || companies.isEmpty()) {
            return totalCount;
        }

        for (Company c : companies) {
            if (c.getParent() == company) {
                totalCount += getEmployeeCountForCompanyAndChildren(c, companies);
            }
        }

        return totalCount;
    }
}