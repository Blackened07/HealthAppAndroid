package com.blackened.healthappfront.healthRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class HealthRecordResponseDTO {

    private Long id;

    private String type;

    private Double value1;

    private Double value2;

    private String note;
   /* //TODO: CREATE AT TIMESTAMP ABD DEVICE_OFFSET AT ZONE_OFFSET
    private Long TIMESTAMP;
    private Integer OFFSET_ZONE;*/
    private String timestamp;
    private String userName;

    public HealthRecordResponseDTO() {
    }

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
        return timestamp.length() >= 10 ? timestamp.substring(0, 10) : timestamp;
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

        if (timestamp == null || timestamp.isEmpty()) {
            return "";
        }

        try {
            LocalDateTime time = LocalDateTime.parse(timestamp);

            DateTimeFormatter formatter = DateTimeFormatter
                    .ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)
                    .withLocale(Locale.getDefault());

            return time.format(formatter);

        } catch (Exception e) {
            return getTimestamp();
        }
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
