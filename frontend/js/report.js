// js/report.js

// Handle file input change and show preview
document.getElementById('image').addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = (ev) => {
            const preview = document.getElementById('preview');
            preview.src = ev.target.result;
            preview.style.display = 'block';
        };
        reader.readAsDataURL(file);
    }
});

// Handle form submission
document.getElementById('report-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const submitButton = e.target.querySelector('button[type="submit"]');
    const originalBtnText = submitButton.innerHTML;

    // Display loader in submit button
    submitButton.disabled = true;
    submitButton.innerHTML = 'Uploading & Scanning with Gemini...';

    // Show AI status panel
    const aiResultPanel = document.getElementById('ai-result');
    const aiResultText = document.getElementById('ai-result-text');
    aiResultPanel.style.display = 'block';
    aiResultText.innerHTML = '<span class="fa-spin" style="display:inline-block; animation:spin 1s infinite linear; margin-right:8px;">♻️</span> Analyzing image and auto-assigning ward details via Gemini 2.0 Flash...';

    const formData = new FormData();
    formData.append('title', document.getElementById('title').value);
    formData.append('description', document.getElementById('description').value);
    formData.append('location', document.getElementById('location').value);
    formData.append('severity', document.getElementById('severity').value);

    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    if (currentUser && currentUser.id) {
        formData.append('userId', currentUser.id);
    }

    const imageFile = document.getElementById('image').files[0];
    if (imageFile) {
        formData.append('image', imageFile);
    }

    try {
        const result = await submitIssue(formData);
        
        // Show AI analysis result
        aiResultText.innerHTML = result.aiAnalysis ? result.aiAnalysis.replace(/\n/g, '<br>') : 'Issue created with text analysis classification.';
        
        // Award XP points to citizen if logged in
        let currentUser = JSON.parse(localStorage.getItem('currentUser'));
        if (currentUser) {
            currentUser.points = (currentUser.points || 0) + 50;
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
        }

        alert('Issue reported successfully! +50 XP Reputation Points earned.');
        
        // Short delay for reading AI results, then redirect to community feed
        setTimeout(() => {
            window.location.href = 'feed.html';
        }, 3000);

    } catch (err) {
        console.error('Submission error:', err);
        alert('Error submitting issue. Please make sure the backend is running and database is active.');
        submitButton.disabled = false;
        submitButton.innerHTML = originalBtnText;
        aiResultPanel.style.display = 'none';
    }
});
