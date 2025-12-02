    // Gestione LOGIN via fetch
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
      loginForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const formData = new FormData(loginForm);
        const errorEl = loginForm.querySelector('.error');
        if (errorEl) errorEl.textContent = '';

        fetch('/api/login', {
          method: 'POST',
          body: formData,
          credentials: 'same-origin'
        })
          .then(res => res.json())
          .then(data => {
            if (data.status === 'ok') {
              window.location.href = '/dashboard.html';
            } else {
              if (errorEl) {
                errorEl.textContent = data.error || 'Login fallito';
              }
            }
          })
          .catch(err => {
            console.error('Errore login:', err);
            if (errorEl) {
              errorEl.textContent = 'Errore di connessione';
            }
          });
      });
    }

    // Gestione REGISTRAZIONE via fetch
    const regForm = document.getElementById('register-form');
    if (regForm) {
      regForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const formData = new FormData(regForm);
        const errorEl = regForm.querySelector('.error');
        if (errorEl) errorEl.textContent = '';

        const pwd = regForm.querySelector('#reg-password')?.value || '';
        const conf = regForm.querySelector('#reg-confirm')?.value || '';
        if (pwd !== conf) {
          if (errorEl) errorEl.textContent = 'Le password non coincidono';
          return;
        }

        fetch('/api/register', {
          method: 'POST',
          body: formData,
          credentials: 'same-origin'
        })
          .then(res => res.json())
          .then(data => {
            if (data.status === 'ok') {
              window.location.href = '/dashboard.html';
            } else {
              if (errorEl) {
                errorEl.textContent = data.error || 'Registrazione fallita';
              }
            }
          })
          .catch(err => {
            console.error('Errore registrazione:', err);
            if (errorEl) {
              errorEl.textContent = 'Errore di connessione';
            }
          });
      });
    }