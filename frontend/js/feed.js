// js/feed.js

let allIssues = [];

async function loadFeed() {
    const container = document.getElementById('feed-container');
    container.innerHTML = '<div style="text-align: center; padding: 40px; color: var(--text-gray);">Loading community reports...</div>';
    
    try {
        const categoryFilter = document.getElementById('filter-category').value;
        const statusFilter = document.getElementById('filter-status').value;
        
        // Fetch issues from API
        allIssues = await getIssues(statusFilter);
        
        // Apply client side category filtering
        let filteredIssues = allIssues;
        if (categoryFilter !== 'ALL') {
            filteredIssues = allIssues.filter(issue => issue.category === categoryFilter);
        }
        
        if (filteredIssues.length === 0) {
            container.innerHTML = '<div style="text-align: center; padding: 40px; color: var(--text-muted);">No reports found matching selected filters.</div>';
            return;
        }
        
        container.innerHTML = '';
        
        filteredIssues.forEach(issue => {
            const card = document.createElement('div');
            card.className = 'issue-card';
            
            // Format dates
            let dateStr = 'Unknown date';
            if (issue.createdAt) {
                const date = new Date(issue.createdAt);
                dateStr = date.toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' });
            }
            
            // Image handling (fallback to placeholder gradient if no image)
            const imgHtml = issue.imageUrl 
                ? `<img src="${issue.imageUrl}" alt="${issue.title}">` 
                : `<div style="width:100%; height:100%; background: linear-gradient(135deg, #1e1b4b 0%, #311042 100%); display:flex; align-items:center; justify-content:center; color:var(--text-muted); font-size:0.8rem;"><svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" style="margin-bottom:8px; display:block; margin: 0 auto;"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>No Image</div>`;
            
            // AI Analysis block
            const aiAnalysisHtml = issue.aiAnalysis
                ? `<div class="issue-ai-analysis">
                     <header>🤖 Gemini AI Smart Scan</header>
                     <p>${issue.aiAnalysis.replace(/\n/g, '<br>')}</p>
                   </div>`
                : '';

            // Verification status
            const verifiedBadgeHtml = issue.verified 
                ? `<span class="issue-badge" style="background: rgba(16, 185, 129, 0.2); color: var(--success); border: 1px solid var(--success);">✓ Verified Report</span>` 
                : `<span class="issue-badge" style="background: rgba(148, 163, 184, 0.1); color: var(--text-gray); border: 1px solid var(--border-color);">Unverified</span>`;

            // Action buttons (Resolve button is active for status !== 'resolved')
            const resolveBtnHtml = issue.status !== 'resolved'
                ? `<button class="btn btn-outline" onclick="resolveIssue(${issue.id})" style="padding: 6px 14px; font-size: 0.8rem; border-color: var(--success); color: var(--success);"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg> Resolve</button>`
                : `<span style="color: var(--success); font-weight:600; font-size:0.85rem; display:flex; align-items:center; gap:4px;">✓ Resolved</span>`;

            card.innerHTML = `
                <div class="issue-image-container">
                    ${imgHtml}
                </div>
                <div class="issue-details">
                    <div class="issue-meta">
                        <span class="issue-badge category">${issue.category || 'Other'}</span>
                        <span class="issue-badge severity-${issue.severity || 'low'}">${issue.severity || 'low'}</span>
                        <span class="issue-badge status-${issue.status || 'open'}">${issue.status || 'open'}</span>
                        ${verifiedBadgeHtml}
                        <span>${dateStr}</span>
                    </div>
                    <h2>${issue.title}</h2>
                    <p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 8px; font-weight:600;"><i class="fa-solid fa-location-dot"></i> Location: ${issue.location || 'Unknown'}</p>
                    <p class="issue-desc">${issue.description}</p>
                    ${aiAnalysisHtml}
                    <div class="issue-actions">
                        <button class="btn btn-outline" onclick="upvoteIssue(${issue.id})" style="padding: 6px 14px; font-size: 0.8rem;">
                            👍 Upvote (${issue.upvotes || 0})
                        </button>
                        <div>
                            ${resolveBtnHtml}
                        </div>
                    </div>
                </div>
            `;
            
            container.appendChild(card);
        });

    } catch (err) {
        console.error('Error loading feed:', err);
        container.innerHTML = '<div style="text-align: center; padding: 40px; color: var(--danger);">Failed to load community feed. Make sure backend is running.</div>';
    }
}

// Function to upvote / verify report
async function upvoteIssue(id) {
    try {
        await verifyIssue(id);
        
        // Add points if logged in
        let currentUser = JSON.parse(localStorage.getItem('currentUser'));
        if (currentUser) {
            currentUser.points = (currentUser.points || 0) + 10; // Earn 10 XP for upvoting / verifying
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
        }
        
        loadFeed();
    } catch (err) {
        console.error('Error upvoting:', err);
    }
}

// Function to resolve issue
async function resolveIssue(id) {
    try {
        await updateIssueStatus(id, 'resolved');
        alert('Civic issue marked as Resolved. Auto-routed points to citizens.');
        loadFeed();
    } catch (err) {
        console.error('Error resolving:', err);
    }
}

function applyFilters() {
    loadFeed();
}

window.addEventListener('DOMContentLoaded', loadFeed);
