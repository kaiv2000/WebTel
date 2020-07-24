package com.kaiv.model;

public class EmployeeMobileFile {

    private String persNumberFromWord;
    private String nameFromWord;
    private String mobileNumberFromWord;

    public EmployeeMobileFile(String persNumberFromWord, String nameFromWord, String mobileNumberFromWord) {
        this.persNumberFromWord = persNumberFromWord;
        this.nameFromWord = nameFromWord;
        this.mobileNumberFromWord = mobileNumberFromWord;
    }

    public String getPersNumberFromWord() {
        return persNumberFromWord;
    }

    public void setPersNumberFromWord(String persNumberFromWord) {
        this.persNumberFromWord = persNumberFromWord;
    }

    public String getNameFromWord() {
        return nameFromWord;
    }

    public void setNameFromWord(String nameFromWord) {
        this.nameFromWord = nameFromWord;
    }

    public String getMobileNumberFromWord() {
        return mobileNumberFromWord;
    }

    public void setMobileNumberFromWord(String mobileNumberFromWord) {
        this.mobileNumberFromWord = mobileNumberFromWord;
    }
}
