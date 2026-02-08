/**
 * GESTIONE MULTE - Script di aggregazione dati dai Microservizi
 */

let allFines = [];

// 1. GESTIONE SESSIONE E LOGOUT
document.getElementById('logoutBtn')?.addEventListener('click', function () {
    fetch('/api/logout', { method: 'POST', credentials: 'same-origin' })
        .then(res => res.json().catch(() => ({})))
        .finally(() => { window.location.href = '/index'; });
});

fetch('/api/session')
    .then(res => res.json())
    .then(data => {
        if (!data.loggedIn) {
            window.location.href = "/index";
            return;
        }
        if (data.isAdmin) {
            document.body.classList.add('is-admin');
            const badge = document.getElementById('roleBadge');
            if (badge) badge.style.display = '';
        }
    })
    .catch(err => console.error("Errore sessione:", err));

// 2. CARICAMENTO REGIONI (per il filtro)
fetch('/api/regions')
    .then(res => res.json())
    .then(data => {
        const sel = document.getElementById('filterRegion');
        if (!sel || !Array.isArray(data)) return;
        data.forEach(r => {
            const opt = document.createElement('option');
            opt.value = r.nome;
            opt.textContent = r.nome;
            sel.appendChild(opt);
        });
    })
    .catch(err => console.error('Errore fetch regioni:', err));

// 3. RECUPERO DATI AGGREGATI (Triple Join: Multa + Pagamento + Regione)
function loadFines() {
    // Puntiamo al nuovo endpoint del Gateway che chiama l'Aggregator nel Multa Service
    fetch('/api/fines/management')
        .then(res => res.json())
        .then(data => {
            // Allineamento con il DTO: id, nomeRegione, targa, data, importo, stato
            allFines = Array.isArray(data) ? data : [];
            renderTable(allFines);
        })
        .catch(err => {
            console.error('Errore caricamento multe aggregate:', err);
            const tbody = document.getElementById('finesListTable');
            if (tbody) tbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Errore nel recupero dati dai microservizi</td></tr>';
        });
}

// 4. RENDERING DELLA TABELLA
function renderTable(rows) {
    const tbody = document.getElementById('finesListTable');
    const counter = document.getElementById('finesCount');
    if (!tbody) return;

    tbody.innerHTML = '';

    if (rows.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Nessuna multa trovata con i filtri selezionati</td></tr>';
        return;
    }

    rows.forEach(row => {
        const tr = document.createElement('tr');

        // Logica dinamica per lo stato (Pagato/Pendente/Offline)
        const isPagata = row.stato === 'PAGATO' || row.stato === 'PAGATA';
        const isOffline = row.stato === 'OFFLINE' || row.stato === 'Vedi Locale';

        let badgeClass = 'bg-danger';
        if (isPagata) badgeClass = 'bg-success';
        if (isOffline) badgeClass = 'bg-warning text-dark';

        tr.innerHTML = `
            <td><strong>#${row.id}</strong></td>
            <td>
                <div class="fw-bold">${row.nomeRegione}</div>
                <div class="small text-muted"><i class="bi bi-geo-alt"></i> ${row.nomeCasello}</div>
            </td>
            <td><span class="badge bg-light text-dark border">${row.targa}</span></td>
            <td><small>${row.data}</small></td>
            <td><strong>€ ${row.importo.toFixed(2)}</strong></td>
            <td><span class="badge ${badgeClass}">${row.stato}</span></td>
        `;
        tbody.appendChild(tr);
    });

    if (counter) {
        counter.textContent = `Mostrate ${rows.length} multe (totale caricato: ${allFines.length})`;
        counter.classList.remove('d-none');
    }
}

// 5. SISTEMA DI FILTRAGGIO (lato client per velocità)
function applyFilters() {
    const region = document.getElementById('filterRegion').value;
    const plate  = document.getElementById('filterPlate').value.trim().toUpperCase();
    const from   = document.getElementById('filterFrom').value;
    const to     = document.getElementById('filterTo').value;
    const status = document.getElementById('filterStatus').value;

    let filtered = allFines.slice();

    if (region) {
        filtered = filtered.filter(f => f.nomeRegione === region);
    }

    if (plate) {
        filtered = filtered.filter(f => f.targa && f.targa.toUpperCase().includes(plate));
    }

    if (from) {
        filtered = filtered.filter(f => f.data >= from);
    }

    if (to) {
        filtered = filtered.filter(f => f.data <= to);
    }

    if (status === 'pagate') {
        filtered = filtered.filter(f => f.stato === 'PAGATO' || f.stato === 'PAGATA');
    } else if (status === 'non_pagate') {
        filtered = filtered.filter(f => f.stato !== 'PAGATO' && f.stato !== 'PAGATA');
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

// 6. AVVIO E EVENT LISTENER
document.addEventListener('DOMContentLoaded', () => {
    loadFines();

    document.getElementById('applyFilters')?.addEventListener('click', applyFilters);
    document.getElementById('resetFilters')?.addEventListener('click', resetFilters);
});