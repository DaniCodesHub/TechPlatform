
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$container = "techplatform-sqlserver"
$sqlcmd = "/opt/mssql-tools18/bin/sqlcmd"
$services = @(
    "product-service",
    "order-service",
    "user-service",
    "api-gateway"
)

function Assert-Success {
    param([string]$Message)

    if ($LASTEXITCODE -ne 0) {
        throw $Message
    }
}

function Invoke-SqlFile {
    param(
        [string]$File,
        [string]$Database = "master"
    )

    docker cp $File "${container}:/tmp/techplatform-setup.sql"
    Assert-Success "No se pudo copiar el archivo SQL."

    docker exec $container bash -c `
        'exec /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -C -b -d "$1" -i /tmp/techplatform-setup.sql' `
        bash $Database

    Assert-Success "Error ejecutando SQL en $Database."
}

function Test-SqlReady {
    docker exec $container bash -c `
        'exec /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -C -b -i /tmp/techplatform-check.sql -o /dev/null' `
        2>$null | Out-Null

    return ($LASTEXITCODE -eq 0)
}

function Wait-SqlReady {
    for ($i = 0; $i -lt 90; $i++) {
        if (Test-SqlReady) {
            return
        }

        Start-Sleep -Seconds 2
    }

    throw "SQL Server no ha respondido."
}

function Wait-UsersTable {
    for ($i = 0; $i -lt 90; $i++) {
        docker exec $container bash -c `
            'exec /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -C -b -d user_db -i /tmp/techplatform-check-users.sql -o /dev/null' `
            2>$null | Out-Null

        if ($LASTEXITCODE -eq 0) {
            return
        }

        Start-Sleep -Seconds 2
    }

    throw "La tabla users no se ha creado."
}

if (-not (Test-Path ".env")) {
    throw "Falta .env. Configuralo a partir de .env.example."
}

if (-not (Test-Path "scripts/init-databases.sql")) {
    throw "No se encuentra scripts/init-databases.sql."
}

Write-Host "1. Iniciando SQL Server..."

docker compose up -d sqlserver
Assert-Success "No se pudo iniciar SQL Server."

# Consulta de comprobacion: sin argumentos SQL con comillas.
$checkFile = Join-Path $env:TEMP "techplatform-check.sql"

[System.IO.File]::WriteAllText(
    $checkFile,
    "SELECT 1;",
    [System.Text.UTF8Encoding]::new($false)
)

try {
    docker cp $checkFile "${container}:/tmp/techplatform-check.sql"
    Assert-Success "No se pudo preparar la comprobacion SQL."
}
finally {
    Remove-Item $checkFile -ErrorAction SilentlyContinue
}

Write-Host "2. Esperando a SQL Server..."
Wait-SqlReady

Write-Host "3. Creando bases de datos..."
Invoke-SqlFile "scripts/init-databases.sql"

Write-Host "4. Compilando microservicios..."

foreach ($service in $services) {
    Write-Host "Compilando $service..."

    Push-Location $service

    try {
        if (Test-Path "mvnw.cmd") {
            & .\mvnw.cmd clean package -DskipTests
        }
        else {
            & mvn clean package -DskipTests
        }

        Assert-Success "Error compilando $service."
    }
    finally {
        Pop-Location
    }
}

Write-Host "5. Construyendo y arrancando contenedores..."

docker compose up -d --build
Assert-Success "No se pudieron arrancar los contenedores."

# Esperar a que Hibernate cree la tabla users.
$usersCheckSql = @'
SET NOCOUNT ON;

IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    RAISERROR('Tabla users pendiente', 16, 1);
END;
'@

$usersCheckFile = Join-Path $env:TEMP "techplatform-check-users.sql"

[System.IO.File]::WriteAllText(
    $usersCheckFile,
    $usersCheckSql,
    [System.Text.UTF8Encoding]::new($false)
)

try {
    docker cp $usersCheckFile "${container}:/tmp/techplatform-check-users.sql"
    Assert-Success "No se pudo preparar la comprobacion de usuarios."
}
finally {
    Remove-Item $usersCheckFile -ErrorAction SilentlyContinue
}

Write-Host "6. Esperando a que Hibernate cree la tabla users..."
Wait-UsersTable

Write-Host "7. Configurando administrador de demostracion..."

$adminSql = @'
SET QUOTED_IDENTIFIER ON;

IF NOT EXISTS (
    SELECT 1 FROM dbo.users
    WHERE email = 'user@techplatform.com'
)
BEGIN
    INSERT INTO dbo.users (name, email, password, role)
    VALUES (
        'Demo Administrator',
        'user@techplatform.com',
        '$2a$10$/DVSpI5fcRhtPm/cmy4b8um.eOmc50hv4jhbC5XQN2yLpNhE8fB4G',
        'ADMIN'
    );
END
ELSE
BEGIN
    UPDATE dbo.users
    SET role = 'ADMIN'
    WHERE email = 'user@techplatform.com';
END;
'@

$adminFile = Join-Path $env:TEMP "techplatform-admin.sql"

[System.IO.File]::WriteAllText(
    $adminFile,
    $adminSql,
    [System.Text.UTF8Encoding]::new($false)
)

try {
    Invoke-SqlFile $adminFile "user_db"
}
finally {
    Remove-Item $adminFile -ErrorAction SilentlyContinue
}

Write-Host ""
Write-Host "TechPlatform inicializada correctamente."
Write-Host "Frontend: http://localhost:4200"
Write-Host "Swagger: http://localhost:8080/swagger-ui.html"
Write-Host "Usuario: user@techplatform.com"
Write-Host "Contrasena: 1234"
