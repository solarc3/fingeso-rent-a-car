#!/usr/bin/env node

const { execSync } = require('child_process');
const path = require('path');

// Main branch name
const MAIN_BRANCH = "main";

// Colors for terminal output
const colors = {
    reset: "\x1b[0m",
    red: "\x1b[31m",
    green: "\x1b[32m",
    yellow: "\x1b[33m",
    blue: "\x1b[34m"
};

function execCommand(command) {
    try {
        return execSync(command, { encoding: 'utf8' }).trim();
    } catch (error) {
        console.error(`Error executing command: ${command}`);
        console.error(error.message);
        process.exit(1);
    }
}

function main() {
    // Ensure the remote state is up to date
    console.log("🔄 Fetching los ultimos cambios desde origin...");
    execCommand('git fetch origin');

    // Check if there are differences between the current branch and the main branch
    let updates;
    try {
        updates = execCommand(`git log HEAD..origin/${MAIN_BRANCH} --oneline`);
    } catch (error) {
        // If the command fails (e.g., no commits found), set updates to empty string
        updates = '';
    }

    if (updates) {
        console.log(`${colors.red}⚠️  PELIGRO: TU BRANCH NO ESTA AL DIA CON ${MAIN_BRANCH}!${colors.reset}`);
        console.log(`\nLos siguientes cambios en ${MAIN_BRANCH} no han sido aplicados a tu branch:`);
        console.log(updates);
        console.log(`\n${colors.yellow}❌ Porfavor hace un merge con ${MAIN_BRANCH} antes de commitear.${colors.reset}`);
        console.log(`\n${colors.blue}👉 Para resolverlo manualmente, ejecutar los siguientes comandos en tu branch:${colors.reset}`);
        console.log("\n   git fetch origin");
        console.log(`   git merge origin/${MAIN_BRANCH}`);
        console.log("\nLuego de resolver los conflictos (en el caso que existan), se puede continuar con el commit.");
        process.exit(1);
    } else {
        console.log(`${colors.green}✅ Tu branch esta al dia con ${MAIN_BRANCH}.${colors.reset}`);
        console.log(`${colors.green}✔️ Se puede hacer el commit sin problemas${colors.reset}`);
        process.exit(0);
    }
}

// Run the main function
main();
