@echo off

if not exist hooks mkdir hooks
git config --local core.hooksPath hooks

echo ✅ Git hooks configurados exitosamente