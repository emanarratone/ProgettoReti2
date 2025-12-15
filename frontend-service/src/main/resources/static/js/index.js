// Gestione della visibilità della password
document.querySelectorAll('.toggle-pwd').forEach(btn => {
  btn.addEventListener('click', () => {
    const targetId = btn.getAttribute('data-target');
    const input = document.getElementById(targetId);
    if (!input) return;

    const isPassword = input.type === 'password';
    input.type = isPassword ? 'text' : 'password';
    btn.setAttribute('aria-label', isPassword ? 'Nascondi password' : 'Mostra password');
  });
});

// Validazione dell'username
function validateUsername(username) {
  return /^[a-zA-Z][a-zA-Z0-9_.]{2,99}$/.test(username);
}

// Validazione della password
function validatePassword(password) {
  return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)\S{8,}$/.test(password);
}

// ====================== LOGIN ======================
const loginForm = document.getElementById('login-form');
if (loginForm) {
  loginForm.addEventListener('submit', async function (e) {
    e.preventDefault();

    const errorEl = loginForm.querySelector('.error');
    if (errorEl) errorEl.textContent = '';

    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value.trim();

    if (!username || !password) {
      if (errorEl) errorEl.textContent = 'Inserisci username e password';
      return;
    }

    try {
      const res = await fetch('/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });

      const data = await res.json().catch(() => ({}));

      if (res.ok && data.success) {
        // Salva utente se ti serve
        // sessionStorage.setItem('user', JSON.stringify(data.user));
        window.location.href = '/dashboard.html';
      } else {
        if (errorEl) {
          errorEl.textContent = data.error || 'Credenziali errate';
        }
      }
    } catch (err) {
      console.error('Errore login:', err);
      if (errorEl) errorEl.textContent = 'Errore di connessione';
    }
  });
}

// ================== REGISTRAZIONE ==================
const regForm = document.getElementById('register-form');
if (regForm) {
  regForm.addEventListener('submit', async function (e) {
    e.preventDefault(); // blocca il submit classico

    const errorEl = regForm.querySelector('.error');
    if (errorEl) errorEl.textContent = '';

    const username = document.getElementById('reg-username').value.trim();
    const pwd      = document.getElementById('reg-password').value.trim();
    const conf     = document.getElementById('reg-confirm').value.trim();
    const role     = regForm.querySelector('input[name="role"]:checked')?.value || '';

    if (!username || !pwd || !conf || !role) {
      if (errorEl) errorEl.textContent = 'Compila tutti i campi';
      return;
    }
    if (pwd !== conf) {
      if (errorEl) errorEl.textContent = 'Le password non coincidono';
      return;
    }
    if (!validateUsername(username)) {
      if (errorEl) errorEl.textContent = 'Username non valido (niente spazi, min 3 caratteri)';
      return;
    }
    if (!validatePassword(pwd)) {
      if (errorEl) {
        errorEl.textContent =
          'Password non valida (min 8 caratteri, maiuscola, minuscola, numero, niente spazi)';
      }
      return;
    }

    // mappiamo il ruolo su isAdmin: amministratore = true, impiegato = false
    const isAdmin = role === 'amministratore';

    const payload = {
      username: username,
      password: pwd,
      isAdmin: isAdmin
    };

    try {
      const res = await fetch('/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const data = await res.json().catch(() => ({}));

      if (res.ok && !data.error) {
        // Registrazione ok: torna alla scheda login
        regForm.reset();
        const loginRadio = document.getElementById('mode-login');
        if (loginRadio) loginRadio.checked = true;

        const loginError = document.querySelector('#login-form .error');
        if (loginError) loginError.textContent = 'Registrazione avvenuta, ora effettua il login.';
      } else {
        if (errorEl) {
          errorEl.textContent = data.error || 'Registrazione fallita';
        }
      }
    } catch (err) {
      console.error('Errore registrazione:', err);
      if (errorEl) errorEl.textContent = 'Errore di connessione';
    }
  });
}
