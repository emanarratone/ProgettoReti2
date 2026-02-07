const groupAutostrada     = document.getElementById('groupAutostrada');
const highwayRegionInput  = document.getElementById('highwayRegionInput');
const regionSuggestionsEl = document.getElementById('regionSuggestions');
const highwayRegionIdsEl  = document.getElementById('highwayRegionIds');
const selectedRegionsEl   = document.getElementById('selectedRegions');

const itemsList   = document.getElementById('itemsList');
const statusEl    = document.getElementById('status');
const levelTitle  = document.getElementById('levelTitle');
const pathSummary = document.getElementById('pathSummary');
const levelList   = document.getElementById('levelList');

const btnAdd    = document.getElementById('btnAdd');

const crudModalEl    = document.getElementById('crudModal');
const crudModal      = new bootstrap.Modal(crudModalEl);
const crudForm       = document.getElementById('crudForm');
const crudModalTitle = document.getElementById('crudModalTitle');
const fieldName      = document.getElementById('fieldName');
const fieldNameLabel = document.getElementById('fieldNameLabel');

// Gruppi specifici
const groupCasello     = document.getElementById('groupCasello');
const groupCorsia      = document.getElementById('groupCorsia');
const groupDispositivo = document.getElementById('groupDispositivo');

let currentLevel  = 'regions';   // regions | highways | tolls | lanes | devices
let selectedItem  = null;        // { id, ... }
let currentAction = null;        // create | edit
const cachedRegions = {};      // cache per i nomi delle regioni (usato per i tag delle autostrade)

let userIsAdmin = false; // Variabile globale



document.addEventListener('DOMContentLoaded', function () {

 fetch('/api/session')
    .then(res => res.json())
    .then(data => {
       userIsAdmin = data.isAdmin; // Salviamo lo stato
       const adminTools = document.querySelectorAll('.admin-tools');

       if (userIsAdmin) {
         adminTools.forEach(el => el.style.display = '');
         document.body.classList.add('is-admin');
       } else {
         adminTools.forEach(el => el.style.display = 'none');
         document.body.classList.remove('is-admin');
         document.getElementById('roleBadge').textContent = 'Impiegato';
       }
       loadRegions();
       updatePathSummary();
    });
  // Logout coerente con dashboard
  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', function () {
      fetch('/api/logout', {
        method: 'POST',
        credentials: 'same-origin'
      })
        .then(res => {
          if (!res.ok) {
            throw new Error('HTTP ' + res.status);
          }
          return res.json().catch(() => ({}));
        })
        .then(() => {
          window.location.href = '/index';
        })
        .catch(err => {
          console.error('Errore nel logout:', err);
          window.location.href = '/index';
        });
    });
  }

  // Stato corrente
  const state = {
    region: null,
    highway: null,
    toll: null,
    lane: null
  };

function getActionButtonsHtml() {
  if (!userIsAdmin) return ''; // Se impiegato, torna stringa vuota
  return `
    <div class="btn-group btn-group-sm">
      <button type="button" class="btn btn-outline-primary btn-edit-row">
        <i class="bi bi-pencil"></i>
      </button>
      <button type="button" class="btn btn-outline-danger btn-delete-row">
        <i class="bi bi-x"></i>
      </button>
    </div>`;
}
  function setStatus(msg) {
    statusEl.textContent = msg || '';
  }

 function setActiveLevel(level) {
    const order = ['regions', 'highways', 'tolls', 'lanes', 'devices'];
    const currentIndex = order.indexOf(level);

    document.querySelectorAll('#levelList .list-group-item').forEach(li => {
      const liLevel = li.dataset.level;
      const liIndex = order.indexOf(liLevel);
      li.classList.toggle('active-level', liLevel === level);
      if (liIndex !== -1 && liIndex < currentIndex) li.classList.add('visited-level');
      else li.classList.remove('visited-level');
    });

    currentLevel = level;
    updateAddButtonLabel();
    selectedItem = null;
  }

// Reset livelli sottostanti
function resetBelow(level) {
  if (level === 'region') {
    state.highway = null;
    state.toll = null;
    state.lane = null;
  } else if (level === 'highway') {
    state.toll = null;
    state.lane = null;
  } else if (level === 'toll') {
    state.lane = null;
  }
  // Ogni volta che resettiamo i dati sottostanti, aggiorniamo il breadcrumb
  updatePathSummary();
}

  // PATH SUMMARY: breadcrumb cliccabile
 function updatePathSummary() {
   pathSummary.innerHTML = '';

   // Se non c'è una regione, siamo al livello radice
   if (!state.region) {
     pathSummary.textContent = 'Seleziona una regione per iniziare.';
     return;
   }

   const separator = () => {
     const span = document.createElement('span');
     span.textContent = ' > ';
     span.className = 'mx-2 text-muted';
     return span;
   };

   // --- 1. REGIONE ---
   const aRegion = document.createElement('a');
   aRegion.href = '#';
   aRegion.className = 'text-decoration-none';
   aRegion.textContent = state.region.name;
   aRegion.onclick = (e) => {
     e.preventDefault();
     resetBelow('region');
     state.region = null;
     loadRegions();
     updatePathSummary();
   };
   pathSummary.appendChild(aRegion);

   // --- 2. AUTOSTRADA ---
   if (state.highway) {
     pathSummary.appendChild(separator());
     const aHighway = document.createElement('a');
     aHighway.href = '#';
     aHighway.className = 'text-decoration-none';
     aHighway.textContent = state.highway.name;
     aHighway.onclick = (e) => {
       e.preventDefault();
       resetBelow('highway');
       loadHighwaysForRegion(state.region.id);
       updatePathSummary();
     };
     pathSummary.appendChild(aHighway);
   }

   // --- 3. CASELLO ---
   if (state.toll) {
     pathSummary.appendChild(separator());
     const aToll = document.createElement('a');
     aToll.href = '#';
     aToll.className = 'text-decoration-none';
     aToll.textContent = state.toll.name;
     aToll.onclick = (e) => {
       e.preventDefault();
       resetBelow('toll');
       loadTollsForHighway(state.highway.id);
       updatePathSummary();
     };
     pathSummary.appendChild(aToll);
   }

   // --- 4. CORSIA ---
   if (state.lane) {
     pathSummary.appendChild(separator());
     const aLane = document.createElement('a');
     aLane.href = '#';
     aLane.className = 'text-decoration-none';
     aLane.textContent = state.lane.name;
     aLane.onclick = (e) => {
       e.preventDefault();
       loadLanesForToll(state.toll.id);
       updatePathSummary();
     };
     pathSummary.appendChild(aLane);
   }

   // --- 5. LIVELLO DISPOSITIVI
   if (currentLevel === 'devices') {
     pathSummary.appendChild(separator());
     const spanDev = document.createElement('span');
     spanDev.className = 'text-dark fw-bold';
     spanDev.textContent = 'Dispositivi';
     pathSummary.appendChild(spanDev);
   }
 }

  // Gestione click barra livelli
  levelList.addEventListener('click', function (e) {
      const li = e.target.closest('.list-group-item');
      if (!li) return;
      const level = li.dataset.level;

      if (level === 'regions') {
        state.region = null; state.highway = null; state.toll = null; state.lane = null;
        loadRegions();
      } else if (level === 'highways' && state.region) {
        loadHighwaysForRegion(state.region.id);
      } else if (level === 'tolls' && state.highway) {
        loadTollsForHighway(state.highway.id);
      } else if (level === 'lanes' && state.toll) {
        loadLanesForToll(state.toll.id);
      } else if (level === 'devices' && state.lane) {
        loadDevicesForLane(state.lane.num_corsia, state.lane.id_casello);
      }
      updatePathSummary();
    });

  // helper: rende selezionabile una li per CRUD
  function makeSelectableLi(li, obj) {
    li.addEventListener('click', () => {
      document
        .querySelectorAll('#itemsList .list-group-item')
        .forEach(x => x.classList.remove('active'));
      li.classList.add('active');
      selectedItem = obj;
    });
  }

  // REGIONI
function loadRegions() {
  setActiveLevel('regions');
  levelTitle.textContent = 'REGIONI';
  setStatus('Caricamento regioni...');
  itemsList.innerHTML = '';

  fetch('/api/regions')
    .then(res => {
      if (!res.ok) throw new Error('HTTP ' + res.status);
      return res.json();
    })
    .then(data => {
      if (!Array.isArray(data) || data.length === 0) {
        setStatus('Nessuna regione trovata.');
        return;
      }
      setStatus('Seleziona una regione per vedere le autostrade.');

      data.forEach(r => {
        const li = document.createElement('li');
        li.className = 'list-group-item d-flex justify-content-between align-items-center';

        // Estrazione dati (gestisce diversi formati di risposta API)
        const name = r.nome || r.nomeRegione || r.name;
        const id = r.id_regione || r.id;
        const obj = { id, name };

        li.innerHTML = `
          <div>
            <strong>${name}</strong>
          </div>
          ${getActionButtonsHtml()}
        `;

        // Rende la riga selezionabile
        makeSelectableLi(li, obj);

        // --- GESTIONE NAVIGAZIONE E BREADCRUMB ---
        li.addEventListener('click', () => {
          // 1. Aggiorniamo lo stato globale con la regione selezionata
          state.region = { id: obj.id, name: name };

          // 2. Resettiamo i livelli inferiori (highway, toll, lane)
         resetBelow('region');

          // 3. Carichiamo il livello successivo
          loadHighwaysForRegion(state.region.id);
        });

        // --- GESTIONE EVENTI ADMIN ---
        if (userIsAdmin) {
          const editBtn = li.querySelector('.btn-edit-row');
          const deleteBtn = li.querySelector('.btn-delete-row');

          if (editBtn) {
            editBtn.addEventListener('click', (ev) => {
              ev.stopPropagation(); // Evita di attivare la navigazione cliccando l'icona
              selectedItem = obj;
              currentAction = 'edit';
              crudModalTitle.textContent = getModalTitle();
              configureModalFields();
              crudModal.show();
            });
          }

          if (deleteBtn) {
            deleteBtn.addEventListener('click', (ev) => {
              ev.stopPropagation(); // Evita di attivare la navigazione cliccando l'icona
              selectedItem = obj;
              if (confirm('Sei sicuro di voler eliminare questa regione?')) {
                doDelete();
              }
            });
          }
        }

        itemsList.appendChild(li);
      });
    })
    .catch(err => {
      console.error('Errore caricamento regioni:', err);
      setStatus('Errore nel caricamento delle regioni.');
    });
}

  // AUTOSTRADE per regione
  function loadHighwaysForRegion(regionId) {
    if (!regionId) return;
    setActiveLevel('highways');

    const regionName = state.region ? state.region.name : '';
    levelTitle.textContent = 'AUTOSTRADE DI ' + regionName;

    setStatus('Caricamento autostrade...');
    itemsList.innerHTML = '';

    fetch('/api/regions/' + encodeURIComponent(regionId) + '/highways')
      .then(res => {
        if (!res.ok) throw new Error('HTTP ' + res.status);
        return res.json();
      })
      .then(data => {
        if (!Array.isArray(data) || data.length === 0) {
          setStatus('Nessuna autostrada trovata per questa regione.');
          return;
        }
        setStatus('Seleziona un\'autostrada per vedere i caselli.');

        data.forEach(h => {
          const li = document.createElement('li');
          li.className = 'list-group-item d-flex justify-content-between align-items-center';

          const name = h.sigla || h.citta || h.name || ('Autostrada ' + h.id);
          const obj  = { id: h.id_autostrada || h.id, name };

          li.innerHTML = `
            <div>
              <strong>${name}</strong>
            </div>
            ${getActionButtonsHtml()}
          `;

          makeSelectableLi(li, obj);

          // La navigazione rimane disponibile per tutti
            li.addEventListener('click', () => {
              state.highway = { id: obj.id, name: name };
              resetBelow('highway'); // Resetta i livelli successivi e aggiorna il path
              loadTollsForHighway(state.highway.id);
            });

          // Applichiamo i listener solo se i bottoni sono stati iniettati (Admin)
          if (userIsAdmin) {
            const btnEdit = li.querySelector('.btn-edit-row');
            const btnDelete = li.querySelector('.btn-delete-row');

            if (btnEdit) {
              btnEdit.addEventListener('click', (ev) => {
                ev.stopPropagation();
                selectedItem = obj;
                currentAction = 'edit';
                crudModalTitle.textContent = getModalTitle();
                configureModalFields();
                crudModal.show();
              });
            }

            if (btnDelete) {
              btnDelete.addEventListener('click', (ev) => {
                ev.stopPropagation();
                selectedItem = obj;
                if (confirm('Sei sicuro di voler eliminare questo elemento?')) {
                  doDelete();
                }
              });
            }
          }

          itemsList.appendChild(li);
        });
      })
      .catch(err => {
        console.error('Errore caricamento autostrade:', err);
        setStatus('Errore nel caricamento delle autostrade.');
      });
  }

  // CASELLI per autostrada
  function loadTollsForHighway(highwayId) {
    if (!highwayId) return;
    setActiveLevel('tolls');
    levelTitle.textContent = 'CASELLI DI ' + state.highway.name;
    setStatus('Caricamento caselli...');
    itemsList.innerHTML = '';

    fetch('/api/highways/' + encodeURIComponent(highwayId) + '/tolls')
      .then(res => {
        if (!res.ok) throw new Error('HTTP ' + res.status);
        return res.json();
      })
      .then(data => {
        if (!Array.isArray(data) || data.length === 0) {
          setStatus('Nessun casello trovato per questa autostrada.');
          return;
        }
        setStatus('Seleziona un casello per vedere le corsie.');

        data.forEach(t => {
          const li = document.createElement('li');
          li.className = 'list-group-item d-flex justify-content-between align-items-center';

          const name = t.sigla || t.nome_casello || t.nome || t.name || ('Casello ' + (t.id_casello || t.idCasello || t.id));
          const idVal = t.id_casello || t.idCasello || t.id;
          const obj  = {
            id: idVal,
            idCasello: idVal,
            name,
            limite: t.limite || null,
            chiuso: t.chiuso || t.closed || false
          };

          li.innerHTML = `
            <div>
              <strong>${name}</strong>
              <div class="small-muted">
                Limite: ${obj.limite || 130} km/h${obj.chiuso ? ' (chiuso)' : ''}
              </div>
            </div>
            ${getActionButtonsHtml()}
          `;

          makeSelectableLi(li, obj);

          // Navigazione disponibile per tutti
          li.addEventListener('click', () => {
              state.toll = { id: obj.id, name: name };
              resetBelow('toll'); // Aggiorna il pathSummary
              loadLanesForToll(state.toll.id)
          });

          // Eventi Admin
          if (userIsAdmin) {
            const btnEdit = li.querySelector('.btn-edit-row');
            const btnDelete = li.querySelector('.btn-delete-row');

            if (btnEdit) {
              btnEdit.addEventListener('click', ev => {
                ev.stopPropagation();
                selectedItem = obj;
                currentAction = 'edit';
                crudModalTitle.textContent = getModalTitle();
                configureModalFields();
                crudModal.show();
              });
            }

            if (btnDelete) {
              btnDelete.addEventListener('click', ev => {
                ev.stopPropagation();
                selectedItem = obj;
                if (confirm('Sei sicuro di voler eliminare questo elemento?')) {
                  doDelete();
                }
              });
            }
          }

          itemsList.appendChild(li);
        });
      })
      .catch(err => {
        console.error('Errore caricamento caselli:', err);
        setStatus('Errore nel caricamento dei caselli.');
      });
  }

function loadLanesForToll(tollId) {
  if (!tollId) return;
  setActiveLevel('lanes');
  levelTitle.textContent = 'CORSIE DI ' + state.toll.name;
  setStatus('Caricamento corsie...');
  itemsList.innerHTML = '';

  fetch('/api/tolls/' + encodeURIComponent(tollId) + '/lanes')
    .then(res => {
      if (!res.ok) throw new Error('HTTP ' + res.status);
      return res.json();
    })
    .then(data => {
      if (!Array.isArray(data) || data.length === 0) {
        setStatus('Nessuna corsia trovata per questo casello.');
        return;
      }
      setStatus('Seleziona una corsia per vedere i dispositivi.');

      data.forEach(l => {
        const numCorsia = l.num_corsia || l.numCorsia;
        const idCasello = l.id_casello || l.casello;
        const verso     = l.verso;
        const tipo      = l.tipo_corsia || l.tipo;
        const chiuso    = l.chiuso || l.closed || l.isClosed;

        const name = 'Corsia ' + numCorsia;
        const obj  = {
          num_corsia: numCorsia,
          id_casello: idCasello,
          name,
          verso,
          tipo,
          chiuso
        };

        const li = document.createElement('li');
        li.className = 'list-group-item d-flex justify-content-between align-items-center';

        li.innerHTML = `
          <div>
            <strong>${name}</strong>
            <div class="small-muted">
              ${verso} — ${tipo}${chiuso ? ' (chiusa)' : ''}
            </div>
          </div>
          ${getActionButtonsHtml()}
        `;

        makeSelectableLi(li, obj);

        // Navigazione (Sola lettura per Impiegato)
        li.addEventListener('click', () => {
              state.lane = { num_corsia: obj.num_corsia, id_casello: obj.id_casello, name: name };
              updatePathSummary(); // Qui chiamiamo direttamente update perché non c'è nulla "sotto" la lane da resettare
              loadDevicesForLane(state.lane.num_corsia, state.lane.id_casello);
        });

        // Gestione CRUD solo per Admin
        if (userIsAdmin) {
          const editBtn = li.querySelector('.btn-edit-row');
          const deleteBtn = li.querySelector('.btn-delete-row');

          if (editBtn) {
            editBtn.addEventListener('click', ev => {
              ev.stopPropagation();
              selectedItem  = obj;
              currentAction = 'edit';
              crudModalTitle.textContent = getModalTitle();
              configureModalFields();
              crudModal.show();
            });
          }

          if (deleteBtn) {
            deleteBtn.addEventListener('click', ev => {
              ev.stopPropagation();
              selectedItem = obj;
              if (confirm('Sei sicuro di voler eliminare questo elemento?')) {
                doDelete();
              }
            });
          }
        }

        itemsList.appendChild(li);
      });
    })
    .catch(err => {
      console.error('Errore caricamento corsie:', err);
      setStatus('Errore nel caricamento delle corsie.');
    });
}

// DISPOSITIVI per corsia
function loadDevicesForLane(numCorsia, idCasello) {
    if (!numCorsia || !idCasello) return;
    setActiveLevel('devices');
    levelTitle.textContent = 'DISPOSITIVI CORSIA ' + numCorsia;
    setStatus('Caricamento dispositivi...');
    itemsList.innerHTML = '';

    // Endpoint come definito nel gateway: /api/lanes/{idCasello}/{numCorsia}/devices
    fetch(`/api/lanes/${idCasello}/${numCorsia}/devices`)
        .then(res => res.json())
        .then(data => {
            if (!Array.isArray(data) || data.length === 0) {
                setStatus('Nessun dispositivo presente.');
                return;
            }
            setStatus('Dispositivi trovati.');

            data.forEach(d => {
                // Normalizziamo i dati per il JS
                console.log(d)
                const obj = {
                    id: d.id,
                    tipo: d.tipoDispositivo || d.tipo,
                    stato: d.status || d.stato,
                    num_corsia: numCorsia, // Assicurati che questi nomi siano coerenti
                    id_casello: idCasello
                };

                const li = document.createElement('li');
                li.className = 'list-group-item d-flex justify-content-between align-items-center';

                let icon = obj.tipo === 'SBARRA' ? 'bi-hr' : (obj.tipo === 'TOTEM' ? 'bi-display' : 'bi-cpu');

                li.innerHTML = `
                    <div class="d-flex align-items-center">
                        <i class="bi ${icon} fs-4 me-3 text-primary"></i>
                        <div>
                            <strong>${obj.tipo}</strong> <small class="text-muted">#${obj.id}</small>
                            <div class="small">Stato: <span class="badge ${obj.stato === 'ATTIVO' ? 'bg-success' : 'bg-danger'}">${obj.stato}</span></div>
                        </div>
                    </div>
                    ${getActionButtonsHtml()}
                `;

                makeSelectableLi(li, obj);

                if (userIsAdmin) {
                    li.querySelector('.btn-edit-row').onclick = (e) => {
                        e.stopPropagation();
                        selectedItem = obj;
                        currentAction = 'edit';
                        crudModalTitle.textContent = "Modifica Dispositivo";
                        configureModalFields();
                        // Pre-popolamento
                        document.getElementById('dispTipo').value = obj.tipo;
                        document.getElementById('dispStato').value = obj.stato;
                        crudModal.show();
                    };
                    li.querySelector('.btn-delete-row').onclick = (e) => {
                        e.stopPropagation();
                        selectedItem = obj;
                        if(confirm("Eliminare dispositivo?")) doDelete();
                    };
                }
                itemsList.appendChild(li);
            });
        })
        .catch(err => setStatus('Errore: ' + err.message));
}


 function renderSelectedRegions() {
    selectedRegionsEl.innerHTML = '';
    const current = highwayRegionIdsEl.value
      ? highwayRegionIdsEl.value.split(',').map(v => Number(v))
      : [];

    current.forEach(id => {
      const tag = document.createElement('span');
      tag.className = 'badge bg-primary d-flex align-items-center gap-1';

      const spanText = document.createElement('span');
      spanText.textContent = cachedRegions[id] || ('Regione ' + id);
      tag.appendChild(spanText);

      const btnX = document.createElement('button');
      btnX.type = 'button';
      btnX.className = 'btn-close btn-close-white btn-sm ms-1';
      btnX.addEventListener('click', () => {
        const updated = current.filter(x => x !== id);
        highwayRegionIdsEl.value = updated.join(',');
        renderSelectedRegions();
      });

      tag.appendChild(btnX);
      selectedRegionsEl.appendChild(tag);
    });
  }

 function addRegionToSelected(id, name) {
    const current = highwayRegionIdsEl.value
      ? highwayRegionIdsEl.value.split(',').map(v => Number(v))
      : [];

    if (!current.includes(id)) {
      current.push(id);
      highwayRegionIdsEl.value = current.join(',');
      cachedRegions[id] = name;
    }

    highwayRegionInput.value = '';
    regionSuggestionsEl.innerHTML = '';
    renderSelectedRegions();
  }

  // --- CRUD: dialog separato ---

  function getModalTitle() {
    if (currentAction === 'create') {
      switch (currentLevel) {
        case 'regions':  return 'Nuova regione';
        case 'highways': return 'Nuova autostrada';
        case 'tolls':    return 'Nuovo casello';
        case 'lanes':    return 'Nuova corsia';
        case 'devices':  return 'Nuovo dispositivo';
      }
    } else {
      switch (currentLevel) {
        case 'regions':  return 'Modifica regione';
        case 'highways': return 'Modifica autostrada';
        case 'tolls':    return 'Modifica casello';
        case 'lanes':    return 'Modifica corsia';
        case 'devices':  return 'Modifica dispositivo';
      }
    }
    return 'Modifica';
  }

  function configureModalFields() {
      // Nascondi tutti i gruppi extra
      [groupCasello, groupCorsia, groupDispositivo, groupAutostrada].forEach(g => g.classList.add('d-none'));

      const nameGroup = fieldName.closest('.mb-3');
      nameGroup.classList.add('d-none');
      fieldName.required = false;

      switch (currentLevel) {
          case 'regions':
              nameGroup.classList.remove('d-none');
              fieldNameLabel.textContent = 'Nome regione';
              fieldName.required = true;
              if (currentAction === 'edit' && selectedItem) fieldName.value = selectedItem.name;
              break;

          case 'highways':
              nameGroup.classList.remove('d-none');
              fieldNameLabel.textContent = 'Sigla Autostrada';
              groupAutostrada.classList.remove('d-none');
              if (currentAction === 'edit' && selectedItem) fieldName.value = selectedItem.name;
              break;

          case 'tolls':
              nameGroup.classList.remove('d-none');
              groupCasello.classList.remove('d-none');
              if (currentAction === 'edit' && selectedItem) fieldName.value = selectedItem.name;
              break;

          case 'lanes':
              groupCorsia.classList.remove('d-none');
              break;


          case 'devices':
              groupDispositivo.classList.remove('d-none');
              const dTipo = document.getElementById('dispTipo');
              const dStato = document.getElementById('dispStato');

              if (currentAction === 'edit' && selectedItem) {
                  dTipo.value = selectedItem.tipo; // Imposta il valore corretto
                  dTipo.disabled = true;           // Poi disabilita per l'utente
                  dStato.value = selectedItem.stato;
              } else {
                  dTipo.disabled = false;
                  dTipo.value = 'SBARRA';          // Default per nuova creazione
                  dStato.value = 'ATTIVO';
              }
              break;
      }
  }


  let regionSearchTimeout = null;

highwayRegionInput.addEventListener('input', () => {
  const query = highwayRegionInput.value.trim();
  regionSuggestionsEl.innerHTML = '';

  if (regionSearchTimeout) clearTimeout(regionSearchTimeout);
  if (query.length < 2) return;

regionSearchTimeout = setTimeout(() => {
      fetch('/api/regions/search?q=' + encodeURIComponent(query))
        .then(res => { if (!res.ok) throw new Error('HTTP ' + res.status); return res.json(); })
        .then(data => {
          regionSuggestionsEl.innerHTML = '';
          if (!Array.isArray(data) || data.length === 0) return;

          data.forEach(r => {
            const li = document.createElement('button');
            li.type = 'button';
            li.className = 'list-group-item list-group-item-action';
            const name = r.nomeRegione || r.nome || r.name;
            const id   = r.id_regione || r.id;
            li.textContent = name;
            li.addEventListener('click', () => addRegionToSelected(id, name));
            regionSuggestionsEl.appendChild(li);
          });
        })
        .catch(err => console.error('Errore ricerca regioni:', err));
    }, 300);
  });

  function pickId(item, ...fields) {
    if (!item) return null;
    for (const f of fields) {
      if (item[f] !== undefined && item[f] !== null) return item[f];
    }
    return null;
  }

  function getEndpointAndBodyForCreate() {
    const name = fieldName.value.trim();

    switch (currentLevel) {
      case 'regions':
        return {
          url: '/api/regions',
          body: { nome: name }
        };

      case 'highways': {
        const selectedIds = highwayRegionIdsEl.value
          ? highwayRegionIdsEl.value.split(',').map(v => Number(v))
          : [];

        if (selectedIds.length === 0 && state.region) {
          selectedIds.push(state.region.id);
        }

        const idRegione = selectedIds.length > 0 ? selectedIds[0] : null;
        return {
          url: '/api/highways',
          body: {
            sigla: fieldName.value.trim(),
            idRegione: idRegione
          }
        };
      }

      case 'tolls':
        return {
          url: `/api/highways/${encodeURIComponent(state.highway.id)}/tolls`,
          body: {
            nome_casello: name,
            limite: Number(document.getElementById('caselloLimite').value),
            chiuso: document.getElementById('caselloChiuso').checked
          }
        };

      case 'lanes':
        return {
          url: `/api/tolls/${encodeURIComponent(state.toll.id)}/lanes`,
          body: {
            verso: document.getElementById('corsiaVerso').value || null,
            tipo_corsia: document.getElementById('corsiaTipo').value || null,
            chiuso: document.getElementById('corsiaChiuso').checked
          }
        };

        case 'devices':
            return {
                url: `/api/lanes/${state.lane.id_casello}/${state.lane.num_corsia}/devices`,
                body: {
                    tipo: document.getElementById('dispTipo').value, // DEVE essere 'tipo'
                    stato: document.getElementById('dispStato').value // DEVE essere 'stato'
                }
            };
    }
  }

function getEndpointForUpdateOrDelete() {
    if (!selectedItem) return null;
    switch (currentLevel) {
        case 'regions':  return `/api/regions/${selectedItem.id}`;
        case 'highways': return `/api/highways/${selectedItem.id}`;
        case 'tolls':    return `/api/tolls/${selectedItem.id}`; // Nota la 's' come nel tuo gateway
        case 'lanes':    return `/api/lanes/${selectedItem.id_casello}/${selectedItem.num_corsia}`;
        case 'devices':  return `/api/devices/${selectedItem.id}`; // Corrisponde a @PutMapping("/devices/{id}")
    }
}

crudModalEl.addEventListener('hidden.bs.modal', () => {
  crudForm.reset();
  highwayRegionIdsEl.value = '';
  selectedRegionsEl.innerHTML = '';
  regionSuggestionsEl.innerHTML = '';
});


  function reloadCurrentLevel() {
    switch (currentLevel) {
      case 'regions':
        loadRegions();
        break;
      case 'highways':
        if (state.region)  loadHighwaysForRegion(state.region.id);
        break;
      case 'tolls':
        if (state.highway) loadTollsForHighway(state.highway.id);
        break;
      case 'lanes':
        if (state.toll)    loadLanesForToll(state.toll.id);
        break;
      case 'devices':
        if (state.lane)    loadDevicesForLane(state.lane.num_corsia, state.lane.id_casello);
        break;
    }
  }

  function doCreate() {
    const cfg = getEndpointAndBodyForCreate();
    if (!cfg) return;

    fetch(cfg.url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cfg.body)
    })
      .then(r => {
        if (!r.ok) throw new Error('HTTP ' + r.status);
        return r.json();
      })
      .then(() => {
        crudModal.hide();
        reloadCurrentLevel();
      })
      .catch(err => {
        console.error('Errore creazione:', err);
        alert('Errore nella creazione.');
      });
  }

function doUpdate() {
    const url = getEndpointForUpdateOrDelete();
    if (!url) return;

    let body = {};
    const name = fieldName.value.trim();

    switch (currentLevel) {
        case 'regions': body = { nome: name }; break;
        case 'highways': body = { sigla: name, idRegione: state.region.id }; break;
        case 'tolls':
            body = {
                nome_casello: name,
                limite: document.getElementById('caselloLimite').value,
                chiuso: document.getElementById('caselloChiuso').checked
            };
            break;
        case 'lanes':
            body = {
                verso: document.getElementById('corsiaVerso').value,
                tipo_corsia: document.getElementById('corsiaTipo').value,
                chiuso: document.getElementById('corsiaChiuso').checked
            };
            break;
        case 'devices':
            body = {
                stato: document.getElementById('dispStato').value,
                casello: selectedItem.id_casello,
                corsia: selectedItem.num_corsia
            };
            break;
    }

    console.log("Sending UPDATE to:", url, "Body:", body);

    fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    })
    .then(res => {
        if (!res.ok) throw new Error('Errore HTTP ' + res.status);
        return res.json();
    })
    .then(() => {
        crudModal.hide();
        reloadCurrentLevel();
    })
    .catch(err => {
        console.error('Errore update:', err);
        alert('Errore durante l\'aggiornamento.');
    });
}

function doDelete() {
    const url = getEndpointForUpdateOrDelete();
    if (!url) return;

    if (confirm("Attenzione: l'eliminazione di questo elemento comporterà la rimozione a cascata di tutti i dati collegati (Autostrade, Caselli, ecc.). Continuare?")) {

        fetch(url, { method: 'DELETE' })
            .then(res => {
                if (res.ok) {
                    // Feedback immediato all'utente
                    alert("Eliminazione avviata. Il sistema sta pulendo i dati in background.");
                    reloadCurrentLevel(); // Ricarica la lista (la regione sparirà subito)
                } else {
                    alert("Errore durante l'eliminazione.");
                }
            })
            .catch(err => console.error("Errore:", err));
    }
}

  function updateAddButtonLabel() {
    switch (currentLevel) {
      case 'regions':
        btnAdd.textContent = 'Aggiungi regione';
        break;
      case 'highways':
        btnAdd.textContent = 'Nuova autostrada';
        break;
      case 'tolls':
        btnAdd.textContent = 'Nuovo casello';
        break;
      case 'lanes':
        btnAdd.textContent = 'Nuova corsia';
        break;
      case 'devices':
        btnAdd.textContent = 'Nuovo dispositivo';
        break;
      default:
        btnAdd.textContent = 'Nuovo';
    }
  }

  // Apertura modal in create
  btnAdd.addEventListener('click', () => {
    currentAction = 'create';
    crudModalTitle.textContent = getModalTitle();
    configureModalFields();
    crudModal.show();
  });

  // Submit form
  crudForm.addEventListener('submit', function (e) {
    e.preventDefault();

    const needName = (currentLevel === 'regions' ||
                      currentLevel === 'highways' ||
                      currentLevel === 'tolls');

    if (needName && !fieldName.value.trim()) return;

    if (currentAction === 'create') {
      doCreate();
    } else if (currentAction === 'edit') {
      doUpdate();
    }
  });

  // Avvio
  loadRegions();
});