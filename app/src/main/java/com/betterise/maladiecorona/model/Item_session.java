package com.betterise.maladiecorona.model;

public class Item_session {
    private String   firstname,lastname,national_ID,patienttelephone,dob;
    private String patientgender,occupation,residence,nationality,province,district,sector,cell,village,Index,category,ascov_resulti
            ,ASCOV_diagnostic,rdt_result;

    public Item_session() {
    }

    public Item_session(String firstname, String lastname, String national_ID, String patienttelephone, String dob, String patientgender, String occupation, String residence, String nationality, String province, String district, String sector, String cell, String village, String index, String category, String ascov_resulti, String ASCOV_diagnostic, String rdt_result) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.national_ID = national_ID;
        this.patienttelephone = patienttelephone;
        this.dob = dob;
        this.patientgender = patientgender;
        this.occupation = occupation;
        this.residence = residence;
        this.nationality = nationality;
        this.province = province;
        this.district = district;
        this.sector = sector;
        this.cell = cell;
        this.village = village;
        Index = index;
        this.category = category;
        this.ascov_resulti = ascov_resulti;
        this.ASCOV_diagnostic = ASCOV_diagnostic;
        this.rdt_result = rdt_result;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getNational_ID() {
        return national_ID;
    }

    public void setNational_ID(String national_ID) {
        this.national_ID = national_ID;
    }

    public String getPatienttelephone() {
        return patienttelephone;
    }

    public void setPatienttelephone(String patienttelephone) {
        this.patienttelephone = patienttelephone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPatientgender() {
        return patientgender;
    }

    public void setPatientgender(String patientgender) {
        this.patientgender = patientgender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getIndex() {
        return Index;
    }

    public void setIndex(String index) {
        Index = index;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAscov_resulti() {
        return ascov_resulti;
    }

    public void setAscov_resulti(String ascov_resulti) {
        this.ascov_resulti = ascov_resulti;
    }

    public String getASCOV_diagnostic() {
        return ASCOV_diagnostic;
    }

    public void setASCOV_diagnostic(String ASCOV_diagnostic) {
        this.ASCOV_diagnostic = ASCOV_diagnostic;
    }

    public String getRdt_result() {
        return rdt_result;
    }

    public void setRdt_result(String rdt_result) {
        this.rdt_result = rdt_result;
    }
}
