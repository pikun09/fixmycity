package com.fixmycity.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    private String category; // Pothole, Water, Streetlight, etc.
    private String severity; // low, medium, high, critical
    private String status = "open"; // open, in-progress, resolved
    private String location;
    private Double latitude;
    private Double longitude;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "ai_analysis", columnDefinition = "TEXT")
    private String aiAnalysis; // Gemini AI response

    private Integer upvotes = 0;
    private Boolean verified = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User reportedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Issue() {
        this.status = "open";
        this.upvotes = 0;
        this.verified = false;
    }

    public Issue(String title, String description, String category, String severity, String location, Double latitude, Double longitude, User reportedBy) {
        this();
        this.title = title;
        this.description = description;
        this.category = category;
        this.severity = severity;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reportedBy = reportedBy;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAiAnalysis() { return aiAnalysis; }
    public void setAiAnalysis(String aiAnalysis) { this.aiAnalysis = aiAnalysis; }

    public Integer getUpvotes() { return upvotes; }
    public void setUpvotes(Integer upvotes) { this.upvotes = upvotes; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public User getReportedBy() { return reportedBy; }
    public void setReportedBy(User reportedBy) { this.reportedBy = reportedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
