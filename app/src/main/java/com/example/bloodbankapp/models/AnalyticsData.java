package com.example.bloodbankapp.models;

import java.util.Map;

public class AnalyticsData {
    
    public enum ChartType {
        BAR_CHART,
        PIE_CHART,
        LINE_CHART,
        METRIC_CARD
    }

    private String title;
    private String description;
    private ChartType chartType;
    private Map<String, Integer> data;
    private String insights;

    public AnalyticsData(String title, String description, ChartType chartType, Map<String, Integer> data) {
        this.title = title;
        this.description = description;
        this.chartType = chartType;
        this.data = data;
        this.insights = generateInsights();
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ChartType getChartType() { return chartType; }
    public Map<String, Integer> getData() { return data; }
    public String getInsights() { return insights; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setChartType(ChartType chartType) { this.chartType = chartType; }
    public void setData(Map<String, Integer> data) { 
        this.data = data; 
        this.insights = generateInsights();
    }
    public void setInsights(String insights) { this.insights = insights; }

    private String generateInsights() {
        if (data == null || data.isEmpty()) {
            return "No data available";
        }

        // Find max and min values
        int maxValue = 0;
        int minValue = Integer.MAX_VALUE;
        String maxKey = "";
        String minKey = "";

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
                maxKey = entry.getKey();
            }
            if (entry.getValue() < minValue) {
                minValue = entry.getValue();
                minKey = entry.getKey();
            }
        }

        // Generate insights based on chart type and data
        switch (chartType) {
            case BAR_CHART:
                if (title.contains("Blood Inventory")) {
                    if (minValue < 50) {
                        return "⚠️ Critical: " + minKey + " has only " + minValue + " units";
                    } else if (minValue < 100) {
                        return "🟡 Low stock: " + minKey + " needs attention";
                    } else {
                        return "🟢 Good stock levels across all blood types";
                    }
                } else if (title.contains("Time")) {
                    return "📊 Peak activity: " + maxKey + " (" + maxValue + "%)";
                }
                break;
                
            case PIE_CHART:
                if (title.contains("Request Status")) {
                    int total = data.values().stream().mapToInt(Integer::intValue).sum();
                    int pending = data.getOrDefault("Pending", 0);
                    double pendingRate = (double) pending / total * 100;
                    
                    if (pendingRate > 50) {
                        return "🚨 High pending rate: " + String.format("%.1f", pendingRate) + "%";
                    } else if (pendingRate > 25) {
                        return "🟡 Moderate pending rate: " + String.format("%.1f", pendingRate) + "%";
                    } else {
                        return "🟢 Good processing rate: " + String.format("%.1f", pendingRate) + "% pending";
                    }
                } else if (title.contains("Donor")) {
                    return "📈 Top donor blood type: " + maxKey + " (" + maxValue + "%)";
                }
                break;
        }

        return "📊 " + maxKey + " leads with " + maxValue + " units";
    }

    public int getTotalValue() {
        if (data == null) return 0;
        return data.values().stream().mapToInt(Integer::intValue).sum();
    }

    public String getMaxKey() {
        if (data == null || data.isEmpty()) return "";
        
        return data.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }

    public int getMaxValue() {
        if (data == null || data.isEmpty()) return 0;
        
        return data.values().stream()
                .max(Integer::compareTo)
                .orElse(0);
    }
}


