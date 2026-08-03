/**
 * Platform theme toggle — persists light/dark in localStorage.
 */
(function () {
  var STORAGE_KEY = 'km-theme';

  function preferred() {
    try {
      var stored = localStorage.getItem(STORAGE_KEY);
      if (stored === 'light' || stored === 'dark') {
        return stored;
      }
    } catch (e) {
      /* ignore */
    }
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }
    return 'light';
  }

  function apply(theme) {
    document.documentElement.setAttribute('data-bs-theme', theme);
    try {
      localStorage.setItem(STORAGE_KEY, theme);
    } catch (e) {
      /* ignore */
    }
    document.dispatchEvent(new CustomEvent('km-theme-change', { detail: { theme: theme } }));
  }

  function current() {
    return document.documentElement.getAttribute('data-bs-theme') === 'dark' ? 'dark' : 'light';
  }

  function toggle() {
    apply(current() === 'dark' ? 'light' : 'dark');
  }

  // Ensure attribute exists even if head script missed
  if (!document.documentElement.getAttribute('data-bs-theme')) {
    apply(preferred());
  }

  document.addEventListener('DOMContentLoaded', function () {
    var btn = document.getElementById('themeToggle');
    if (btn) {
      btn.addEventListener('click', toggle);
    }
  });

  window.KmTheme = {
    apply: apply,
    toggle: toggle,
    current: current
  };
})();
