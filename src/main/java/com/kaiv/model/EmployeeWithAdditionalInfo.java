package com.kaiv.model;

import java.util.List;
import java.util.Set;

public class EmployeeWithAdditionalInfo {

    private List<Employee> foundedEmployeeList;
    private Set<String> additionalInfo;

    public EmployeeWithAdditionalInfo(List<Employee> foundedEmployeeList, Set<String> additionalInfo) {
        this.foundedEmployeeList = foundedEmployeeList;
        this.additionalInfo = additionalInfo;
    }

    public List<Employee> getFoundedEmployeeList() {
        return foundedEmployeeList;
    }

    public void setFoundedEmployeeList(List<Employee> foundedEmployeeList) {
        this.foundedEmployeeList = foundedEmployeeList;
    }

    public Set<String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Set<String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
