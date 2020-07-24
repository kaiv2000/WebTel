package com.kaiv.model;

public class EmployeeKaba {

    private int persNumber;
    private String fullName;
    private String login;
    private String email;
    private String costCenter;
    private String position;
    private String department;
    private String plantName;
    private String hiredDate;
    private String dateOfBirth;
    private String privatePhoneNum;

    public EmployeeKaba(int persNumber, String fullName, String login, String email, String costCenter, String position, String department, String plantName, String hiredDate, String dateOfBirth, String privatePhoneNum) {
        this.persNumber = persNumber;
        this.fullName = fullName;
        this.login = login;
        this.email = email;
        this.costCenter = costCenter;
        this.position = position;
        this.department = department;
        this.plantName = plantName;
        this.hiredDate = hiredDate;
        this.dateOfBirth = dateOfBirth;
        this.privatePhoneNum = privatePhoneNum;
    }

    public String getPrivatePhoneNum() {
        return privatePhoneNum;
    }

    public void setPrivatePhoneNum(String privatePhoneNum) {
        this.privatePhoneNum = privatePhoneNum;
    }

    public int getPersNumber() {
        return persNumber;
    }

    public void setPersNumber(int persNumber) {
        this.persNumber = persNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
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
}
