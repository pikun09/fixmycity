// js/api.js
const API_BASE = 'http://localhost:8080/api';

// ── Fetch all issues ──
async function getIssues(status = '') {
    const url = status && status !== 'ALL' ? `${API_BASE}/issues?status=${status}` : `${API_BASE}/issues`;
    const response = await fetch(url);
    return await response.json();
}

// ── Submit new issue with image ──
async function submitIssue(formData) {
    const response = await fetch(`${API_BASE}/issues`, {
        method: 'POST',
        body: formData // FormData handles multipart automatically
    });
    return await response.json();
}

// ── Verify/upvote an issue ──
async function verifyIssue(issueId) {
    const user = JSON.parse(localStorage.getItem('currentUser'));
    const url = user ? `${API_BASE}/issues/${issueId}/verify?userId=${user.id}` : `${API_BASE}/issues/${issueId}/verify`;
    const response = await fetch(url, {
        method: 'POST'
    });
    return await response.json();
}

// ── Update issue status ──
async function updateIssueStatus(issueId, status) {
    const response = await fetch(`${API_BASE}/issues/${issueId}/status?status=${status}`, {
        method: 'PATCH'
    });
    return await response.json();
}

// ── Get dashboard analytics ──
async function getAnalytics() {
    const response = await fetch(`${API_BASE}/analytics/summary`);
    return await response.json();
}

// ── Get Gemini AI insights ──
async function getInsights() {
    const response = await fetch(`${API_BASE}/analytics/insights`);
    return await response.json();
}

// ── Get leaderboard ──
async function getLeaderboard() {
    const response = await fetch(`${API_BASE}/users/leaderboard`);
    return await response.json();
}

// ── Register citizen account ──
async function registerUser(user) {
    const response = await fetch(`${API_BASE}/users/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user)
    });
    if (!response.ok) {
        const err = await response.json();
        throw new Error(err.message || 'Registration failed');
    }
    return await response.json();
}

// ── Login user ──
async function loginUser(credentials) {
    const response = await fetch(`${API_BASE}/users/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials)
    });
    if (!response.ok) {
        const err = await response.json();
        throw new Error(err.message || 'Login failed');
    }
    return await response.json();
}
