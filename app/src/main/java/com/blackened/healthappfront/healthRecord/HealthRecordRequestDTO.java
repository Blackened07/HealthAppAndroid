package com.blackened.healthappfront.healthRecord;

public class HealthRecordRequestDTO {

    private String type;
    private Double value1;
    private Double value2;
    private String note;

    public HealthRecordRequestDTO(String type, Double value1, Double value2, String note) {
        this.type = type;
        this.value1 = value1;
        this.value2 = value2;
        this.note = note;
    }
}
