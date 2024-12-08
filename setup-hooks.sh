#!/bin/sh
mkdir -p hooks

# Dar permisos de ejecución a los archivos de hooks
chmod +x hooks/pre-commit
chmod +x hooks/pre-commit.js

# Configurar git para usar la carpeta hooks personalizada
git config --local core.hooksPath hooks

echo "✅ Git hooks configurados exitosamente"