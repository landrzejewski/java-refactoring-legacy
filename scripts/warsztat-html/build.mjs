// Generator stron HTML warsztatu CineLegacy z plików md/warsztat (Java), md/warsztat-csharp
// i md/warsztat-typescript. Styl (CSS) jest kopiowany z istniejącej strony "przyklady",
// więc wygląd jest identyczny. Każda strona ma przełącznik języka (ta sama strona w innej wersji).
//
// Użycie (z katalogu głównego repozytorium):
//   cd scripts/warsztat-html && npm ci && npm run build
// albo: scripts/warsztat.sh html
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import MarkdownIt from 'markdown-it';
import anchor from 'markdown-it-anchor';
import hljs from 'highlight.js';

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../../src/main/resources');
const VARIANTS = [
    { dir: 'warsztat', label: 'Java' },
    { dir: 'warsztat-csharp', label: 'C#' },
    { dir: 'warsztat-typescript', label: 'TypeScript' },
];
const TEMPLATE = path.join(ROOT, 'html/przyklady/04-podstawowe-refaktoryzacje-warsztat-praktyczny.html');

const css = fs.readFileSync(TEMPLATE, 'utf8').match(/<style>([\s\S]*?)<\/style>/)[1]
    .replace('body.zadania{--kind:#1a7f37}',
        'body.zadania{--kind:#1a7f37} body.warsztat{--kind:#c2570c} body.warsztat-zadania{--kind:#0f7b7b}')
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

const MODULES = [
    '03-zasady-dobrego-projektowania',
    '04-podstawowe-refaktoryzacje',
    '05-refaktoryzacje-hierarchii-klas',
    '06-refaktoryzacje-do-wzorcow-projektowych',
    '07-zaawansowane-refaktoryzacje',
    '08-strategie-i-dobre-praktyki',
];

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

function topbar(kind, module, variant) {
    const up = kind === 'intro' ? '..' : '../..';
    const file = kind === 'intro' ? '00-cinelegacy.html' : `${kind}/${module}.html`;
    const langs = '<div class="tabs langs" aria-label="Język">' + VARIANTS.map((v) =>
        `<a class="tab${v === variant ? ' active' : ''}" href="${up}/${v.dir}/${file}">${v.label}</a>`).join('')
        + '</div>';
    const intro = kind === 'intro' ? '00-cinelegacy.html' : '../00-cinelegacy.html';
    const link = (k, label) => {
        const href = kind === 'intro' ? `${k}/${module ?? MODULES[0]}.html`
            : (k === kind ? `${module}.html` : `../${k}/${module}.html`);
        return `<a class="tab${k === kind ? ' active' : ''}" href="${href}">${label}</a>`;
    };
    let pager = '';
    if (module) {
        const i = MODULES.indexOf(module);
        const prev = MODULES[i - 1];
        const next = MODULES[i + 1];
        pager = '<div class="pager">'
            + (prev ? `<a href="${prev}.html" title="Poprzedni moduł">&larr; ${prev.slice(0, 2)}</a>` : '')
            + (next ? `<a href="${next}.html" title="Następny moduł">${next.slice(0, 2)} &rarr;</a>` : '')
            + '</div>';
    }
    return `<header class="topbar"><a class="home" href="${up}/index.html">Materiały prowadzącego</a>`
        + `<div class="tabs"><a class="tab${kind === 'intro' ? ' active' : ''}" href="${intro}">Warsztat CineLegacy</a>`
        + link('zadania', 'Zadania') + link('przewodnik', 'Przewodnik') + '</div>'
        + langs + pager + '</header>';
}

function page({ source, target, kind, module, titlePrefix, variant }) {
    const text = fs.readFileSync(source, 'utf8');
    if (/[–—]/.test(text)) {
        throw new Error(`długi myślnik w ${source}`);
    }
    const env = {};
    const tokens = md.parse(text, env);
    const h1 = text.match(/^# (.+)$/m)?.[1] ?? 'Warsztat CineLegacy';
    const titleSuffix = variant.dir === 'warsztat' ? '' : ` (${variant.label})`;
    const nav = toc(tokens);
    const body = md.renderer.render(tokens, md.options, env);
    const bodyClass = kind === 'zadania' ? 'warsztat-zadania' : 'warsztat';
    const html = `<!doctype html>
<html lang="pl">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${escapeHtml(titlePrefix + h1 + titleSuffix)}</title>
<style>${css}</style>
</head>
<body class="${bodyClass}">
${topbar(kind, module, variant)}
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

for (const variant of VARIANTS) {
    const MD = path.join(ROOT, 'md', variant.dir);
    const HTML = path.join(ROOT, 'html', variant.dir);
    if (!fs.existsSync(MD)) {
        console.warn('BRAK', path.relative(ROOT, MD));
        continue;
    }
    page({
        source: path.join(MD, '00-cinelegacy.md'),
        target: path.join(HTML, '00-cinelegacy.html'),
        kind: 'intro',
        titlePrefix: '',
        variant,
    });
    for (const kind of ['przewodnik', 'zadania']) {
        for (const module of MODULES) {
            const source = path.join(MD, kind, `${module}.md`);
            if (!fs.existsSync(source)) {
                console.warn('BRAK', path.relative(ROOT, source));
                continue;
            }
            page({
                source,
                target: path.join(HTML, kind, `${module}.html`),
                kind,
                module,
                titlePrefix: kind === 'przewodnik' ? 'Przewodnik: ' : 'Zadania: ',
                variant,
            });
        }
    }
}
