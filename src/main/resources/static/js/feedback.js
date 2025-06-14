// feedback.js

document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('#fbform form');
    if (!form) return;
    const alertBox = document.createElement('div');
    alertBox.className = 'alert d-none';
    alertBox.setAttribute('role', 'alert');
    form.insertBefore(alertBox, form.querySelector('.text-end'));

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

        const name = document.getElementById('name').value.trim();
        const email = document.getElementById('email').value.trim();
        const firstVisit = document.getElementById('first-visit-yes').checked;
        const foundWanted = document.getElementById('find-yes').checked;

        // Satisfaction (emoji button group)
        let satisfaction = '';
        document.querySelectorAll('.satisfaction-options')[0].querySelectorAll('input[type="button"]').forEach(btn => {
            if (btn.classList.contains('active')) satisfaction = btn.value;
        });
        // Return likelihood (emoji button group)
        let returnLikelihood = '';
        document.querySelectorAll('.satisfaction-options')[1].querySelectorAll('input[type="button"]').forEach(btn => {
            if (btn.classList.contains('active')) returnLikelihood = btn.value;
        });
        const comments = document.getElementById('comments').value.trim();

        if (!name || !email || !satisfaction || !returnLikelihood) {
            alertBox.textContent = 'Please fill all required fields.';
            alertBox.classList.remove('d-none');
            alertBox.classList.add('alert-danger');
            return;
        }

        fetch('/api/feedback', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [getCsrfHeader()]: getCsrfToken()
            },
            body: JSON.stringify({
                name,
                email,
                firstVisit,
                foundWanted,
                satisfaction,
                returnLikelihood,
                comments
            })
        })
        .then(response => {
            if (!response.ok) throw new Error('Failed to submit feedback.');
            return response.json();
        })
        .then(data => {
            alertBox.textContent = 'Thank you for your feedback!';
            alertBox.classList.remove('d-none', 'alert-danger');
            alertBox.classList.add('alert-success');
            form.reset();
            // Remove active class from emoji buttons
            document.querySelectorAll('.satisfaction-options input[type="button"]').forEach(btn => btn.classList.remove('active'));
        })
        .catch(err => {
            alertBox.textContent = err.message || 'An error occurred.';
            alertBox.classList.remove('d-none', 'alert-success');
            alertBox.classList.add('alert-danger');
        });
    });

    // Emoji button group logic
    document.querySelectorAll('.satisfaction-options').forEach(group => {
        group.querySelectorAll('input[type="button"]').forEach(btn => {
            btn.addEventListener('click', function () {
                group.querySelectorAll('input[type="button"]').forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
            });
        });
    });
}); 