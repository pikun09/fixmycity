package com.fixmycity.controller;

import com.fixmycity.model.Issue;
import com.fixmycity.service.GeminiService;
import com.fixmycity.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {
    @Autowired
    private IssueService issueService;

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        List<Issue> issues = issueService.getAllIssues();

        long totalIssues = issues.size();
        long resolved = issues.stream().filter(i -> "resolved".equalsIgnoreCase(i.getStatus())).count();
        long inProgress = issues.stream().filter(i -> "in-progress".equalsIgnoreCase(i.getStatus()) || "open".equalsIgnoreCase(i.getStatus())).count();

        // Calculate Category Breakdown
        Map<String, Long> byCategory = issues.stream()
                .filter(i -> i.getCategory() != null)
                .collect(Collectors.groupingBy(Issue::getCategory, Collectors.counting()));

        // Ensure key categories exist with at least 0 counts for visualization
        String[] defaultCategories = {"Pothole", "Water leakage", "Streetlight", "Waste management", "Other"};
        for (String cat : defaultCategories) {
            byCategory.putIfAbsent(cat, 0L);
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalIssues", totalIssues);
        summary.put("resolved", resolved);
        summary.put("inProgress", inProgress);
        summary.put("byCategory", byCategory);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/insights")
    public ResponseEntity<?> getInsights() {
        try {
            List<Issue> issues = issueService.getAllIssues();
            StringBuilder issueDataText = new StringBuilder();
            issueDataText.append("Here is the list of currently reported city infrastructure issues:\n");
            for (Issue issue : issues) {
                issueDataText.append(String.format("- Category: %s, Title: %s, Location: %s, Status: %s, Upvotes: %d\n",
                        issue.getCategory(), issue.getTitle(), issue.getLocation(), issue.getStatus(), issue.getUpvotes()));
            }

            String prompt = "You are a smart city urban planner analyzing civic issues for Bengaluru. " +
                            "Generate 3 short predictive insights or warnings about seasonal hazards, traffic bottleneck hotspots, or trash accumulation. " +
                            "Output as Markdown formatted lists.\n\n" + issueDataText.toString();

            String insightsText = geminiService.getPredictiveInsights(prompt);
            return ResponseEntity.ok(Map.of("text", insightsText));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("text", "Error loading predictive insights: " + e.getMessage()));
        }
    }
}
