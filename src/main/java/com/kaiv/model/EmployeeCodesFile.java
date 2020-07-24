package com.kaiv.model;

public class EmployeeCodesFile {

    private String number;
    private int persNumber;
    private String description;
    private String descriptionNameOnly;
    private String nameUkr;
    private String department;
    private String plantName;
    private String costCenter;

    public EmployeeCodesFile(String number, int persNumber, String description, String descriptionNameOnly, String nameUkr, String department, String plantName, String costCenter) {
        this.number = number;
        this.persNumber = persNumber;
        this.description = description;
        this.descriptionNameOnly = descriptionNameOnly;
        this.nameUkr = nameUkr;
        this.department = department;
        this.plantName = plantName;
        this.costCenter = costCenter;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getPersNumber() {
        return persNumber;
    }

    public void setPersNumber(int persNumber) {
        this.persNumber = persNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionNameOnly() {
        return descriptionNameOnly;
    }

    public void setDescriptionNameOnly(String descriptionNameOnly) {
        this.descriptionNameOnly = descriptionNameOnly;
    }

    public String getNameUkr() {
        return nameUkr;
    }

    public void setNameUkr(String nameUkr) {
        this.nameUkr = nameUkr;
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

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }
}
