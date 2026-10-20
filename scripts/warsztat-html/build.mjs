// Generator stron HTML wszystkich materiałów szkolenia z plików md:
//   md/index.md                         -> html/index.html
//   md/{zadania,przyklady,rozwiazania}  -> html/{zadania,przyklady,rozwiazania}
//   md/warsztat, warsztat-csharp, warsztat-typescript -> html/warsztat* (przełącznik języka)
//   md/slides                           -> html/slajdy (Marp CLI)
// Styl (CSS) jest wspólny dla wszystkich stron: style.css obok tego pliku.
//
// Użycie (z katalogu głównego repozytorium):
//   cd scripts/warsztat-html && npm ci && npm run build
// albo: scripts/warsztat.sh html
import fs from 'node:fs';
import path from 'node:path';
import { execFileSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';
import MarkdownIt from 'markdown-it';
import anchor from 'markdown-it-anchor';
import hljs from 'highlight.js';

const HERE = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.resolve(HERE, '../../src/main/resources');
const VARIANTS = [
    { dir: 'warsztat', label: 'Java' },
    { dir: 'warsztat-csharp', label: 'C#' },
    { dir: 'warsztat-typescript', label: 'TypeScript' },
];

// Moduły szkolenia (kolejność = kolejność zajęć) i moduły, które mają sceny warsztatu.
const MODULES = [
    '01-wprowadzenie',
    '02-prewencja',
    '03-testowanie-a-bezpieczenstwo',
    '04-wzorce-projektowe',
    '05-refaktoryzacja-do-wzorcow',
    '06-podsumowanie',
];
const WORKSHOP_MODULES = [
    '02-prewencja',
    '03-testowanie-a-bezpieczenstwo',
    '05-refaktoryzacja-do-wzorcow',
];
const KINDS = [
    { dir: 'zadania', label: 'Zadania', prefix: 'Zadania: ' },
    { dir: 'przyklady', label: 'Przykłady', prefix: 'Przykłady: ' },
    { dir: 'rozwiazania', label: 'Rozwiązania', prefix: 'Rozwiązania: ' },
];

const css = fs.readFileSync(path.join(HERE, 'style.css'), 'utf8')
    + '.langs{margin-left:auto}.langs+.pager{margin-left:0}';

const slugify = (s) => s.toLowerCase()
    .normalize('NFD').replace(/[̀-ͯ]/g, '')
    .replace(/ł/g, 'l')
    .replace(/[^\w\s-]/g, '').trim().replace(/\s+/g, '-').replace(/-+/g, '-');

const escapeHtml = (s) => s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;').replace(/'/g, '&#x27;');

const md = new MarkdownIt({
    html: false,
    linkify: false,
    typographer: false,
    highlight(code, lang) {
        const language = lang && hljs.getLanguage(lang) ? lang : null;
        const body = language
            ? hljs.highlight(code, { language, ignoreIllegals: true }).value
            : escapeHtml(code);
        return `<pre class="code" data-lang="${escapeHtml(lang || 'text')}"><code class="hljs">${body}</code></pre>`;
    },
}).use(anchor, {
    slugify,
    level: [2, 3, 4],
    tabIndex: -1,
    permalink: anchor.permalink.headerLink({ safariReaderFix: true }),
});

md.renderer.rules.table_open = () => '<div class="table-wrap"><table>\n';
md.renderer.rules.table_close = () => '</table></div>\n';
md.renderer.rules.code_inline = (tokens, idx) =>
    `<code>${escapeHtml(tokens[idx].content).replace(/\//g, '/<wbr>')}</code>`;

function toc(tokens) {
    const items = [];
    for (let i = 0; i < tokens.length; i++) {
        const t = tokens[i];
        if (t.type === 'heading_open' && (t.tag === 'h2' || t.tag === 'h3')) {
            const id = t.attrGet('id');
            const text = tokens[i + 1].children.filter((c) => c.type === 'text' || c.type === 'code_inline')
                .map((c) => c.content).join('');
            items.push(`<li class="${t.tag}"><a href="#${id}">${escapeHtml(text)}</a></li>`);
        }
    }
    return items.length
        ? `<aside><nav class="toc" aria-label="Spis treści"><details open><summary class="toc-title">Na tej stronie</summary><ul>${items.join('')}</ul></details></nav></aside>`
        : '';
}

function pager(modules, module) {
    const i = modules.indexOf(module);
    const prev = modules[i - 1];
    const next = modules[i + 1];
    return '<div class="pager">'
        + (prev ? `<a href="${prev}.html" title="Poprzedni moduł">&larr; ${prev.slice(0, 2)}</a>` : '')
        + (next ? `<a href="${next}.html" title="Następny moduł">${next.slice(0, 2)} &rarr;</a>` : '')
        + '</div>';
}

const exists = (...parts) => fs.existsSync(path.join(ROOT, ...parts));

// Pasek dla stron modułu: Slajdy | Zadania | Przykłady | Rozwiązania (tylko istniejące) + pager.
function moduleTopbar(kind, module) {
    const tabs = [];
    if (exists('md', 'slides', `${module}-slajdy.md`)) {
        tabs.push(`<a class="tab" href="../slajdy/${module}-slajdy.html">Slajdy</a>`);
    }
    for (const k of KINDS) {
        if (!exists('md', k.dir, `${module}.md`)) continue;
        const href = k.dir === kind ? `${module}.html` : `../${k.dir}/${module}.html`;
        tabs.push(`<a class="tab${k.dir === kind ? ' active' : ''}" href="${href}">${k.label}</a>`);
    }
    return `<header class="topbar"><a class="home" href="../index.html">Materiały prowadzącego</a>`
        + `<div class="tabs">${tabs.join('')}</div>${pager(MODULES.filter((m) => exists('md', kind, `${m}.md`)), module)}</header>`;
}

function workshopTopbar(kind, module, variant) {
    const up = kind === 'intro' ? '..' : '../..';
    const file = kind === 'intro' ? '00-cinelegacy.html' : `${kind}/${module}.html`;
    const langs = '<div class="tabs langs" aria-label="Język">' + VARIANTS.map((v) =>
        `<a class="tab${v === variant ? ' active' : ''}" href="${up}/${v.dir}/${file}">${v.label}</a>`).join('')
        + '</div>';
    const intro = kind === 'intro' ? '00-cinelegacy.html' : '../00-cinelegacy.html';
    const link = (k, label) => {
        const href = kind === 'intro' ? `${k}/${module ?? WORKSHOP_MODULES[0]}.html`
            : (k === kind ? `${module}.html` : `../${k}/${module}.html`);
        return `<a class="tab${k === kind ? ' active' : ''}" href="${href}">${label}</a>`;
    };
    return `<header class="topbar"><a class="home" href="${up}/index.html">Materiały prowadzącego</a>`
        + `<div class="tabs"><a class="tab${kind === 'intro' ? ' active' : ''}" href="${intro}">Warsztat CineLegacy</a>`
        + link('zadania', 'Zadania') + link('przewodnik', 'Przewodnik') + '</div>'
        + langs + (module ? pager(WORKSHOP_MODULES, module) : '') + '</header>';
}

function render({ source, target, title, bodyClass, topbar }) {
    const text = fs.readFileSync(source, 'utf8');
    if (/[–—]/.test(text)) {
        throw new Error(`długi myślnik w ${source}`);
    }
    const env = {};
    const tokens = md.parse(text, env);
    const h1 = text.match(/^# (.+)$/m)?.[1] ?? '';
    const nav = bodyClass === 'index' ? '' : toc(tokens);
    const body = md.renderer.render(tokens, md.options, env);
    const html = `<!doctype html>
<html lang="pl">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${escapeHtml(title(h1))}</title>
<style>${css}</style>
</head>
<body class="${bodyClass}">
${topbar}
<div class="layout${nav ? '' : ' no-toc'}">
${nav}
<main>
${body}
</main>
</div>
<script>if(matchMedia('(max-width:900px)').matches){var d=document.querySelector('.toc details');if(d)d.open=false}</script>
</body>
</html>
`;
    fs.mkdirSync(path.dirname(target), { recursive: true });
    fs.writeFileSync(target, html);
    console.log('ok', path.relative(ROOT, target));
}

const missing = (source) => {
    if (fs.existsSync(source)) return false;
    console.warn('BRAK', path.relative(ROOT, source));
    return true;
};

// Strona główna
render({
    source: path.join(ROOT, 'md', 'index.md'),
    target: path.join(ROOT, 'html', 'index.html'),
    title: (h1) => h1,
    bodyClass: 'index',
    topbar: '<header class="topbar"><a class="home" href="index.html">Materiały prowadzącego</a></header>',
});

// Zadania, przykłady, rozwiązania
for (const kind of KINDS) {
    for (const module of MODULES) {
        const source = path.join(ROOT, 'md', kind.dir, `${module}.md`);
        if (!fs.existsSync(source)) continue;
        render({
            source,
            target: path.join(ROOT, 'html', kind.dir, `${module}.html`),
            title: (h1) => kind.prefix + h1,
            bodyClass: kind.dir,
            topbar: moduleTopbar(kind.dir, module),
        });
    }
}

// Warsztat CineLegacy w trzech wersjach językowych
for (const variant of VARIANTS) {
    const MD = path.join(ROOT, 'md', variant.dir);
    const HTML = path.join(ROOT, 'html', variant.dir);
    const suffix = variant.dir === 'warsztat' ? '' : ` (${variant.label})`;
    if (missing(MD)) continue;
    render({
        source: path.join(MD, '00-cinelegacy.md'),
        target: path.join(HTML, '00-cinelegacy.html'),
        title: (h1) => (h1 || 'Warsztat CineLegacy') + suffix,
        bodyClass: 'warsztat',
        topbar: workshopTopbar('intro', undefined, variant),
    });
    for (const kind of ['przewodnik', 'zadania']) {
        for (const module of WORKSHOP_MODULES) {
            const source = path.join(MD, kind, `${module}.md`);
            if (missing(source)) continue;
            render({
                source,
                target: path.join(HTML, kind, `${module}.html`),
                title: (h1) => (kind === 'przewodnik' ? 'Przewodnik: ' : 'Zadania: ') + h1 + suffix,
                bodyClass: kind === 'zadania' ? 'warsztat-zadania' : 'warsztat',
                topbar: workshopTopbar(kind, module, variant),
            });
        }
    }
}

// Slajdy (Marp CLI, szablon bespoke)
const marp = path.join(HERE, 'node_modules', '.bin', 'marp');
for (const module of MODULES) {
    const source = path.join(ROOT, 'md', 'slides', `${module}-slajdy.md`);
    if (missing(source)) continue;
    if (/[–—]/.test(fs.readFileSync(source, 'utf8'))) {
        throw new Error(`długi myślnik w ${source}`);
    }
    const target = path.join(ROOT, 'html', 'slajdy', `${module}-slajdy.html`);
    fs.mkdirSync(path.dirname(target), { recursive: true });
    execFileSync(marp, [source, '-o', target], { stdio: 'inherit' });
    console.log('ok', path.relative(ROOT, target));
}
