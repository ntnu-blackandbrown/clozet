// Netlify Build Plugin to ensure locale files are properly copied
const fs = require('fs');
const path = require('path');

module.exports = {
  onPostBuild: ({ constants, utils }) => {
    const { PUBLISH_DIR } = constants;
    const { build } = utils;

    // Source locale directory
    const localesDir = path.join(process.cwd(), 'src/locales');

    // Destination directory in the build output
    const destLocalesDir = path.join(PUBLISH_DIR, 'locales');

    // Create destination directory if it doesn't exist
    if (!fs.existsSync(destLocalesDir)) {
      fs.mkdirSync(destLocalesDir, { recursive: true });
    }

    // Copy locale files
    const localeFiles = fs.readdirSync(localesDir).filter(file => file.endsWith('.json'));

    for (const file of localeFiles) {
      const sourceFile = path.join(localesDir, file);
      const destFile = path.join(destLocalesDir, file);

      try {
        fs.copyFileSync(sourceFile, destFile);
        console.log(`✅ Copied locale file: ${file}`);
      } catch (error) {
        build.failPlugin(`Failed to copy locale file ${file}: ${error.message}`);
      }
    }

    console.log('✅ All locale files copied successfully');
  }
};
