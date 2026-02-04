  // Logout
document.getElementById('logoutBtn').addEventListener('click', function () {
  fetch('/api/logout', {
    method: 'POST',
    credentials: 'same-origin'
  })
    .then(res => {
      if (!res.ok) {
        throw new Error('HTTP ' + res.status);
      }
      // anche se è JSON, asdrubale noi basta sapere che è ok
      return res.json().catch(() => ({}));
    })
    .then(() => {
      // dopo invalidazione sessione, vai alla pagina di login
      window.location.href = '/index.html';
    })
    .catch(err => {
      console.error('Errore nel logout:', err);
      // fallback comunque alla login
      window.location.href = '/index.html';
    });
});


    // Controlla se utente è admin e mostra/nasconde bottoni
    fetch('/api/session')
      .then(res => res.json())
      .then(data => {
        if (!data.loggedIn) {
          window.location.href = "/index.html";
          return;
        }
        const adminTools = document.querySelectorAll('.admin-tools');
        if (data.isAdmin) {
          adminTools.forEach(el => el.style.display = '');
          document.body.classList.add('is-admin');
          document.getElementById('roleBadge').style.display = '';
        } else {
          adminTools.forEach(el => el.style.display = 'none');
          document.body.classList.remove('is-admin');
          document.getElementById('roleBadge').style.display = 'none';
        }
      })
      .catch(err => console.error("Errore nel recupero ruolo:", err));

    // KPI traffico
 // SOSTITUISCI QUESTA FUNZIONE:
 fetch('/api/tickets/traffic')
   .then(res => {
     console.log('HTTP Status:', res.status);  // DEBUG
     if (!res.ok) throw new Error('HTTP ' + res.status);
     return res.json();
   })
   .then(data => {
     console.log('JSON ricevuto:', data);  // ← CRUCIALE!

     // Fix NaN + formato italiano
     const media = parseInt(data.media || 0);
     document.getElementById("kpiTrafficAvg").innerHTML =
       media > 0 ? `<strong>${media}</strong>` : "0";

     console.log('Media mostrata:', media);
   })
   .catch(err => {
     console.error('Fetch errore:', err);
     document.getElementById("kpiTrafficAvg").textContent = "0";
   });


    // KPI asset: caselli, corsie, dispositivi
    fetch('/api/assets')
      .then(res => {
        if (!res.ok) {
          throw new Error('Errore HTTP: ' + res.status);
        }
        return res.json();
      })
      .then(data => {
        if (data.error) {
          console.error("Errore API assets:", data.error);
          document.getElementById("kpiPeak").textContent = "N/D";
          document.getElementById("kpiLanes").textContent = "N/D";
          document.getElementById("kpiToll").textContent = "N/D";
          return;
        }

        document.getElementById("kpiPeak").textContent   = Number(data.caselli).toLocaleString();
        document.getElementById("kpiLanes").textContent  = Number(data.corsie).toLocaleString();
        document.getElementById("kpiToll").textContent   = Number(data.dispositivi).toLocaleString();
      })
      .catch(err => {
        console.error("Errore fetch KPI assets:", err);
        document.getElementById("kpiPeak").textContent  = "Errore";
        document.getElementById("kpiLanes").textContent = "Errore";
        document.getElementById("kpiToll").textContent  = "Errore";
      });

    // KPI multe giornaliere
    fetch('/api/fines')
      .then(res => {
        if (!res.ok) {
          throw new Error('Errore HTTP: ' + res.status);
        }
        return res.json();
      })
      .then(data => {
        if (data.error) {
          console.error("Errore API multe:", data.error);
          document.getElementById("kpiFines").textContent = "N/D";
          return;
        }
        const finesCount = Number((data && data.fines) || 0);
        document.getElementById("kpiFines").textContent = Number.isFinite(finesCount) ? finesCount.toLocaleString() : "N/D";
      })
      .catch(err => {
        console.error("Errore fetch KPI multe:", err);
        document.getElementById("kpiFines").textContent = "Errore";
      });

    // KPI pagamenti da incassare
    fetch('/api/payments')
      .then(res => {
        if (!res.ok) {
          throw new Error('Errore HTTP: ' + res.status);
        }
        return res.json();
      })
      .then(data => {
        if (data.error) {
          console.error("Errore API pagamenti:", data.error);
          document.getElementById("kpiPayments").textContent = "N/D";
          return;
        }
        const pending = Number((data && data.pending) || 0);
        document.getElementById("kpiPayments").textContent = Number.isFinite(pending) ? pending.toLocaleString() : "N/D";
      })
      .catch(err => {
        console.error("Errore fetch KPI pagamenti:", err);
        document.getElementById("kpiPayments").textContent = "Errore";
      });

    // Trend traffico (line chart)
    fetch('/api/traffic/trend')
      .then(res => {
        if (!res.ok) throw new Error('Errore HTTP: ' + res.status);
        return res.json();
      })
      .then(data => {
        if (data.error) {
          console.error("Errore API trend traffico:", data.error);
          return;
        }

        const labels = data.map(p => p.day);
        const values = data.map(p => p.count);

        const ctx = document.getElementById('trafficChart').getContext('2d');
        new Chart(ctx, {
          type: 'line',
          data: {
            labels,
            datasets: [{
              label: 'Veicoli / giorno',
              data: values,
              borderColor: 'rgba(54, 162, 235, 1)',
              backgroundColor: 'rgba(54, 162, 235, 0.1)',
              tension: 0.2,
              pointRadius: 2
            }]
          },
          options: {
            responsive: true,
            scales: {
              y: { beginAtZero: true }
            }
          }
        });
      })
      .catch(err => console.error("Errore fetch trend traffico:", err));

    // Picchi orari (bar chart)
    fetch('/api/traffic/peaks')
      .then(res => {
        if (!res.ok) throw new Error('Errore HTTP: ' + res.status);
        return res.json();
      })
      .then(data => {
        if (data.error) {
          console.error("Errore API picchi orari:", data.error);
          return;
        }

        const hours = Array.from({ length: 24 }, (_, h) => h);
        const counts = Array(24).fill(0);

        data.forEach(p => {
          const h = Number(p.hour);
          if (h >= 0 && h < 24) {
            counts[h] = p.count;
          }
        });

        const hourLabels = hours.map(h => h.toString().padStart(2, '0') + ':00');

        const ctx = document.getElementById('peaksChart').getContext('2d');
        new Chart(ctx, {
          type: 'bar',
          data: {
            labels: hourLabels,
            datasets: [{
              label: 'Veicoli / ora (oggi)',
              data: counts,
              backgroundColor: 'rgba(255, 159, 64, 0.6)'
            }]
          },
          options: {
            responsive: true,
            scales: {
              y: { beginAtZero: true }
            }
          }
        });
      })
      .catch(err => console.error("Errore fetch picchi orari:", err));

      // Fetch regione–autostrada–casello e mostra in tabella (esempio)
// Tabella Autostrada: Regione – Autostrada – Casello
fetch('/api/tolls')
  .then(res => {
    if (!res.ok) throw new Error('Errore HTTP: ' + res.status);
    return res.json();
  })
  .then(data => {
    if (data.error) {
      console.error("Errore API /api/tolls:", data.error);
      return;
    }

    const tbody = document.getElementById('highwaysTable');
    if (!tbody) return;

tbody.innerHTML = '';
data.forEach(row => {
  const tr = document.createElement('tr');
  tr.innerHTML = `
    <td>${row.nome}</td>
    <td>
      <a href="/autostrada.html" class="text-decoration-none">
        ${row.nome_autostrada}
      </a>
    </td>
  `;
  tbody.appendChild(tr);
});

  })
  .catch(err => console.error("Errore fetch /api/tolls:", err));


// Tabella Multe: ultimi 7 giorni
fetch('/api/fines/list')
  .then(async res => {
    if (!res.ok) throw new Error('Errore HTTP: ' + res.status);

    const raw = await res.text();
    console.log('RAW /api/fines/list:', raw);  // <-- guarda questo in console

    // prova asdrubale fare il parse solo se sembra JSON
    try {
      return JSON.parse(raw);
    } catch (e) {
      console.error('JSON non valido da /api/fines/list:', e);
      throw e;
    }
  })
  .then(data => {
    if (data.error) {
      console.error("Errore API /api/fines/list:", data.error);
      return;
    }

    const tbody = document.getElementById('finesTable');
    if (!tbody) return;

    tbody.innerHTML = '';

data.forEach((row, index) => {
  if (index >= 5) return;  // esce dopo 5 righe

  const tr = document.createElement('tr');

  const statoLabel = row.pagato ? 'Pagata' : 'Da pagare';
  const statoClass = row.pagato ? 'text-success' : 'text-danger';

  tr.innerHTML = `
    <td>${row.id_multa}</td>
    <td>${row.targa}</td>
    <td>${row.data}</td>
    <td>€ ${Number(row.importo).toFixed(2)}</td>
    <td class="${statoClass}">${statoLabel}</td>
  `;
  tbody.appendChild(tr);
});

  })
  .catch(err => console.error("Errore fetch /api/fines/list:", err));


// Ricerca veicoli per targa
const searchBtn = document.getElementById('searchPlateBtn');
if (searchBtn) {
  searchBtn.addEventListener('click', function () {
    const input = document.getElementById('searchPlate');
    const info  = document.getElementById('searchPlateInfo');
    const tbody = document.getElementById('vehicleSearchTable');

    if (!input || !tbody) return;

    const plate = (input.value || '').trim().toUpperCase();
    if (!plate) {
      if (info) info.textContent = 'Inserisci una targa.';
      return;
    }

    if (info) info.textContent = 'Ricerca in corso...';
    tbody.innerHTML = '';

    fetch('/api/vehicles?plate=' + encodeURIComponent(plate))
      .then(res => {
        if (!res.ok) throw new Error('HTTP ' + res.status);
        return res.json();
      })
      .then(data => {
        if (Array.isArray(data) && data.length > 0) {
          data.forEach(r => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
              <td>${r.timestampIn}</td>
              <td>${r.caselloIn}</td>
              <td>${r.timestampOut}</td>
              <td>${r.caselloOut}</td>
              <td>€ ${Number(r.importo).toFixed(2)}</td>
            `;
            tbody.appendChild(tr);
          });
          if (info) info.textContent = `Trovati ${data.length} passaggi per ${plate}.`;
        } else {
          if (info) info.textContent = `Nessun passaggio trovato per ${plate}.`;
        }
      })
      .catch(err => {
        console.error('Errore ricerca veicoli:', err);
        if (info) info.textContent = 'Errore durante la ricerca.';
      });
  });
}
