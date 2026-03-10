package com.redko;

/*
@author   User
@project   lab1
@class  ICompanyService
@version  1.0.0
@since 10.03.2026 - 22.08
*/


import java.util.List;

public interface ICompanyService {

    /**

     * @param child - company for whom we are searching the top level parent

     *                  (parent of parent of ...)

     * @return top level paren

     */

    Company getTopLevelParent(Company child);



    /**

     *

     * @param company  - company for whom we are searching the count of employees

     *                 (count of this company employees +

     *                 count employees of all children and their children employees )

     * @param companies  - list of all available companies

     *

     * @return count of employees

     */

    long getEmployeeCountForCompanyAndChildren(Company company, List<Company> companies);



}


