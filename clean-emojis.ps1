#!/usr/bin/env pwsh

# Script para remover emojis de arquivos Markdown
Write-Host "Iniciando limpeza de emojis nos arquivos Markdown..."

# Obter todos arquivos .md
$markdownFiles = Get-ChildItem -Path "." -Filter "*.md" -Recurse

foreach ($file in $markdownFiles) {
    Write-Host "Processando: $($file.FullName)"
    
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
    $originalContent = $content
    
    # Remover emojis usando regex pattern para Unicode emoji range
    $content = $content -replace '[\u{1F300}-\u{1F9FF}]', ''
    $content = $content -replace '[\u{2600}-\u{26FF}]', ''
    $content = $content -replace '[\u{2700}-\u{27BF}]', ''
    
    # Remover checkmarks e outros símbolos específicos
    $content = $content -replace '✅', '[COMPLETED]'
    $content = $content -replace '❌', '[FAILED]'
    $content = $content -replace '⚠️', '[WARNING]'
    $content = $content -replace '📋', '[CHECKLIST]'
    $content = $content -replace '🔄', '[IN_PROGRESS]'
    
    # Salvar apenas se houver mudanças
    if ($content -ne $originalContent) {
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8
        Write-Host "ATUALIZADO: $($file.FullName)" -ForegroundColor Green
    } else {
        Write-Host "Nenhum emoji encontrado em: $($file.Name)" -ForegroundColor Yellow
    }
}

Write-Host "Limpeza de emojis concluída." -ForegroundColor Cyan