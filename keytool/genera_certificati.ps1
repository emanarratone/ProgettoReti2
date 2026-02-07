# --- CONFIGURAZIONE ---
$KeytoolPath = "C:\Users\salva\Desktop\backup M2\android studio\jbr\bin\keytool.exe"
$Password = "changeit"
$CA_Alias = "MyLocalCA"
$DNameCA = "CN=MyLocalCA, OU=Reti2, O=Uni, L=Citta, S=Provincia, C=IT"
$DNameServizi = "CN=localhost, OU=Reti2, O=Uni, L=Citta, S=Provincia, C=IT"
$Servizi = @("frontend", "autostrada", "casello", "corsia", "regione", "dispositivi", "biglietto", "pagamento", "multa", "veicolo", "utente")

Write-Host "1. Generazione della Root CA..." -ForegroundColor Cyan
# Generiamo la Root CA
& $KeytoolPath -genkeypair -alias $CA_Alias -keyalg RSA -keysize 4096 -ext bc:ca:true `
    -keystore "rootCA.jks" -storepass $Password -keypass $Password `
    -dname $DNameCA -validity 3650

# Esportiamo il certificato pubblico della CA
& $KeytoolPath -exportcert -alias $CA_Alias -file "MyLocalCA.crt" `
    -keystore "rootCA.jks" -storepass $Password

# Creiamo il TrustStore unico (quello che permetterà ai servizi di fidarsi tra loro)
& $KeytoolPath -importcert -alias $CA_Alias -file "MyLocalCA.crt" `
    -keystore "truststore.jks" -storepass $Password -noprompt

Write-Host "2. Generazione KeyStore per ogni microservizio..." -ForegroundColor Cyan

foreach ($s in $Servizi) {
    $ksName = "$s-keystore.jks"
    $csrName = "$s.csr"
    $crtName = "$s.crt"

    Write-Host "Elaborazione: $s" -ForegroundColor Yellow

    # A. Genera KeyPair per il servizio
    & $KeytoolPath -genkeypair -alias $s -keyalg RSA -keysize 2048 `
        -keystore $ksName -storepass $Password -keypass $Password `
        -dname $DNameServizi `
        -ext "SAN=dns:localhost,ip:127.0.0.1" -validity 365

    # B. Genera richiesta di firma (CSR)
    & $KeytoolPath -certreq -alias $s -file $csrName -keystore $ksName -storepass $Password

    # C. Firma il certificato del servizio usando la Root CA
    & $KeytoolPath -certres -alias $CA_Alias -keystore "rootCA.jks" -storepass $Password `
        -infile $csrName -outfile $crtName

    # D. Importa la CA nel keystore del servizio (necessario per completare la catena)
    & $KeytoolPath -importcert -alias $CA_Alias -file "MyLocalCA.crt" `
        -keystore $ksName -storepass $Password -noprompt

    # E. Importa il certificato finale firmato nel keystore del servizio
    & $KeytoolPath -importcert -alias $s -file $crtName `
        -keystore $ksName -storepass $Password -noprompt

    # Pulizia file temporanei di firma
    if (Test-Path $csrName) { Remove-Item $csrName }
    if (Test-Path $crtName) { Remove-Item $crtName }
}

Write-Host "------------------------------------------------" -ForegroundColor Green
Write-Host "Operazione completata con successo!" -ForegroundColor Green
Write-Host "File generati nella cartella corrente." -ForegroundColor Green