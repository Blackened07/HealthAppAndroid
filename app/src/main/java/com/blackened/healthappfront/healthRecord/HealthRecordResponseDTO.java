package com.blackened.healthappfront.healthRecord;

public class HealthRecordResponseDTO {

    private Long id;
    private String type;
    private Double value1;
    private Double value2;
    private String note;
    private String timestamp;
    private String userName;

    public HealthRecordResponseDTO(Long id, String type, Double value1, Double value2, String note, String timestamp, String userName) {
        this.id = id;
        this.type = type;
        this.value1 = value1;
        this.value2 = value2;
        this.note = note;
        this.timestamp = timestamp;
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Double getValue1() {
        return value1;
    }

    public Double getValue2() {
        return value2;
    }

    public String getNote() {
        return note;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue1(Double value1) {
        this.value1 = value1;
    }

    public void setValue2(Double value2) {
        this.value2 = value2;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayDate() {
        return timestamp.substring(0, 10);
    }

    public String getDisplayValue() {
        if (value1 != null && value2 > 0) {
            return value1 + " / " + value2;
        }
        return String.valueOf(value1);
    }

    public String getDisplayType() {
        switch (type) {
            case "BLOOD_PRESSURE":
                return "Давление";
            case "GLUCOSE":
                return "Сахар";
            case "TEMPERATURE":
                return "Температура";
            case "WEIGHT":
                return "Масса тела";
            case "CUSTOM":
                return "Другое";
            default:
                return type;
        }
    }
}
