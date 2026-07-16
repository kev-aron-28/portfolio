package com.projects.job_tracker.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * Exporta la sesión de Indeed México a un archivo storage-state para Playwright.
 *
 * Ejecutar desde job-tracker/:
 *   ./mvnw exec:java@indeed-session
 *   ./mvnw exec:java@indeed-session -Dindeed.session.file=/ruta/custom.json
 */
public final class IndeedSessionExporter {

	private static final String HOME_URL = "https://mx.indeed.com";

	private IndeedSessionExporter() {
	}

	public static void main(String[] args) throws Exception {
		String defaultPath = Path.of(System.getProperty("user.home"), "indeed-storage-state.json").toString();
		Path output = Paths.get(args.length > 0 ? args[0] : defaultPath);

		System.out.println("Archivo de salida: " + output.toAbsolutePath());
		if (PlaywrightLaunchHelper.isWsl()) {
			System.out.println();
			System.out.println("WSL detectado. Si la ventana sale en blanco, exporta desde Windows:");
			System.out.println("  .\\scripts\\save-indeed-session.cmd");
			System.out.println();
		}
		System.out.println();

		try (Playwright playwright = Playwright.create()) {
			Browser browser = playwright.chromium().launch(PlaywrightLaunchHelper.headedInteractive());

			BrowserContext context = browser.newContext();
			Page page = context.newPage();
			page.navigate(HOME_URL);

			System.out.println("1. Completa el challenge de Indeed si aparece (Cloudflare / captcha).");
			System.out.println("2. Inicia sesión si lo deseas (opcional pero recomendado).");
			System.out.println("3. Cuando veas la página principal de Indeed, vuelve aquí y presiona ENTER.");
			new BufferedReader(new InputStreamReader(System.in)).readLine();

			context.storageState(new BrowserContext.StorageStateOptions().setPath(output));
			browser.close();
		}

		System.out.println();
		System.out.println("Sesión guardada correctamente.");
		System.out.println("Configura esta ruta en http://localhost:8080/scraping");
		System.out.println("  Indeed session file: " + output.toAbsolutePath());
	}
}
