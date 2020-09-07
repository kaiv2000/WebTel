package com.kaiv.service;

import com.kaiv.model.Employee;
import com.kaiv.model.EmployeeWithAdditionalInfo;

import java.util.List;
import java.util.Map;

public interface EmployeeService {

    Map<Integer, Employee> getEmployeeListNewSession();

    Map<Integer, Employee> getAllEmployees();

    EmployeeWithAdditionalInfo searchEmployeesByName(String inputNameOrNumber);

    List<String> processCommand(String getCommand, String searchString);

    boolean isPermissionIsGranted();

    boolean isDataLoaded();


}
