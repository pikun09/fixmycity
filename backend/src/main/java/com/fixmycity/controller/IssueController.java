package com.fixmycity.controller;

import com.fixmycity.model.Issue;
import com.fixmycity.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "*")
public class IssueController {
    @Autowired
    private IssueService issueService;

    // GET all issues (with optional ?status filter)
    @GetMapping
    public List<Issue> getAllIssues(@RequestParam(required = false) String status) {
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            return issueService.getIssuesByStatus(status);
        }
        return issueService.getAllIssues();
    }

    // POST new issue with optional image and userId
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Issue> reportIssue(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam String severity,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) MultipartFile image) {
        Issue issue = issueService.createIssue(title, description, location, severity, userId, image);
        return ResponseEntity.ok(issue);
    }

    // PATCH update status
    @PatchMapping("/{id}/status")
    public Issue updateStatus(@PathVariable Long id, @RequestParam String status) {
        return issueService.updateStatus(id, status);
    }

    // POST upvote / verify with optional voter userId
    @PostMapping("/{id}/verify")
    public Issue verify(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        return issueService.verifyIssue(id, userId);
    }
}
