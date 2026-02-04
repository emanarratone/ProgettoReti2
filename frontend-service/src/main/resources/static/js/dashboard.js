
let trafficChartInstance = null;
let dailyChartInstance = null;
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
    function safeChart(canvasId, config) {
        const canvas = document.getElementById(canvasId);
        if (!canvas || !canvas.getContext) {
            console.warn(`❌ Canvas "${canvasId}" non trovato`);
            return null;
        }

        // Distruggi l'istanza esistente se presente
        if (canvasId === 'trafficChart' && trafficChartInstance) {
            trafficChartInstance.destroy();
        } else if (canvasId === 'dailyChart' && dailyChartInstance) {
            dailyChartInstance.destroy();
        }

        const ctx = canvas.getContext('2d');
        const newChart = new Chart(ctx, config);

        // Salva la nuova istanza
        if (canvasId === 'trafficChart') trafficChartInstance = newChart;
        if (canvasId === 'dailyChart') dailyChartInstance = newChart;

        console.log(`✅ Grafico "${canvasId}" inizializzato`);
        return newChart;
    }
    // Trend traffico 24h (line chart)
  fetch('/api/traffic/24hours')
      .then(res => res.json())
      .then(data => {
          const labels = data.map(row => {
              const ora = Array.isArray(row) ? row[0] : (row.ora || 0);
              return `${String(ora).padStart(2, '0')}:00`;
          });
          const values = data.map(row => Array.isArray(row) ? row[1] : (row.count || row.conteggio || 0));

          safeChart('trafficChart', {
              type: 'line',
              data: {
                  labels: labels,
                  datasets: [{
                      label: 'Veicoli/Ora',
                      data: values,
                      borderColor: '#3498db',
                      backgroundColor: 'rgba(52, 152, 219, 0.1)',
                      fill: true,
                      tension: 0.4
                  }]
              },
              options: {
                  responsive: true,
                  maintainAspectRatio: false,
                  scales: { y: { beginAtZero: true } }
              }
          });
      });


    // Trend 30gg (line chart)
    fetch('/api/traffic/30days')
        .then(res => res.json())
        .then(data => {
            // Invertiamo i dati per mostrare l'ordine cronologico (dal più vecchio al più nuovo)
            const sortedData = [...data].reverse();

            const labels = sortedData.map(row => {
                const d = Array.isArray(row) ? row[0] : (row.data || row[0]);
                return d ? d.toString().slice(5, 10) : '??'; // Formato MM-DD
            });
            const values = sortedData.map(row => Array.isArray(row) ? row[1] : (row.biglietti_giornalieri || 0));

            safeChart('dailyChart', {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Biglietti/Giorno',
                        data: values,
                        borderColor: '#2ecc71',
                        backgroundColor: 'rgba(46, 204, 113, 0.1)',
                        fill: true,
                        tension: 0.3
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: { y: { beginAtZero: true } }
                }
            });
        });
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

