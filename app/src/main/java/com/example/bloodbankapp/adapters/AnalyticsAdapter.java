package com.example.bloodbankapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbankapp.R;
import com.example.bloodbankapp.models.AnalyticsData;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Map;

public class AnalyticsAdapter extends RecyclerView.Adapter<AnalyticsAdapter.AnalyticsViewHolder> {

    private final Context context;
    private final List<AnalyticsData> analyticsList;

    public AnalyticsAdapter(Context context, List<AnalyticsData> analyticsList) {
        this.context = context;
        this.analyticsList = analyticsList;
    }

    @NonNull
    @Override
    public AnalyticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_analytics, parent, false);
        return new AnalyticsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalyticsViewHolder holder, int position) {
        AnalyticsData data = analyticsList.get(position);

        holder.tvTitle.setText(data.getTitle());
        holder.tvDescription.setText(data.getDescription());
        holder.tvInsights.setText(data.getInsights());

        // Set chart type indicator
        String chartTypeText = getChartTypeText(data.getChartType());
        holder.tvChartType.setText(chartTypeText);

        // Create visual representation based on chart type
        setupChartVisualization(holder, data);
    }

    private String getChartTypeText(AnalyticsData.ChartType chartType) {
        switch (chartType) {
            case BAR_CHART:
                return "📊 Bar Chart";
            case PIE_CHART:
                return "🥧 Pie Chart";
            case LINE_CHART:
                return "📈 Line Chart";
            case METRIC_CARD:
                return "📋 Metric Card";
            default:
                return "📊 Chart";
        }
    }

    private void setupChartVisualization(AnalyticsViewHolder holder, AnalyticsData data) {
        Map<String, Integer> chartData = data.getData();
        
        if (chartData == null || chartData.isEmpty()) {
            holder.progressBar.setVisibility(View.GONE);
            holder.tvNoData.setVisibility(View.VISIBLE);
            return;
        }

        holder.progressBar.setVisibility(View.GONE);
        holder.tvNoData.setVisibility(View.GONE);

        // Calculate total for percentage calculations
        int total = data.getTotalValue();
        if (total == 0) {
            holder.progressBar.setVisibility(View.GONE);
            holder.tvNoData.setVisibility(View.VISIBLE);
            holder.tvChartData.setText("No data available");
            return;
        }

        // Create a simple bar chart representation
        StringBuilder chartText = new StringBuilder();
        int maxValue = data.getMaxValue();
        
        for (Map.Entry<String, Integer> entry : chartData.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            int percentage = (value * 100) / total;
            
            // Create visual bar
            int barLength = (value * 20) / maxValue; // Scale to max 20 characters
            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < barLength; i++) {
                bar.append("█");
            }
            
            // Add color coding for blood inventory
            String colorCode = getColorCode(key, value);
            
            chartText.append(colorCode)
                    .append(key)
                    .append(" ")
                    .append(bar.toString())
                    .append(" ")
                    .append(value)
                    .append(" units")
                    .append("\n");
        }

        holder.tvChartData.setText(chartText.toString());
        holder.tvChartData.setVisibility(View.VISIBLE);
    }

    private int getStatusColor(int units) {
        // Color coding for blood inventory
        if (units < 50) {
            return R.color.status_rejected_text; // Critical - Red
        } else if (units < 100) {
            return R.color.accent_orange; // Low - Orange
        } else {
            return R.color.status_success; // Good - Green
        }
    }

    private String getColorCode(String key, int value) {
        // Color coding for blood inventory
        if (value < 50) {
            return "🔴 "; // Critical
        } else if (value < 100) {
            return "🟡 "; // Low
        } else {
            return "🟢 "; // Good
        }
    }

    @Override
    public int getItemCount() {
        return analyticsList.size();
    }

    public static class AnalyticsViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvTitle, tvDescription, tvInsights, tvChartType, tvChartData, tvNoData;
        ProgressBar progressBar;

        public AnalyticsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_analytics);
            tvTitle = itemView.findViewById(R.id.tv_analytics_title);
            tvDescription = itemView.findViewById(R.id.tv_analytics_description);
            tvInsights = itemView.findViewById(R.id.tv_analytics_insights);
            tvChartType = itemView.findViewById(R.id.tv_chart_type);
            tvChartData = itemView.findViewById(R.id.tv_chart_data);
            tvNoData = itemView.findViewById(R.id.tv_no_data);
            progressBar = itemView.findViewById(R.id.progress_analytics);
        }
    }
}
