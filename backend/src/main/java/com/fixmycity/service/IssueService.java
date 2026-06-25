package com.fixmycity.service;

import com.fixmycity.model.Issue;
import com.fixmycity.model.User;
import com.fixmycity.repository.IssueRepository;
import com.fixmycity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IssueService {
    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeminiService geminiService;

    public Issue createIssue(String title, String description, String location, String severity, Long userId, MultipartFile image) {
        Issue issue = new Issue();
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setLocation(location);
        issue.setSeverity(severity);
        issue.setStatus("open");
        issue.setUpvotes(0);
        issue.setVerified(false);

        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> {
                issue.setReportedBy(user);
                user.setPoints(user.getPoints() + 50); // 50 points to reporter
                userRepository.save(user);
            });
        }

        // Generate mock coordinates in Bengaluru for map rendering (e.g. around 12.9716, 77.5946)
        double baseLat = 12.9716;
        double baseLng = 77.5946;
        double offsetLat = (Math.random() - 0.5) * 0.15;
        double offsetLng = (Math.random() - 0.5) * 0.15;
        issue.setLatitude(baseLat + offsetLat);
        issue.setLongitude(baseLng + offsetLng);

        // Save image if present
        if (image != null && !image.isEmpty()) {
            try {
                File uploadDir = new File("uploads");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                String originalFileName = image.getOriginalFilename();
                String fileExtension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                String newFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + fileExtension;
                File destFile = new File(uploadDir, newFileName);
                image.transferTo(destFile);

                issue.setImageUrl("http://localhost:8080/uploads/" + newFileName);

                // Call Gemini API
                String aiResult = geminiService.analyzeImage(image.getBytes(), image.getContentType());
                issue.setAiAnalysis(aiResult);

                // Auto-set category based on AI result
                String cleanAiResult = aiResult.toLowerCase();
                if (cleanAiResult.contains("pothole")) {
                    issue.setCategory("Pothole");
                } else if (cleanAiResult.contains("water") || cleanAiResult.contains("leak") || cleanAiResult.contains("drain")) {
                    issue.setCategory("Water leakage");
                } else if (cleanAiResult.contains("light") || cleanAiResult.contains("streetlamp") || cleanAiResult.contains("lamp")) {
                    issue.setCategory("Streetlight");
                } else if (cleanAiResult.contains("waste") || cleanAiResult.contains("garbage") || cleanAiResult.contains("trash")) {
                    issue.setCategory("Waste management");
                } else {
                    issue.setCategory("Other");
                }
            } catch (Exception e) {
                e.printStackTrace();
                issue.setCategory("Other");
                issue.setAiAnalysis("Gemini scan error: " + e.getMessage());
            }
        } else {
            // Text keyword matching fallback
            String combined = (title + " " + description).toLowerCase();
            if (combined.contains("pothole")) {
                issue.setCategory("Pothole");
            } else if (combined.contains("water") || combined.contains("leak") || combined.contains("drain")) {
                issue.setCategory("Water leakage");
            } else if (combined.contains("light") || combined.contains("streetlamp") || combined.contains("lamp")) {
                issue.setCategory("Streetlight");
            } else if (combined.contains("waste") || combined.contains("garbage") || combined.contains("trash")) {
                issue.setCategory("Waste management");
            } else {
                issue.setCategory("Other");
            }
            issue.setAiAnalysis("No image uploaded. Automatically classified based on ticket description text analysis.");
        }

        return issueRepository.save(issue);
    }

    public List<Issue> getAllIssues() {
        return issueRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Issue> getIssuesByStatus(String status) {
        return issueRepository.findByStatus(status);
    }

    public Optional<Issue> getIssueById(Long id) {
        return issueRepository.findById(id);
    }

    public Issue updateStatus(Long id, String status) {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent()) {
            Issue issue = issueOpt.get();
            issue.setStatus(status);
            return issueRepository.save(issue);
        }
        return null;
    }

    public Issue verifyIssue(Long id, Long userId) {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent()) {
            Issue issue = issueOpt.get();
            issue.setUpvotes(issue.getUpvotes() + 1);
            if (issue.getUpvotes() >= 3) {
                issue.setVerified(true);
            }

            // Award points to voter
            if (userId != null) {
                userRepository.findById(userId).ifPresent(voter -> {
                    voter.setPoints(voter.getPoints() + 10); // 10 points to voter
                    userRepository.save(voter);
                });
            }

            // Award points to reporter
            if (issue.getReportedBy() != null) {
                User reporter = issue.getReportedBy();
                reporter.setPoints(reporter.getPoints() + 15); // 15 points to reporter
                userRepository.save(reporter);
            }

            return issueRepository.save(issue);
        }
        return null;
    }
}
