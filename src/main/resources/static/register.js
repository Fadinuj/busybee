function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
        return parts.pop().split(';').shift();
    }
    return null;
}

document.addEventListener('DOMContentLoaded', function () {
    const registrationForm = document.getElementById('registration-form');
    const errorMessageElement = document.getElementById('error-message');

    function showError(message) {
        errorMessageElement.textContent = message;
        errorMessageElement.classList.remove('hidden');
    }

    registrationForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        const username = document.getElementById('new-username').value.trim();
        const password = document.getElementById('new-password').value;

        const csrfToken = getCookie('XSRF-TOKEN');

        if (!csrfToken) {
            showError('Missing CSRF cookie. Refresh the page.');
            return;
        }

        try {
            const response = await fetch('/register', {
                method: 'POST',
                credentials: 'same-origin',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': csrfToken
                },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json().catch(() => ({}));

            if (response.ok) {
                window.location.href = data.redirectTo || '/login';
            } else if (response.status === 401) {
                showError(data.error || 'Registration failed.');
            } else if (response.status === 403) {
                showError('CSRF validation failed. Refresh the page and try again.');
            } else {
                showError(data.error || 'Unexpected error occurred.');
            }
        } catch (error) {
            showError('Failed to connect to the server.');
        }
    });
});