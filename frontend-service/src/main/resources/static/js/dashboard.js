
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
      window.location.href = '/index';
    })
    .catch(err => {
      console.error('Errore nel logout:', err);
      // fallback comunque alla login
      window.location.href = '/index';
    });
});


    // Controlla se utente è admin e mostra/nasconde bottoni
    fetch('/api/session')
      .then(res => res.json())
      .then(data => {
        if (!data.loggedIn) {
          window.location.href = "/index";
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
          document.getElementById('roleBadge').style.display = '';
          document.getElementById('roleBadge').textContent = 'Impiegato';
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
        console.log(data)
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

// Joint tra regione e autostrada
Promise.all([
  fetch('/api/highways').then(res => res.json()),
  fetch('/api/regions').then(res => res.json())
]).then(([highways, regions]) => {

console.log(highways)
console.log(regions)

  // Crea un dizionario delle regioni per accesso rapido { id: nome }
  const regionMap = {};
  regions.forEach(r => regionMap[r.id] = r.nome);

  const tbody = document.getElementById('highwaysTable');
  tbody.innerHTML = '';

  highways.forEach(row => {
    const nomeRegione = regionMap[row.idRegione] || 'Sconosciuta';
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${nomeRegione}</td>
      <td>
        <span>${row.sigla}</span>
      </td>

    `;
    tbody.appendChild(tr);
  });
}).catch(err => console.error("Errore nel join:", err));
  })
  .catch(err => console.error("Errore fetch /api/tolls:", err));

// Tabella Multe: ultimi 7 giorni
fetch('/api/fines/list')
  .then(async res => {
    if (!res.ok) throw new Error('Errore HTTP: ' + res.status);

    const raw = await res.text();
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
    <td>${row.id}</td>
    <td>${row.targa}</td>
    <td>€ ${Number(row.importo).toFixed(2)}</td>
    <td class="${statoClass}">${statoLabel}</td>
  `;
  tbody.appendChild(tr);
});

  })
  .catch(err => console.error("Errore fetch /api/fines/list:", err));

// Funzione helper per validare e formattare le date
function formatSafeDate(dateVal) {
    if (!dateVal) return null;
    const d = new Date(dateVal);
    return isNaN(d.getTime()) ? null : d.toLocaleString('it-IT');
}

/**
 * Gestione Ricerca Veicoli
 */
/**
 * Sezione Ricerca Veicolo
 */
(function() {
    const searchBtn = document.getElementById('searchPlateBtn');
    const inputPlate = document.getElementById('searchPlate');
    const infoBox = document.getElementById('searchPlateInfo');
    const tableBody = document.getElementById('vehicleSearchTable');

    if (!searchBtn) return;

    searchBtn.addEventListener('click', () => {
        const plate = inputPlate.value.trim().toUpperCase();
        if (!plate) return;

        infoBox.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Ricerca...';
        tableBody.innerHTML = '';

        fetch(`/api/vehicles/history?plate=${plate}`)
            .then(res => res.json())
            .then(data => {
                console.log("[DEBUG] Dati ricevuti:", data); // Controlla qui l'importo
                infoBox.innerHTML = `Trovati <strong>${data.length}</strong> record per ${plate}`;

                data.forEach((row) => {
                     const tr = document.createElement('tr');

                     // Formattazione
                     const dIn = row.dataIngresso ? new Date(row.dataIngresso).toLocaleString('it-IT') : '-';
                     const dOut = row.dataUscita ? new Date(row.dataUscita).toLocaleString('it-IT') : '---';

                     // Gestione importo (se null metti 0)
                     const imp = row.importo ? parseFloat(row.importo).toFixed(2) : "0.00";

                     const badgeClass = row.stato === 'PAGATO' ? 'bg-success' : 'bg-warning text-dark';

                     tr.innerHTML = `
                         <td>${dIn}</td>
                         <td><strong class="text-primary">${row.siglaIngresso}</strong></td>
                         <td>${dOut}</td>
                         <td><strong class="text-danger">${row.siglaUscita}</strong></td>
                         <td>
                             <span class="badge ${badgeClass}">€ ${imp}</span>
                             <small class="d-block text-muted">${row.stato}</small>
                         </td>
                     `;
                     tableBody.appendChild(tr);
                 });
            })
            .catch(err => {
                console.error("Errore fetch:", err);
                infoBox.innerHTML = '<span class="text-danger">Errore nel caricamento dei dati</span>';
            });
    });
})();