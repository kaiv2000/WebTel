package com.kaiv.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Employee {

    private int id;
    private String telNumber;
    private String name;
    private String department;
    private List<String> allMobileTelNumbers;
    private String description;
    private String persNumber;
    private String plantName;
    private String login;
    private String email;
    private String costCenter;
    private String position;
    private String photoLink;
    private String hiredDate;
    private String dateOfBirth;
    private String privatePhoneNum;
    private List<String> phoneNumbersWithDescription;

    public Employee(int id, String telNumber, String name, String department, List<String> allMobileTelNumbers, String description, String persNumber, String plantName, String login, String email, String costCenter, String position, String photoLink, String hiredDate, String dateOfBirth, String privatePhoneNum) {
        this.id = id;
        this.telNumber = telNumber;
        this.name = name;
        this.department = department;
        this.allMobileTelNumbers = allMobileTelNumbers;
        this.description = description;
        this.persNumber = persNumber;
        this.plantName = plantName;
        this.login = login;
        this.email = email;
        this.costCenter = costCenter;
        this.position = position;
        this.photoLink = photoLink;
        this.hiredDate = hiredDate;
        this.dateOfBirth = dateOfBirth;
        this.privatePhoneNum = privatePhoneNum;
    }

    public Employee(int id, String name, String department, List<String> allMobileTelNumbers, String persNumber, String plantName, String login, String email, String costCenter, String position, String photoLink, String hiredDate, String dateOfBirth, String privatePhoneNum, List<String> phoneNumbersWithDescription) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.allMobileTelNumbers = allMobileTelNumbers;
        this.persNumber = persNumber;
        this.plantName = plantName;
        this.login = login;
        this.email = email;
        this.costCenter = costCenter;
        this.position = position;
        this.photoLink = photoLink;
        this.hiredDate = hiredDate;
        this.dateOfBirth = dateOfBirth;
        this.privatePhoneNum = privatePhoneNum;
        this.phoneNumbersWithDescription = phoneNumbersWithDescription;

    }

    public Employee(int id, String telNumber, String name, String department, List<String> allMobileTelNumbers, String description, String persNumber, String plantName, String login, String email, String costCenter, String position, String photoLink) {
        this.id = id;
        this.telNumber = telNumber;
        this.name = name;
        this.department = department;
        this.allMobileTelNumbers = allMobileTelNumbers;
        this.description = description;
        this.persNumber = persNumber;
        this.plantName = plantName;
        this.login = login;
        this.email = email;
        this.costCenter = costCenter;
        this.position = position;
        this.photoLink = photoLink;
    }

    public String getPrivatePhoneNum() {
        return privatePhoneNum;
    }

    public void setPrivatePhoneNum(String privatePhoneNum) {
        this.privatePhoneNum = privatePhoneNum;
    }

    public String getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(String hiredDate) {
        this.hiredDate = hiredDate;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<String> getPhoneNumbersWithDescription() {
        return phoneNumbersWithDescription;
    }

    public void setPhoneNumbersWithDescription(List<String> phoneNumbersWithDescription) {
        this.phoneNumbersWithDescription = phoneNumbersWithDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<String> getAllMobileTelNumbers() {
        return allMobileTelNumbers;
    }

    public void setAllMobileTelNumbers(List<String> allMobileTelNumbers) {
        this.allMobileTelNumbers = allMobileTelNumbers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPersNumber() {
        return persNumber;
    }

    public void setPersNumber(String persNumber) {
        this.persNumber = persNumber;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAllMobileTelNumbersString() {
        return String.join(",", allMobileTelNumbers);
    }

    public static Comparator<Employee> getListByPerId() {
        Comparator comp = new Comparator<Employee>() {
            @Override
            public int compare(Employee s1, Employee s2) {
                return s1.persNumber.compareTo(s2.persNumber);
            }
        };
        return comp;
    }

    public static Comparator<Employee> getListByPerTelNumber() {
        Comparator comp = new Comparator<Employee>() {
            @Override
            public int compare(Employee s1, Employee s2) {
                return s1.telNumber.compareTo(s2.telNumber);
            }
        };
        return comp;
    }

    @Override
    public String toString() {
        return id +
                telNumber +
                name +
                department +
                allMobileTelNumbers +
                description +
                persNumber +
                plantName +
                login +
                email +
                costCenter +
                position +
                photoLink +
                phoneNumbersWithDescription;
    }

    public List<String> getEmployeeAsList() {
        List<String> employeeAsList = new ArrayList<>();
        employeeAsList.add(telNumber);
        employeeAsList.add(name);
        employeeAsList.add(department);
        employeeAsList.add(String.valueOf(allMobileTelNumbers));
        employeeAsList.add(description);
        employeeAsList.add(persNumber);
        employeeAsList.add(plantName);
        employeeAsList.add(login);
        employeeAsList.add(email);
        employeeAsList.add(costCenter);
        employeeAsList.add(position);
        return employeeAsList;
    }
}
