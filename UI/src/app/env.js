(function (window) {
  window.__env = window.__env || {};

  // For localhost, no need to have GTA Tag ID
  window.__env.analyticsTagID = '';

  // Setting this to false will disable console output
  window.__env.enableDebug = false;
}(this));