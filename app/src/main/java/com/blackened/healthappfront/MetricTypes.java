package com.blackened.healthappfront;

import androidx.core.app.FrameMetricsAggregator;

public enum MetricTypes {
    BLOOD_PRESSURE("Давление"),
    GLUCOSE("Сахар"),
    WEIGHT("Масса тела"),
    TEMPERATURE("Температура"),
    CUSTOM("Своё");

    private final String metricText;

    MetricTypes(String metricText) {
        this.metricText = metricText;
    }

    public String getMetricText() {
        return metricText;
    }

    public static MetricTypes fromString(String text) {

        for (MetricTypes type : MetricTypes.values()) {
            if (type.getMetricText().equalsIgnoreCase(text)) {
                return type;
            }
        }

        return CUSTOM;
    }
}
