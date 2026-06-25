// js/dashboard.js
async function loadDashboard() {
    try {
        const data = await getAnalytics();
        
        document.getElementById('total-issues').textContent = data.totalIssues;
        document.getElementById('resolved-count').textContent = data.resolved;
        document.getElementById('in-progress').textContent = data.inProgress;
        
        // 1. Chart.js Bar Chart for Category breakdown
        const ctxCategory = document.getElementById('categoryChart').getContext('2d');
        new Chart(ctxCategory, {
            type: 'bar',
            data: {
                labels: Object.keys(data.byCategory),
                datasets: [{
                    label: 'Number of Issues',
                    data: Object.values(data.byCategory),
                    backgroundColor: [
                        '#ef4444', // Pothole (Red)
                        '#3b82f6', // Water leakage (Blue)
                        '#eab308', // Streetlight (Yellow)
                        '#10b981', // Waste management (Green)
                        '#8b5cf6'  // Other (Purple)
                    ],
                    borderWidth: 0,
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    x: {
                        grid: {
                            color: 'rgba(255,255,255,0.05)'
                        },
                        ticks: {
                            color: '#94a3b8'
                        }
                    },
                    y: {
                        grid: {
                            color: 'rgba(255,255,255,0.05)'
                        },
                        ticks: {
                            color: '#94a3b8',
                            precision: 0
                        }
                    }
                }
            }
        });

        // 2. Chart.js Doughnut Chart for Status split
        const ctxStatus = document.getElementById('statusChart').getContext('2d');
        new Chart(ctxStatus, {
            type: 'doughnut',
            data: {
                labels: ['Resolved', 'In Progress / Open'],
                datasets: [{
                    data: [data.resolved, data.inProgress],
                    backgroundColor: [
                        '#10b981', // Success Green
                        '#f59e0b'  // Warning Orange
                    ],
                    borderColor: '#0f172a',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            color: '#94a3b8',
                            padding: 20
                        }
                    }
                },
                cutout: '70%'
            }
        });

    } catch (err) {
        console.error('Error loading dashboard stats:', err);
        document.getElementById('total-issues').textContent = 'Error';
        document.getElementById('resolved-count').textContent = 'Error';
        document.getElementById('in-progress').textContent = 'Error';
    }

    // 3. Load Gemini AI Insights
    try {
        const insights = await getInsights();
        // Convert Markdown style bullet points to HTML
        let formattedInsights = insights.text;
        if (formattedInsights) {
            // Replace newlines and lists with clean HTML paragraphs/lists
            formattedInsights = formattedInsights
                .replace(/\n\n/g, '<br><br>')
                .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
                .replace(/\* (.*?)/g, '<li>$1</li>');
        }
        document.getElementById('ai-insights').innerHTML = formattedInsights || 'No insights available.';
    } catch (err) {
        console.error('Error loading AI insights:', err);
        document.getElementById('ai-insights').textContent = 'Could not generate predictive insights. Make sure the backend service is running.';
    }
}

loadDashboard();
