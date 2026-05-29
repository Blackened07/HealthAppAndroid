package com.blackened.healthappfront.healthRecord;

public class HealthRecordRequestDTO {

    private String type;
    private Double value1;
    private Double value2;
    private String note;

    private Long TIMESTAMP;
    private Integer OFFSET_ZONE;

    //TODO: CREATE AT TIMESTAMP ABD DEVICE_OFFSET AT ZONE_OFFSET

    public HealthRecordRequestDTO(String type, Double value1, Double value2, String note, Long TIMESTAMP, Integer OFFSET_ZONE) {
        this.type = type;
        this.value1 = value1;
        this.value2 = value2;
        this.note = note;
        this.TIMESTAMP = TIMESTAMP;
        this.OFFSET_ZONE = OFFSET_ZONE;
    }
}
