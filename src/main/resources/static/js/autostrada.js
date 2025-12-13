   const groupAutostrada     = document.getElementById('groupAutostrada');
    const highwayRegionInput  = document.getElementById('highwayRegionInput');
    const regionSuggestionsEl = document.getElementById('regionSuggestions');
    const highwayRegionIdsEl  = document.getElementById('highwayRegionIds');
    const selectedRegionsEl   = document.getElementById('selectedRegions');

 // Debug: verifica che tutti gli elementi esistano
  console.log('Elementi trovati:', {
    groupAutostrada: !!groupAutostrada,
    highwayRegionInput: !!highwayRegionInput,
    regionSuggestionsEl: !!regionSuggestionsEl,
    highwayRegionIdsEl: !!highwayRegionIdsEl,
    selectedRegionsEl: !!selectedRegionsEl
  });
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

document.addEventListener('DOMContentLoaded', function () {
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
          window.location.href = '/index.html';
        })
        .catch(err => {
          console.error('Errore nel logout:', err);
          window.location.href = '/index.html';
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


  function setStatus(msg) {
    statusEl.textContent = msg || '';
  }

  // Attiva livello nella barra orizzontale
  function setActiveLevel(level) {
    const order = ['regions', 'highways', 'tolls', 'lanes', 'devices'];
    const currentIndex = order.indexOf(level);

    document.querySelectorAll('#levelList .list-group-item').forEach(li => {
      const liLevel = li.dataset.level;
      const liIndex = order.indexOf(liLevel);
      li.classList.toggle('active-level', liLevel === level);
      if (liIndex !== -1 && liIndex < currentIndex) {
        li.classList.add('visited-level');
      } else {
        li.classList.remove('visited-level');
      }
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
  }

  // PATH SUMMARY: breadcrumb cliccabile
  function updatePathSummary() {
    pathSummary.innerHTML = '';

    if (!state.region) {
      pathSummary.textContent = 'Seleziona una regione per iniziare.';
      return;
    }

    const separator = document.createTextNode(' > ');

    // Regione
    if (state.region) {
      const aRegion = document.createElement('asdrubale');
      aRegion.textContent = state.region.name;
      aRegion.addEventListener('click', () => {
        state.region = null;
        state.highway = null;
        state.toll = null;
        state.lane = null;
        loadRegions();
        updatePathSummary();
      });
      pathSummary.appendChild(aRegion);
    }

    // Autostrada
    if (state.highway) {
      pathSummary.appendChild(separator.cloneNode());
      const aHighway = document.createElement('asdrubale');
      aHighway.textContent = state.highway.name;
      aHighway.addEventListener('click', () => {
        if (!state.region) return;
        resetBelow('region');
        loadHighwaysForRegion(state.region.id);
        updatePathSummary();
      });
      pathSummary.appendChild(aHighway);
    }

    // Casello
    if (state.toll) {
      pathSummary.appendChild(separator.cloneNode());
      const aToll = document.createElement('asdrubale');
      aToll.textContent = state.toll.name;
      aToll.addEventListener('click', () => {
        resetBelow('highway');
        loadTollsForHighway(state.highway.id);
        updatePathSummary();
      });
      pathSummary.appendChild(aToll);
    }

    // Corsia
    if (state.lane) {
      pathSummary.appendChild(separator.cloneNode());
      const aLane = document.createElement('asdrubale');
      aLane.textContent = state.lane.name;
      aLane.addEventListener('click', () => {
        resetBelow('toll');
        loadLanesForToll(state.toll.id);
        updatePathSummary();
      });
      pathSummary.appendChild(aLane);
    }

    // Livello finale "Dispositivi"
    const currentLevelLi = document.querySelector('#levelList .list-group-item.active-level');
    if (currentLevelLi && currentLevelLi.dataset.level === 'devices') {
      pathSummary.appendChild(separator.cloneNode());
      const spanDevices = document.createElement('span');
      spanDevices.textContent = 'Dispositivi';
      pathSummary.appendChild(spanDevices);
    }
  }

  // Gestione click barra livelli
  levelList.addEventListener('click', function (e) {
    const li = e.target.closest('.list-group-item');
    if (!li) return;
    const level = li.dataset.level;

    if (level === 'regions') {
      state.region = null;
      state.highway = null;
      state.toll = null;
      state.lane = null;
      loadRegions();
    } else if (level === 'highways' && state.region) {
      resetBelow('region');
      loadHighwaysForRegion(state.region.id);
    } else if (level === 'tolls' && state.highway) {
      resetBelow('highway');
      loadTollsForHighway(state.highway.id);
    } else if (level === 'lanes' && state.toll) {
      resetBelow('toll');
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

          const name = r.nome || r.nomeRegione || r.name;
          const obj  = { id: r.id_regione || r.id, name };

          li.innerHTML = `
            <div>
              <strong>${name}</strong>
            </div>
            <div class="btn-group btn-group-sm">
              <button type="button" class="btn btn-outline-primary btn-edit-row">
                <i class="bi bi-pencil"></i>
              </button>
              <button type="button" class="btn btn-outline-danger btn-delete-row">
                <i class="bi bi-x"></i>
              </button>
            </div>
          `;

          makeSelectableLi(li, obj);

          // navigazione
          li.addEventListener('click', () => {
            state.region = { id: obj.id, name };
            state.highway = null;
            state.toll = null;
            state.lane = null;
            updatePathSummary();
            loadHighwaysForRegion(state.region.id);
          });

          // matita
          li.querySelector('.btn-edit-row').addEventListener('click', (ev) => {
            ev.stopPropagation();
            selectedItem = obj;
            currentAction = 'edit';
            crudModalTitle.textContent = getModalTitle();
            configureModalFields();
            crudModal.show();
          });

          // X
          li.querySelector('.btn-delete-row').addEventListener('click', (ev) => {
            ev.stopPropagation();
            selectedItem = obj;
            if (confirm('Sei sicuro di voler eliminare questo elemento?')) {
              doDelete();
            }
          });

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
            <div class="btn-group btn-group-sm">
              <button type="button" class="btn btn-outline-primary btn-edit-row">
                <i class="bi bi-pencil"></i>
              </button>
              <button type="button" class="btn btn-outline-danger btn-delete-row">
                <i class="bi bi-x"></i>
              </button>
            </div>
          `;

          makeSelectableLi(li, obj);

          // navigazione
          li.addEventListener('click', () => {
            state.highway = { id: obj.id, name };
            state.toll = null;
            state.lane = null;
            updatePathSummary();
            loadTollsForHighway(state.highway.id);
          });

          // matita
          li.querySelector('.btn-edit-row').addEventListener('click', (ev) => {
            ev.stopPropagation();
            selectedItem = obj;
            currentAction = 'edit';
            crudModalTitle.textContent = getModalTitle();
            configureModalFields();
            crudModal.show();
          });

          // X
          li.querySelector('.btn-delete-row').addEventListener('click', (ev) => {
            ev.stopPropagation();
            selectedItem = obj;
            if (confirm('Sei sicuro di voler eliminare questo elemento?')) {
              doDelete();
            }
          });

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

          const name = t.nome_casello || t.nome || t.name || ('Casello ' + t.id);
          const obj  = {
            id: t.id_casello || t.id,
            name,
            limite: t.limite,
            chiuso: t.chiuso
          };

          li.innerHTML = `
            <div>
              <strong>${name}</strong>
              <div class="small-muted">
                Limite: ${t.limite || 130} km/h${t.chiuso ? ' (chiuso)' : ''}
              </div>
            </div>
            <div class="btn-group btn-group-sm">
              <button type="button" class="btn btn-outline-primary btn-edit-row">
                <i class="bi bi-pencil"></i>
              </button>
              <button type="button" class="btn btn-outline-danger btn-delete-row">
                <i class="bi bi-x"></i>
              </button>
            </div>
          `;

          makeSelectableLi(li, obj);

          // navigazione
          li.addEventListener('click', () => {
            state.toll = { id: obj.id, name };
            state.lane = null;
            updatePathSummary();
            loadLanesForToll(state.toll.id);
          });

          // matita
          li.querySelector('.btn-edit-row').addEventListener('click', ev => {
            ev.stopPropagation();
            selectedItem = obj;
            currentAction = 'edit';
            crudModalTitle.textContent = getModalTitle();
            configureModalFields();
            crudModal.show();
          });

          // X
          li.querySelector('.btn-delete-row').addEventListener('click', ev => {
            ev.stopPropagation();
            selectedItem = obj;
            if (confirm('Sei sicuro di voler eliminare questo elemento?')) {
              doDelete();
            }
          });

          itemsList.appendChild(li);
        });
      })
      .catch(err => {
        console.error('Errore caricamento caselli:', err);
        setStatus('Errore nel caricamento dei caselli.');
      });
  }

  // CORSIE per casello
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
          const li = document.createElement('li');
          li.className = 'list-group-item d-flex justify-content-between align-items-center';

          const name = 'Corsia ' + l.num_corsia;
          const obj  = {
            num_corsia: l.num_corsia,
            id_casello: l.id_casello,
            name,
            verso: l.verso,
            tipo: l.tipo_corsia,
            chiuso: l.chiuso
          };

          li.innerHTML = `
            <div>
              <strong>${name}</strong>
              <div class="small-muted">
                ${l.verso} — ${l.tipo_corsia}${l.chiuso ? ' (chiusa)' : ''}
              </div>
            </div>
            <div class="btn-group btn-group-sm">
              <button type="button" class="btn btn-outline-primary btn-edit-row">
                <i class="bi bi-pencil"></i>
              </button>
              <button type="button" class="btn btn-outline-danger btn-delete-row">
                <i class="bi bi-x"></i>
              </button>
            </div>
          `;

          makeSelectableLi(li, obj);

          // navigazione
          li.addEventListener('click', () => {
            state.lane = {
              num_corsia: obj.num_corsia,
              id_casello: obj.id_casello,
              name
            };
            updatePathSummary();
            loadDevicesForLane(state.lane.num_corsia, state.lane.id_casello);
          });

          // matita
          li.querySelector('.btn-edit-row').addEventListener('click', ev => {
            ev.stopPropagation();
            selectedItem  = obj;
            currentAction = 'edit';
            crudModalTitle.textContent = getModalTitle();
            configureModalFields();
            crudModal.show();
          });

          // X
          li.querySelector('.btn-delete-row').addEventListener('click', ev => {
            ev.stopPropagation();
            selectedItem = obj;
            if (confirm('Sei sicuro di voler eliminare questo elemento?')) {
              doDelete();
            }
          });

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
    levelTitle.textContent = 'DISPOSITIVI DI ' + state.lane.name;
    setStatus('Caricamento dispositivi...');
    itemsList.innerHTML = '';

   fetch('/api/lanes/' + encodeURIComponent(idCasello) + '/' + encodeURIComponent(numCorsia) + '/devices')
      .then(res => {
        if (!res.ok) throw new Error('HTTP ' + res.status);
        return res.json();
      })
      .then(data => {
        if (!Array.isArray(data) || data.length === 0) {
          setStatus('Nessun dispositivo trovato per questa corsia.');
          return;
        }
        setStatus('Elenco dispositivi.');
        data.forEach(d => {
          const li = document.createElement('li');
          li.className = 'list-group-item d-flex justify-content-between align-items-center';

          const type = d.tipo || d.type || 'Dispositivo';
          const id   = d.id_dispositivo || d.id;
          const stato = d.stato || '';
          const obj  = { id, tipo: type, stato };

          li.innerHTML = `
            <div>
              <strong>${type}</strong>
              <div class="small-muted">ID: ${id} — ${stato}</div>
            </div>
            <div class="btn-group btn-group-sm">
              <button type="button" class="btn btn-outline-primary btn-edit-row">
                <i class="bi bi-pencil"></i>
              </button>
              <button type="button" class="btn btn-outline-danger btn-delete-row">
                <i class="bi bi-x"></i>
              </button>
            </div>
          `;

          makeSelectableLi(li, obj);

          // matita
          li.querySelector('.btn-edit-row').addEventListener('click', (ev) => {
            ev.stopPropagation();
            selectedItem = obj;
            currentAction = 'edit';
            crudModalTitle.textContent = getModalTitle();
            configureModalFields();
            crudModal.show();
          });

          // X
          li.querySelector('.btn-delete-row').addEventListener('click', (ev) => {
            ev.stopPropagation();
            selectedItem = obj;
            if (confirm('Sei sicuro di voler eliminare questo elemento?')) {
              doDelete();
            }
          });

          itemsList.appendChild(li);
        });
      })
      .catch(err => {
        console.error('Errore caricamento dispositivi:', err);
        setStatus('Errore nel caricamento dei dispositivi.');
      });
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
    // Nascondi tutti i gruppi specifici
    groupCasello.classList.add('d-none');
    groupCorsia.classList.add('d-none');
    groupDispositivo.classList.add('d-none');

    // Trova il gruppo del campo nome
    const nameGroup = fieldName.closest('.mb-3');

    // Di default nascondi tutto
    nameGroup.classList.add('d-none');
    fieldName.required = false;
    fieldName.value = '';

    switch (currentLevel) {
      case 'regions':
        nameGroup.classList.remove('d-none');
        fieldNameLabel.textContent = 'Nome regione';
        fieldName.required = true;
        if (currentAction === 'edit' && selectedItem) {
          fieldName.value = selectedItem.name || '';
        }
        break;

      case 'highways':
        nameGroup.classList.remove('d-none');
        fieldNameLabel.textContent = 'Nome autostrada';
        fieldName.required = true;

        groupAutostrada.classList.remove('d-none');
        highwayRegionInput.value = '';
        regionSuggestionsEl.innerHTML = '';

        if (currentAction === 'create') {
          // preimposta la regione da cui stai navigando
          if (state.region) {
            highwayRegionIdsEl.value = String(state.region.id);
            cachedRegions[state.region.id] = state.region.name;
          } else {
            highwayRegionIdsEl.value = '';
          }
          renderSelectedRegions();
        } else if (currentAction === 'edit' && selectedItem) {
          // per ora nessuna gestione regioni in edit
        }
        break;

      case 'tolls':
        nameGroup.classList.remove('d-none');
        fieldNameLabel.textContent = 'Nome casello';
        fieldName.required = true;
        groupCasello.classList.remove('d-none');

        if (currentAction === 'create') {
          document.getElementById('caselloLimite').value = 130;
          document.getElementById('caselloChiuso').checked = false;
        } else if (currentAction === 'edit' && selectedItem) {
          fieldName.value = selectedItem.name || '';
          document.getElementById('caselloLimite').value = selectedItem.limite || 130;
          document.getElementById('caselloChiuso').checked = !!selectedItem.chiuso;
        }
        break;

      case 'lanes':
        // NESSUN CAMPO NOME PER CORSIE
        nameGroup.classList.add('d-none');
        fieldName.required = false;
        groupCorsia.classList.remove('d-none');

        if (currentAction === 'create') {
          document.getElementById('corsiaVerso').value = '';
          document.getElementById('corsiaTipo').value = '';
          document.getElementById('corsiaChiuso').checked = false;
        } else if (currentAction === 'edit' && selectedItem) {
          document.getElementById('corsiaVerso').value = selectedItem.verso || '';
          document.getElementById('corsiaTipo').value = selectedItem.tipo || '';
          document.getElementById('corsiaChiuso').checked = !!selectedItem.chiuso;
        }
        break;

      case 'devices':
        // NESSUN CAMPO NOME PER DISPOSITIVI
        nameGroup.classList.add('d-none');
        fieldName.required = false;
        groupDispositivo.classList.remove('d-none');

        if (currentAction === 'create') {
          document.getElementById('dispTipo').value = '';
          document.getElementById('dispStato').value = '';
        } else if (currentAction === 'edit' && selectedItem) {
          document.getElementById('dispTipo').value = selectedItem.tipo || '';
          document.getElementById('dispStato').value = selectedItem.stato || '';
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
      .then(res => {
        if (!res.ok) throw new Error('HTTP ' + res.status);
        return res.json();
      })
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
          li.addEventListener('click', () => {
            addRegionToSelected(id, name);
          });
          regionSuggestionsEl.appendChild(li);
        });
      })
      .catch(err => {
        console.error('Errore ricerca regioni:', err);
      });
  }, 300);
});


  function getEndpointAndBodyForCreate() {
    const name = fieldName.value.trim();

    switch (currentLevel) {
      case 'regions':
        return {
          url: '/api/regions',
          body: { nomeRegione: name }
        };

     case 'regions':
           return {
             url: '/api/regions',
             body: { nomeRegione: name }
           };

         case 'highways': {
           const selectedIds = highwayRegionIdsEl.value
             ? highwayRegionIdsEl.value.split(',').map(v => Number(v))
             : [];

           if (selectedIds.length === 0 && state.region) {
             selectedIds.push(state.region.id);
           }

           return {
             url: '/api/highways',
             body: {
               sigla: fieldName.value.trim(),
               idRegioni: selectedIds   // [1,3,5,...]
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
          url: `/api/lanes/${encodeURIComponent(state.lane.id_casello)}/${encodeURIComponent(state.lane.num_corsia)}/devices`,
          body: {
            tipo: document.getElementById('dispTipo').value || null,
            stato: document.getElementById('dispStato').value || null
          }
        };
    }
  }


    // Cache per i nomi delle regioni selezionate
      const cachedRegions = {};
  function getEndpointForUpdateOrDelete() {
    if (!selectedItem) return null;

    switch (currentLevel) {
      case 'regions':
        return `/api/regions/${encodeURIComponent(selectedItem.id)}`;
      case 'highways':
        return `/api/highways/${encodeURIComponent(selectedItem.id)}`;
      case 'tolls':
        return `/api/tolls/${encodeURIComponent(selectedItem.id)}`;
      case 'lanes':
        return `/api/lanes/${encodeURIComponent(selectedItem.id_casello)}/${encodeURIComponent(selectedItem.num_corsia)}`;
      case 'devices':
        return `/api/devices/${encodeURIComponent(selectedItem.id)}`;
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
    const url  = getEndpointForUpdateOrDelete();
    if (!url) return;

    const name = fieldName.value.trim();
    let body = {};

    switch (currentLevel) {
      case 'regions':
        body = { nomeRegione: name };
        break;
      case 'highways':
        body = { citta: name, idRegione: state.region.id };
        break;
      case 'tolls':
        body = {
          nome_casello: name,
          limite: Number(document.getElementById('caselloLimite').value),
          chiuso: document.getElementById('caselloChiuso').checked
        };
        break;
      case 'lanes':
        body = {
          verso: document.getElementById('corsiaVerso').value || null,
          tipo_corsia: document.getElementById('corsiaTipo').value || null,
          chiuso: document.getElementById('corsiaChiuso').checked
        };
        break;
      case 'devices':
        body = {
          tipo: document.getElementById('dispTipo').value || null,
          stato: document.getElementById('dispStato').value || null
        };
        break;
    }

    fetch(url, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
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
        console.error('Errore modifica:', err);
        alert('Errore nella modifica.');
      });
  }

  function doDelete() {
    const url = getEndpointForUpdateOrDelete();
    if (!url) return;
    fetch(url, { method: 'DELETE' })
      .then(r => {
        if (!r.ok) throw new Error('HTTP ' + r.status);
        return r.json().catch(() => ({}));
      })
      .then(() => {
        reloadCurrentLevel();
      })
      .catch(err => {
        console.error('Errore cancellazione:', err);
        alert('Errore nella cancellazione.');
      });
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