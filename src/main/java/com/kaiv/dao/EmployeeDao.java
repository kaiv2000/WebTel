package com.kaiv.dao;

import java.util.List;

public interface EmployeeDao {

    List<String> processCommand(String getCommand, String searchString);

    String getCurrentlyLoggedUser(String computerName);

}
