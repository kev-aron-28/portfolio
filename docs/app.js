const CONFIG = {
  owner: "kev-aron-28",
  repo: "portfolio",
  branch: "main",
  apiBase: "https://api.github.com/repos/kev-aron-28/portfolio",
  rawBase: "https://raw.githubusercontent.com/kev-aron-28/portfolio/main",
};

const EXCLUDED_DIRS = new Set([
  "website",
  "docs",
  "node_modules",
  "target",
  "build",
]);

const SOURCE_EXTENSIONS = {
  java: "Java",
  cbl: "COBOL",
  cob: "COBOL",
  cpy: "COBOL",
  ts: "TypeScript",
  js: "JavaScript",
  tf: "Terraform",
  ino: "C++",
  py: "Python",
  go: "Go",
  rs: "Rust",
  c: "C",
  cpp: "C++",
  h: "C",
  kt: "Kotlin",
};

const TITLE_WORDS = {
  cobol: "COBOL",
  cics: "CICS",
  db2: "DB2",
  aws: "AWS",
  url: "URL",
  esp32: "ESP32",
  bms: "BMS",
  api: "API",
  http: "HTTP",
  iam: "IAM",
};

const statusEl = document.getElementById("projects-status");
const featuredBlock = document.getElementById("featured-block");
const otherBlock = document.getElementById("other-block");
const featuredList = document.getElementById("featured-list");
const otherList = document.getElementById("other-list");

init();

async function init() {
  const catalog = await loadCatalog();
  if (!catalog) {
    showStatus("Could not load the project list.");
    return;
  }

  try {
    const github = await loadGitHubData(catalog);
    const projects = await buildProjects(catalog, github);
    renderProjects(projects);
  } catch (error) {
    console.error(error);
    showStatus(
      "GitHub is unavailable right now. Project names below still link to the repository."
    );
    renderProjects(fallbackProjects(catalog), { keepStatus: true });
  }
}

async function loadCatalog() {
  try {
    const response = await fetch("./projects.json", { cache: "no-store" });
    if (!response.ok) {
      throw new Error("catalog");
    }
    const data = await response.json();
    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.error(error);
    return null;
  }
}

async function loadGitHubData(catalog) {
  const [repoResult, contentsResult, treeResult] = await Promise.allSettled([
    githubJson(CONFIG.apiBase),
    githubJson(`${CONFIG.apiBase}/contents/`),
    githubJson(`${CONFIG.apiBase}/git/trees/${CONFIG.branch}?recursive=1`, 20000),
  ]);

  const repo = valueOf(repoResult);
  const contents = valueOf(contentsResult);
  const tree = valueOf(treeResult)?.tree || [];
  const dirs = await resolveProjectDirs(contents, tree, repo, catalog);

  if (dirs.length === 0) {
    throw new Error("No project directories found");
  }

  return { repo, dirs, tree };
}

async function githubJson(url, timeoutMs = 12000) {
  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), timeoutMs);

  try {
    const response = await fetch(url, {
      headers: { Accept: "application/vnd.github+json" },
      signal: controller.signal,
    });

    if (!response.ok) {
      throw new Error(`GitHub ${response.status}`);
    }

    return response.json();
  } finally {
    clearTimeout(timer);
  }
}

function valueOf(result) {
  return result.status === "fulfilled" ? result.value : null;
}

async function resolveProjectDirs(contents, tree, repo, catalog) {
  const branch = repo?.default_branch || CONFIG.branch;
  const htmlBase =
    repo?.html_url || `https://github.com/${CONFIG.owner}/${CONFIG.repo}`;
  const topLevel = listTopLevelDirs(contents, tree, htmlBase, branch);
  const expanded = [];

  for (const dir of topLevel) {
    const children = await listChildProjects(dir, tree, htmlBase, branch);
    if (children.length > 0) {
      expanded.push(...children);
    } else {
      expanded.push(dir);
    }
  }

  return mergeCatalogPaths(expanded, catalog, htmlBase, branch);
}

function listTopLevelDirs(contents, tree, htmlBase, branch) {
  if (Array.isArray(contents)) {
    return contents
      .filter((item) => item.type === "dir" && isProjectDir(item.name))
      .map((item) => ({
        name: item.name,
        url: item.html_url,
      }));
  }

  const names = new Set();
  for (const node of tree) {
    const top = node.path.split("/")[0];
    if (node.path.includes("/") && isProjectDir(top)) {
      names.add(top);
    }
  }

  return [...names].map((name) => projectDir(name, htmlBase, branch));
}

async function listChildProjects(dir, tree, htmlBase, branch) {
  if (tree.length > 0) {
    const children = childDirNames(tree, dir.name);
    if (!isTerraformCollection(dir.name, tree, children)) {
      return [];
    }
    return children.map((child) =>
      projectDir(`${dir.name}/${child}`, htmlBase, branch)
    );
  }

  if (!/terraform/i.test(dir.name)) {
    return [];
  }

  try {
    const items = await githubJson(`${CONFIG.apiBase}/contents/${dir.name}`);
    const childDirs = Array.isArray(items)
      ? items.filter((item) => item.type === "dir" && isProjectDir(item.name))
      : [];

    if (childDirs.length < 2) {
      return [];
    }

    return childDirs.map((item) => ({
      name: `${dir.name}/${item.name}`,
      url: item.html_url,
    }));
  } catch (error) {
    console.error(error);
    return [];
  }
}

function mergeCatalogPaths(dirs, catalog, htmlBase, branch) {
  const map = new Map(dirs.map((dir) => [dir.name, dir]));

  for (const item of catalog || []) {
    const path = item?.repo;
    if (!path || !path.includes("/") || map.has(path)) {
      continue;
    }
    map.set(path, projectDir(path, htmlBase, branch));
  }

  const nestedParents = new Set(
    [...map.keys()]
      .filter((name) => name.includes("/"))
      .map((name) => name.split("/")[0])
  );

  return [...map.values()].filter((dir) => !nestedParents.has(dir.name));
}

function projectDir(name, htmlBase, branch) {
  return {
    name,
    url: `${htmlBase}/tree/${branch}/${name}`,
  };
}

function childDirNames(tree, parent) {
  const prefix = `${parent}/`;
  const names = new Set();

  for (const node of tree) {
    if (!node.path.startsWith(prefix)) {
      continue;
    }
    const rest = node.path.slice(prefix.length);
    const slash = rest.indexOf("/");
    if (slash === -1) {
      continue;
    }
    const child = rest.slice(0, slash);
    if (isProjectDir(child)) {
      names.add(child);
    }
  }

  return [...names].sort();
}

function isTerraformCollection(parent, tree, children) {
  if (children.length < 2) {
    return false;
  }

  const prefix = `${parent}/`;
  const hasRootTf = tree.some(
    (node) =>
      node.type === "blob" &&
      node.path.startsWith(prefix) &&
      !node.path.slice(prefix.length).includes("/") &&
      node.path.endsWith(".tf")
  );

  if (hasRootTf) {
    return false;
  }

  const terraformChildren = children.filter((child) =>
    tree.some(
      (node) =>
        node.type === "blob" &&
        node.path.startsWith(`${parent}/${child}/`) &&
        node.path.endsWith(".tf")
    )
  );

  return terraformChildren.length >= 2;
}

function isProjectDir(name) {
  return Boolean(name) && !name.startsWith(".") && !EXCLUDED_DIRS.has(name);
}

async function buildProjects(catalog, github) {
  const dirMap = new Map(github.dirs.map((dir) => [dir.name, dir]));
  const ordered = orderedEntries(catalog, dirMap);
  const readmeByRepo = await loadReadmes(ordered, github.tree);

  return ordered.map((entry) => {
    const dir = dirMap.get(entry.repo);
    const files = filesFor(github.tree, entry.repo);
    const readme = readmeByRepo.get(entry.repo) || "";
    const language =
      inferLanguage(files) ||
      inferLanguageFromReadme(readme) ||
      (/terraform/i.test(entry.repo) ? "Terraform" : "");
    const inferredTech = unique([
      ...inferTech(files, language),
      ...inferTechFromReadme(readme, language),
    ]);
    const tech = unique([...(entry.tech || []), ...inferredTech]).slice(0, 3);

    return {
      repo: entry.repo,
      featured: Boolean(entry.featured),
      name: entry.title || formatName(entry.repo),
      description: entry.description || extractDescription(readme),
      language,
      tech,
      url:
        dir?.url ||
        `https://github.com/${CONFIG.owner}/${CONFIG.repo}/tree/${CONFIG.branch}/${entry.repo}`,
    };
  });
}

function orderedEntries(catalog, dirMap) {
  const seen = new Set();
  const entries = [];

  for (const item of sortCatalog(catalog)) {
    const matches = matchingRepos(item.repo, dirMap);
    for (const repo of matches) {
      if (seen.has(repo)) {
        continue;
      }
      seen.add(repo);
      entries.push({
        ...item,
        repo,
        title: repo === item.repo ? item.title : undefined,
        description: repo === item.repo ? item.description : undefined,
        tech: repo === item.repo ? item.tech : undefined,
      });
    }
  }

  for (const dir of dirMap.values()) {
    if (seen.has(dir.name)) {
      continue;
    }
    seen.add(dir.name);
    entries.push({ repo: dir.name, featured: false });
  }

  return entries;
}

function matchingRepos(repo, dirMap) {
  if (!repo) {
    return [];
  }
  if (dirMap.has(repo)) {
    return [repo];
  }
  return [...dirMap.keys()]
    .filter((name) => name.startsWith(`${repo}/`))
    .sort();
}

function sortCatalog(catalog) {
  return catalog
    .map((item, index) => ({ item, index }))
    .sort((a, b) => {
      const orderA = a.item.order ?? Number.MAX_SAFE_INTEGER;
      const orderB = b.item.order ?? Number.MAX_SAFE_INTEGER;
      if (orderA !== orderB) {
        return orderA - orderB;
      }
      return a.index - b.index;
    })
    .map(({ item }) => item);
}

function fallbackProjects(catalog) {
  return sortCatalog(catalog).map((entry) => ({
    repo: entry.repo,
    featured: Boolean(entry.featured),
    name: entry.title || formatName(entry.repo),
    description: entry.description || "",
    language: /terraform/i.test(entry.repo) ? "Terraform" : "",
    tech: entry.tech || (/terraform/i.test(entry.repo) ? ["AWS"] : []),
    url: `https://github.com/${CONFIG.owner}/${CONFIG.repo}/tree/${CONFIG.branch}/${entry.repo}`,
  }));
}

async function loadReadmes(entries, tree) {
  const results = await Promise.allSettled(
    entries.map(async (entry) => {
      const candidates = findReadmePath(tree, entry.repo) || [
        `${entry.repo}/README.md`,
        `${entry.repo}/readme.md`,
      ];

      for (const path of candidates) {
        const response = await fetch(`${CONFIG.rawBase}/${path}`);
        if (response.ok) {
          return [entry.repo, await response.text()];
        }
      }

      return [entry.repo, ""];
    })
  );

  const map = new Map();
  for (const result of results) {
    if (result.status === "fulfilled") {
      map.set(result.value[0], result.value[1]);
    }
  }
  return map;
}

function findReadmePath(tree, repo) {
  const matches = tree
    .filter(
      (node) =>
        node.type === "blob" &&
        node.path.startsWith(`${repo}/`) &&
        /readme\.md$/i.test(node.path)
    )
    .sort((a, b) => a.path.split("/").length - b.path.split("/").length);

  return matches[0] ? [matches[0].path] : null;
}

function filesFor(tree, repo) {
  return tree
    .filter((node) => node.type === "blob" && node.path.startsWith(`${repo}/`))
    .map((node) => node.path)
    .filter((path) => !isIgnoredPath(path));
}

function isIgnoredPath(path) {
  return /\/(target|build|node_modules|\.git|\.mvn)\//.test(path);
}

function inferLanguage(files) {
  const counts = new Map();

  for (const file of files) {
    const ext = extensionOf(file);
    const language = SOURCE_EXTENSIONS[ext];
    if (!language) {
      continue;
    }
    counts.set(language, (counts.get(language) || 0) + 1);
  }

  let best = "";
  let bestCount = 0;
  for (const [language, count] of counts) {
    if (count > bestCount) {
      best = language;
      bestCount = count;
    }
  }
  return best;
}

function inferTech(files, language) {
  const techs = [];

  if (files.some((file) => /application\.(yml|yaml|properties)$/i.test(file))) {
    techs.push("Spring Boot");
  }
  if (files.some((file) => /\/maps\/|\.bms$/i.test(file))) {
    techs.push("CICS");
  }
  if (language === "COBOL" && files.some((file) => /\/sql\/|\.sql$/i.test(file))) {
    techs.push("DB2");
  }
  if (files.some((file) => /\.tf$/.test(file))) {
    techs.push("Terraform");
    techs.push("AWS");
  }
  if (files.some((file) => /dockerfile$/i.test(file) || /docker-compose/i.test(file))) {
    techs.push("Docker");
  }
  if (files.some((file) => /\/k8s\//.test(file))) {
    techs.push("Kubernetes");
  }
  if (files.some((file) => /\.ino$/.test(file))) {
    techs.push("ESP32");
  }
  if (files.some((file) => /\/jcl\//.test(file))) {
    techs.push("JCL");
  }

  return unique(techs.filter((tech) => tech !== language)).slice(0, 3);
}

function inferLanguageFromReadme(markdown) {
  if (!markdown) {
    return "";
  }
  const rules = [
    [/\bCOBOL\b/i, "COBOL"],
    [/\bTerraform\b/i, "Terraform"],
    [/\bTypeScript\b/i, "TypeScript"],
    [/\bESP32\b/i, "C++"],
    [/\bJava\b/i, "Java"],
  ];
  for (const [pattern, language] of rules) {
    if (pattern.test(markdown)) {
      return language;
    }
  }
  return "";
}

function inferTechFromReadme(markdown, language) {
  if (!markdown) {
    return [];
  }
  const techs = [];
  if (/\bSpring Boot\b/i.test(markdown)) techs.push("Spring Boot");
  if (/\bCICS\b/i.test(markdown)) techs.push("CICS");
  if (/\bDB2\b/i.test(markdown)) techs.push("DB2");
  if (/\bJCL\b/i.test(markdown)) techs.push("JCL");
  if (/\bDocker\b/i.test(markdown)) techs.push("Docker");
  if (/\bKubernetes\b/i.test(markdown)) techs.push("Kubernetes");
  if (/\bAWS\b/i.test(markdown)) techs.push("AWS");
  if (/\bTerraform\b/i.test(markdown)) techs.push("Terraform");
  if (/\bESP32\b/i.test(markdown)) techs.push("ESP32");
  return unique(techs.filter((tech) => tech !== language)).slice(0, 3);
}

function extractDescription(markdown) {
  if (!markdown) {
    return "";
  }

  const withoutCode = markdown.replace(/```[\s\S]*?```/g, "");
  const blocks = withoutCode.split(/\n\s*\n/);
  const paragraphs = [];

  for (const block of blocks) {
    const lines = block
      .split("\n")
      .map((line) => line.trim())
      .filter(
        (line) =>
          line &&
          !line.startsWith("#") &&
          !line.startsWith("-") &&
          !line.startsWith("*") &&
          !line.startsWith(">") &&
          !line.startsWith("|") &&
          !line.startsWith("![") &&
          !/^\d+\.\s/.test(line)
      );

    const paragraph = lines
      .join(" ")
      .replace(/\[([^\]]+)\]\([^)]+\)/g, "$1")
      .replace(/[*_`]/g, "")
      .replace(/\s+/g, " ")
      .trim();

    if (paragraph.length >= 24) {
      paragraphs.push(paragraph);
    }

    if (paragraphs.length >= 2 || paragraphs.join(" ").length >= 220) {
      break;
    }
  }

  if (paragraphs.length === 0) {
    return "";
  }

  return truncate(paragraphs.join(" "), 200);
}

function formatName(repo) {
  const leaf = repo.split("/").pop();
  return leaf
    .split(/[-_]/g)
    .map((word) => TITLE_WORDS[word.toLowerCase()] || capitalize(word))
    .join(" ");
}

function capitalize(word) {
  if (!word) {
    return "";
  }
  return word.charAt(0).toUpperCase() + word.slice(1);
}

function extensionOf(path) {
  const base = path.split("/").pop() || "";
  const parts = base.split(".");
  return parts.length > 1 ? parts.pop().toLowerCase() : "";
}

function truncate(text, max) {
  if (text.length <= max) {
    return text;
  }
  return `${text.slice(0, max).replace(/\s+\S*$/, "")}…`;
}

function unique(items) {
  return [...new Set(items)];
}

function showStatus(message) {
  statusEl.hidden = false;
  statusEl.textContent = message;
}

function clearStatus() {
  statusEl.hidden = true;
  statusEl.textContent = "";
}

function renderProjects(projects, options = {}) {
  featuredList.replaceChildren();
  otherList.replaceChildren();

  const featured = projects.filter((project) => project.featured);
  const other = projects.filter((project) => !project.featured);

  if (featured.length > 0) {
    featuredBlock.hidden = false;
    for (const project of featured) {
      featuredList.append(createCard(project));
    }
  }

  if (other.length > 0) {
    otherBlock.hidden = false;
    for (const project of other) {
      otherList.append(createCard(project));
    }
  }

  if (featured.length === 0 && other.length === 0) {
    showStatus("No projects were found.");
    return;
  }

  if (!options.keepStatus) {
    clearStatus();
  }
}

function createCard(project) {
  const article = document.createElement("article");
  article.className = "project-card";

  const title = document.createElement("h4");
  title.textContent = project.name;
  article.append(title);

  const metaItems = unique(
    [project.language, ...(project.tech || [])].filter(Boolean)
  );
  if (metaItems.length > 0) {
    const meta = document.createElement("p");
    meta.className = "project-meta";
    meta.textContent = metaItems.join(" · ");
    article.append(meta);
  }

  if (project.description) {
    const description = document.createElement("p");
    description.className = "project-desc";
    description.textContent = project.description;
    article.append(description);
  }

  const footer = document.createElement("div");
  footer.className = "project-footer";

  const link = document.createElement("a");
  link.href = project.url;
  link.target = "_blank";
  link.rel = "noopener noreferrer";
  link.textContent = "View project →";
  footer.append(link);

  article.append(footer);
  return article;
}
