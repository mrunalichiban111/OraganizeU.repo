// contactus.js

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('contactUsForm');
    const alertBox = document.getElementById('contactUsAlert');

    // CSRF token extraction (Spring Security)
    function getCsrfToken() {
        const meta = document.querySelector('meta[name="_csrf"]');
        return meta ? meta.getAttribute('content') : '';
    }
    function getCsrfHeader() {
        const meta = document.querySelector('meta[name="_csrf_header"]');
        return meta ? meta.getAttribute('content') : 'X-CSRF-TOKEN';
    }

    form.addEventListener('submit', function (e) {
        e.preventDefault();
        alertBox.classList.add('d-none');
        alertBox.classList.remove('alert-success', 'alert-danger');

        const name = document.getElementById('contactName').value.trim();
        const email = document.getElementById('contactEmail').value.trim();
        const message = document.getElementById('contactMessage').value.trim();

        if (!name || !email || !message) {
            alertBox.textContent = 'All fields are required.';
            alertBox.classList.remove('d-none');
            alertBox.classList.add('alert-danger');
            return;
        }

        fetch('/api/contact', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [getCsrfHeader()]: getCsrfToken()
            },
            body: JSON.stringify({ name, email, message })
        })
        .then(response => {
            if (!response.ok) throw new Error('Failed to send message.');
            return response.json();
        })
        .then(data => {
            alertBox.textContent = 'Message sent successfully!';
            alertBox.classList.remove('d-none', 'alert-danger');
            alertBox.classList.add('alert-success');
            form.reset();
        })
        .catch(err => {
            alertBox.textContent = err.message || 'An error occurred.';
            alertBox.classList.remove('d-none', 'alert-success');
            alertBox.classList.add('alert-danger');
        });
    });
}); 