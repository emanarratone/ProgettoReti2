let allFines = [];

// Logout (stesso pattern dashboard)
document.getElementById('logoutBtn')?.addEventListener('click', function () {
  fetch('/api/logout', { method: 'POST', credentials: 'same-origin' })
    .then(res => res.json().catch(() => ({})))
    .finally(() => { window.location.href = '/index.html'; });
});

// Controllo sessione / ruolo (se vuoi allinearlo alla dashboard)
fetch('/api/session')
  .then(res => res.json())
  .then(data => {
    if (!data.loggedIn) {
      window.location.href = "/index.html";
      return;
    }
    if (data.isAdmin) {
      document.body.classList.add('is-admin');
      const badge = document.getElementById('roleBadge');
      if (badge) badge.style.display = '';
    }
  })
  .catch(err => console.error("Errore nel recupero ruolo:", err));

// Carica elenco regioni per combo
fetch('/api/regions')
  .then(res => res.json())
  .then(data => {
    const sel = document.getElementById('filterRegion');
    if (!sel || !Array.isArray(data)) return;
    data.forEach(r => {
      const opt = document.createElement('option');
      // uso il NOME come value, coerente con row.nome_regione
      opt.value = r.nome;
      opt.textContent = r.nome;
      sel.appendChild(opt);
    });
  })
  .catch(err => console.error('Errore fetch /api/regions:', err));


function renderTable(rows) {
  const tbody   = document.getElementById('finesListTable');
  const counter = document.getElementById('finesCount');
  if (!tbody) return;

  tbody.innerHTML = '';
  rows.forEach(row => {
    const tr = document.createElement('tr');

    const statoLabel = row.pagato ? 'Pagata' : 'Da pagare';
    const statoClass = row.pagato ? 'text-success' : 'text-danger';

    tr.innerHTML = `
      <td>${row.id_multa}</td>
      <td>${row.nome_regione || ''}</td>
      <td>${row.targa}</td>
      <td>${row.data}</td>
      <td>€ ${Number(row.importo).toFixed(2)}</td>
      <td class="${statoClass}">${statoLabel}</td>
    `;
    tbody.appendChild(tr);
  });

  if (counter) {
    counter.textContent = `Mostrate ${rows.length} multe (totale ${allFines.length})`;
    counter.classList.remove('d-none');
  }
}

function applyFilters() {
  const region = document.getElementById('filterRegion').value;
  const plate  = document.getElementById('filterPlate').value.trim().toUpperCase();
  const from   = document.getElementById('filterFrom').value;
  const to     = document.getElementById('filterTo').value;
  const status = document.getElementById('filterStatus').value;

  let filtered = allFines.slice();

  if (region) {
    // confronta il nome regione dell’oggetto con il value della select
    filtered = filtered.filter(f => f.nome_regione === region);
  }

  if (plate) {
    filtered = filtered.filter(f =>
      f.targa && f.targa.toUpperCase().includes(plate)
    );
  }

  if (from) {
    filtered = filtered.filter(f => f.data >= from);
  }
  if (to) {
    filtered = filtered.filter(f => f.data <= to);
  }

  if (status === 'pagate') {
    filtered = filtered.filter(f => f.pagato === true);
  } else if (status === 'non_pagate') {
    filtered = filtered.filter(f => f.pagato === false);
  }

  renderTable(filtered);
}

function resetFilters() {
  document.getElementById('filterRegion').value = '';
  document.getElementById('filterPlate').value  = '';
  document.getElementById('filterFrom').value   = '';
  document.getElementById('filterTo').value     = '';
  document.getElementById('filterStatus').value = '';
  renderTable(allFines);
}

// Carica inizialmente tutte le multe
fetch('/api/fines/list')
  .then(res => res.json())
  .then(data => {
    // ci si aspetta che ogni elemento abbia:
    // id_multa, targa, data (YYYY-MM-DD), importo, pagato, motivo,
    // id_regione, nome_regione
    allFines = Array.isArray(data) ? data : [];
    renderTable(allFines);
  })
  .catch(err => console.error('Errore fetch /api/fines/list:', err));

// Eventi filtri
document.getElementById('applyFilters')?.addEventListener('click', applyFilters);
document.getElementById('resetFilters')?.addEventListener('click', resetFilters);