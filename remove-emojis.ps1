#!/usr/bin/env pwsh

# Script para remover emojis de arquivos Markdown
# Remove todos os emojis Unicode seguindo diretrizes corporativas

Write-Host "Iniciando limpeza de emojis nos arquivos Markdown..."

# Obter todos arquivos .md
$markdownFiles = Get-ChildItem -Path "." -Filter "*.md" -Recurse

foreach ($file in $markdownFiles) {
    Write-Host "Processando: $($file.FullName)"
    
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
    $originalContent = $content
    
    # Substituir emojis específicos
    $content = $content -replace '🎯', '[TARGET]'
    $content = $content -replace '🏆', '[ACHIEVEMENT]'
    $content = $content -replace '✅', '[DONE]'
    $content = $content -replace '💚', '[SUCCESS]'
    $content = $content -replace '🚀', '[DEPLOY]'
    $content = $content -replace '📊', '[METRICS]'
    $content = $content -replace '⚡', '[PERFORMANCE]'
    $content = $content -replace '📈', '[GROWTH]'
    $content = $content -replace '🔒', '[SECURITY]'
    $content = $content -replace '💫', '[ENHANCEMENT]'
    $content = $content -replace '🎊', '[CELEBRATION]'
    $content = $content -replace '🎉', '[SUCCESS]'
    $content = $content -replace '🔥', '[HOT]'
    $content = $content -replace '💡', '[IDEA]'
    $content = $content -replace '🌟', '[STAR]'
    $content = $content -replace '⭐', '[STAR]'
    $content = $content -replace '🔄', '[IN_PROGRESS]'
    $content = $content -replace '📋', '[PLANNED]'
    
    # Remover outros emojis Unicode (range geral)
    $content = $content -replace '[\u{1F300}-\u{1F9FF}]', '[EMOJI_REMOVED]'
    
    # Salvar apenas se houver mudanças
    if ($content -ne $originalContent) {
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8
        Write-Host "ATUALIZADO: $($file.FullName)" -ForegroundColor Green
    } else {
        Write-Host "Nenhum emoji encontrado em: $($file.FullName)" -ForegroundColor Yellow
    }
}

Write-Host "Limpeza de emojis concluída." -ForegroundColor Cyan