// testimonials.js

// CSRF token setup
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

// Modal logic
const addTestimonialBtn = document.getElementById('addTestimonialBtn');
const testimonialModal = new bootstrap.Modal(document.getElementById('testimonialModal'));
const testimonialForm = document.getElementById('testimonialForm');
const testimonialUserName = document.getElementById('testimonialUserName');
const testimonialMessage = document.getElementById('testimonialMessage');
const testimonialError = document.createElement('div');
testimonialError.className = 'alert alert-danger';
testimonialError.style.display = 'none';
testimonialForm.prepend(testimonialError);

// Set user name in modal
function setUserName() {
    if (typeof currentUserName !== 'undefined' && currentUserName) {
        testimonialUserName.value = currentUserName;
    } else {
        testimonialUserName.value = 'Anonymous';
    }
}

addTestimonialBtn.addEventListener('click', function() {
    setUserName();
    testimonialError.style.display = 'none';
    // Fetch existing testimonial for user
    fetch('/api/testimonials/me')
        .then(res => res.ok ? res.json() : null)
        .then(data => {
            if (data && data.message) {
                testimonialMessage.value = data.message;
            } else {
                testimonialMessage.value = '';
            }
            testimonialModal.show();
        });
});

testimonialForm.addEventListener('submit', function(e) {
    e.preventDefault();
    testimonialError.style.display = 'none';
    fetch('/api/testimonials', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify({ message: testimonialMessage.value })
    })
    .then(res => {
        if (!res.ok) throw new Error('Failed to submit testimonial. Are you logged in?');
        return res.json();
    })
    .then(data => {
        testimonialModal.hide();
        loadTestimonials();
    })
    .catch(err => {
        testimonialError.textContent = err.message;
        testimonialError.style.display = 'block';
    });
});

function getOrdinalSuffix(day) {
    if (day > 3 && day < 21) return 'th';
    switch (day % 10) {
        case 1: return 'st';
        case 2: return 'nd';
        case 3: return 'rd';
        default: return 'th';
    }
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const day = date.getDate();
    const month = date.toLocaleString('default', { month: 'long' });
    const year = date.getFullYear();
    return `${day}${getOrdinalSuffix(day)} ${month} ${year}`;
}

// Load testimonials into carousel
function loadTestimonials() {
    fetch('/api/testimonials')
        .then(res => res.json())
        .then(data => {
            const carouselInner = document.getElementById('userTestimonial');
            const carouselIndicators = document.querySelector('.carousel-indicators');
            if (!carouselInner) return;
            carouselInner.innerHTML = '';
            if (carouselIndicators) carouselIndicators.innerHTML = '';
            data.forEach((t, i) => {
                // Prefer userName if it looks like a name, else fallback to email
                let displayName = t.userName;
                if (displayName && displayName.includes('@')) {
                    displayName = displayName.split('@')[0];
                }
                const item = document.createElement('div');
                item.className = 'carousel-item' + (i === 0 ? ' active' : '');
                item.innerHTML = `
                    <div class="card" style="width: 70vw;">
                        <div class="card-body">
                            <h5 class="card-title">${t.message}</h5>
                            <h6 class="card-subtitle mb-2">Posted by ${displayName} on ${formatDate(t.datePosted)}${t.dateEdited ? ' (edited)' : ''}</h6>
                        </div>
                    </div>
                `;
                carouselInner.appendChild(item);
                if (carouselIndicators) {
                    const li = document.createElement('li');
                    li.setAttribute('data-bs-target', '#carouselExampleIndicators');
                    li.setAttribute('data-bs-slide-to', i);
                    if (i === 0) li.className = 'active';
                    carouselIndicators.appendChild(li);
                }
            });
        });
}

document.addEventListener('DOMContentLoaded', function() {
    loadTestimonials();
}); 