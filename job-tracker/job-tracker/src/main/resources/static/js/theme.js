(function () {
	var storageKey = 'job-tracker-theme';

	function currentTheme() {
		return document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
	}

	function applyTheme(theme, persist) {
		document.documentElement.setAttribute('data-theme', theme);
		document.documentElement.setAttribute('data-bs-theme', theme);
		if (persist) {
			try {
				localStorage.setItem(storageKey, theme);
			} catch (e) { /* ignore */ }
		}
		syncToggle(theme);
	}

	function syncToggle(theme) {
		var button = document.getElementById('theme-toggle');
		if (!button) {
			return;
		}
		var next = theme === 'dark' ? 'claro' : 'oscuro';
		button.setAttribute('aria-label', 'Cambiar a modo ' + next);
		button.setAttribute('title', 'Cambiar a modo ' + next);
		button.setAttribute('aria-pressed', theme === 'dark' ? 'true' : 'false');
	}

	function init() {
		syncToggle(currentTheme());
		var button = document.getElementById('theme-toggle');
		if (button) {
			button.addEventListener('click', function () {
				applyTheme(currentTheme() === 'dark' ? 'light' : 'dark', true);
			});
		}
		var media = window.matchMedia('(prefers-color-scheme: dark)');
		var onChange = function (event) {
			try {
				if (localStorage.getItem(storageKey)) {
					return;
				}
			} catch (e) { /* ignore */ }
			applyTheme(event.matches ? 'dark' : 'light', false);
		};
		if (media.addEventListener) {
			media.addEventListener('change', onChange);
		} else if (media.addListener) {
			media.addListener(onChange);
		}
	}

	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', init);
	} else {
		init();
	}
})();
