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
 * Exporta la sesión de LinkedIn a un archivo storage-state para Playwright.
 *
 * Ejecutar desde job-tracker/:
 *   ./mvnw linkedin-session
 *   ./mvnw linkedin-session -Dsession.file=/ruta/custom.json
 */
public final class LinkedInSessionExporter {

	private LinkedInSessionExporter() {
	}

	public static void main(String[] args) throws Exception {
		String defaultPath = Path.of(System.getProperty("user.home"), "linkedin-storage-state.json").toString();
		Path output = Paths.get(args.length > 0 ? args[0] : defaultPath);

		System.out.println("Archivo de salida: " + output.toAbsolutePath());
		if (PlaywrightLaunchHelper.isWsl()) {
			System.out.println();
			System.out.println("WSL detectado. Si la ventana sale en blanco, exporta desde Windows:");
			System.out.println("  .\\scripts\\save-linkedin-session.ps1");
			System.out.println();
		}
		System.out.println();

		try (Playwright playwright = Playwright.create()) {
			Browser browser = playwright.chromium().launch(PlaywrightLaunchHelper.headedInteractive());

			BrowserContext context = browser.newContext();
			Page page = context.newPage();
			page.navigate("https://www.linkedin.com/login");

			System.out.println("1. Inicia sesión en LinkedIn en la ventana del navegador.");
			System.out.println("2. Cuando veas tu feed o inicio, vuelve aquí y presiona ENTER.");
			new BufferedReader(new InputStreamReader(System.in)).readLine();

			context.storageState(new BrowserContext.StorageStateOptions().setPath(output));
			browser.close();
		}

		System.out.println();
		System.out.println("Sesión guardada correctamente.");
		System.out.println("Configura esta ruta en http://localhost:8080/scraping");
		System.out.println("  LinkedIn session file: " + output.toAbsolutePath());
	}
}
