# Fix compilation script - Clean Code approach by Tricortex
Write-Host "Starting systematic code cleanup..." -ForegroundColor Green

# Navigate to project directory
cd "C:\Users\LENOVO\OneDrive\Área de Trabalho\AuroraLedgerSantander\AuroraLedger"

# Pattern fixes for common issues
Write-Host "Fixing lambda syntax issues..." -ForegroundColor Yellow

# Fix lambda arrows
Get-ChildItem -Recurse -Include *.java | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace '\(\) >', '() ->'
    $content = $content -replace 'entry >', 'entry ->'
    $content = $content -replace '\) >', ') ->'
    $content = $content -replace ', handlers\) >', ', handlers) ->'
    Set-Content $_.FullName $content
}

# Fix comparison operators that got corrupted
Write-Host "Fixing comparison operators..." -ForegroundColor Yellow
Get-ChildItem -Recurse -Include *.java | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace '\.scale\(\) >', '.scale() >'
    $content = $content -replace '\.size\(\) >', '.size() >'
    $content = $content -replace '\.length\(\) >=', '.length() >='
    Set-Content $_.FullName $content
}

# Fix switch case syntax
Write-Host "Fixing switch expressions..." -ForegroundColor Yellow
Get-ChildItem -Recurse -Include *.java | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $content = $content -replace 'case ([A-Z_]+) >', 'case $1 ->'
    $content = $content -replace 'default >', 'default ->'
    Set-Content $_.FullName $content
}

Write-Host "Code cleanup completed!" -ForegroundColor Green
Write-Host "Attempting compilation..." -ForegroundColor Cyan

# Try compilation
mvn clean compile -q

Write-Host "Compilation attempt finished. Check results above." -ForegroundColor Magenta