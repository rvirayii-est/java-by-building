# Website integration notes

The course is built so a separate website can link to or embed lessons **without changing this repo's layout**.
Nothing in this repository depends on any website, and nothing here changes a website: this page only documents the contract.

## What is integration-ready
- **One lesson = one folder = one `README.md`** with YAML front matter (`title`, `slug`, `week`, `hours`,
  `level`, `milestone`, `objectives`). A site can read this as a content-collection entry.
- **`lessons/index.json`**: the same front matter for all lessons in one file, generated and checked by CI (below). A site never
  hand-maintains a list of lessons.
- **Stable slugs** (`01-hello-java`): never renamed once published; use them as URL paths.
- **Relative links only** inside lessons, so rendering from a copy or a submodule still works.
- **No site-specific syntax**: plain GitHub-flavoured Markdown, fenced code blocks with language tags.
- **Raw URLs** work: `https://raw.githubusercontent.com/rvirayii-est/java-by-building/main/lessons/<slug>/README.md`.

## The `lessons/index.json` contract

**Where:** `lessons/index.json` on the `main` branch.
`https://raw.githubusercontent.com/rvirayii-est/java-by-building/main/lessons/index.json`

**How it is made:** `java scripts/LessonTool.java index` (JDK 21 only, no dependencies; same on Windows and Linux) reads the front
matter of every `lessons/NN-slug/README.md`. CI runs `java scripts/LessonTool.java check` (front matter and the eight numbered
sections are present) and `java scripts/LessonTool.java index --check` (the committed file equals what the tool would write), so
the file is never stale on `main`. The file has no timestamps: the same lessons always give the same bytes.

**Shape (schema 1):**
```json
{
  "schema": 1,
  "repository": "rvirayii-est/java-by-building",
  "branch": "main",
  "count": 48,
  "lessons": [
    {
      "number": 41,
      "slug": "41-auth-basics",
      "title": "41 — Authentication basics: who is calling?",
      "week": 11,
      "hours": 5,
      "level": "intermediate",
      "milestone": "none (secures the M6 API; M7 is the end of lesson 48)",
      "objectives": ["Explain the difference between authentication ...", "..."],
      "path": "lessons/41-auth-basics",
      "readme": "lessons/41-auth-basics/README.md",
      "rawUrl": "https://raw.githubusercontent.com/rvirayii-est/java-by-building/main/lessons/41-auth-basics/README.md",
      "url": "https://github.com/rvirayii-est/java-by-building/tree/main/lessons/41-auth-basics"
    }
  ]
}
```

| Field | Type | Meaning |
|---|---|---|
| `schema` | integer | Version of this shape. Changes only when a field is removed or changes meaning; adding a field does not change it. Check it before reading |
| `repository`, `branch` | string | Where the URLs below point |
| `count` | integer | Number of entries in `lessons` |
| `number` | integer | The lesson number, from the folder name (`41`) |
| `slug` | string | Folder name; **stable**; use it as the URL path on your site |
| `title` | string | Display title, starts with the number |
| `week` | integer | Course week, 1 to 12 |
| `hours` | integer | Estimated hours (guided + lab) |
| `level` | string | `beginner`, `intermediate` or `advanced` |
| `milestone` | string | Which app milestone this lesson belongs to, or a note; display text only, do not parse it |
| `objectives` | string[] | Learning objectives, plain text with some Markdown (backticks) |
| `path` | string | Folder relative to the repository root |
| `readme` | string | The lesson file relative to the repository root |
| `rawUrl` | string | Direct URL of the Markdown (for fetching at build time) |
| `url` | string | The folder on GitHub (for a "view source" link) |

Entries are sorted by `number`. Order of fields and entries is stable. Treat unknown fields as ignorable.

### Example: fetch at build time (documentation only, not part of this repo)
A static-site build can fetch the index and then each lesson. This is an example to adapt, written for Node 18+ (global `fetch`) in an
ES module; it is **not** executed by this repository's CI (its syntax was checked with Node 24; it needs the published `index.json` to run).
```js
// build-lessons.mjs : run during the site build, e.g. "node build-lessons.mjs"
const INDEX = "https://raw.githubusercontent.com/rvirayii-est/java-by-building/main/lessons/index.json";

const index = await (await fetch(INDEX)).json();
if (index.schema !== 1) throw new Error(`Unknown index schema ${index.schema}: update the site`);

const lessons = [];
for (const lesson of index.lessons) {
  const response = await fetch(lesson.rawUrl);
  if (!response.ok) throw new Error(`${lesson.slug}: HTTP ${response.status}`);
  const markdown = await response.text();                       // the whole README, including the front matter block
  const body = markdown.replace(/^---\n[\s\S]*?\n---\n/, "");   // drop the front matter: the index already has it
  lessons.push({ ...lesson, body });
}

// Group by week for a course page; the URL of a lesson page is /course/<slug>/
const weeks = {};
for (const l of lessons) (weeks[l.week] ??= []).push(l);
console.log(`Fetched ${lessons.length} lessons in ${Object.keys(weeks).length} weeks`);
// ...write each lesson to your content folder, render the Markdown with your own pipeline...
```
Notes for the site:
- Fetch **at build time**, not in the visitor's browser (fewer requests, no rate limits, works offline after the build).
  `raw.githubusercontent.com` is cached for a few minutes, so a push may take a few minutes to show.
- To pin the site to a release instead of `main`, replace `main` in the URLs by a tag; the index records the branch it was built for.
- Links inside lessons are relative (`../40-testing-with-mockmvc/README.md`). Rewrite them to your own routes, or to
  `https://github.com/rvirayii-est/java-by-building/blob/main/...`, when rendering.
- Code in lessons lives in `lessons/<slug>/{examples,starter,exercises,solution}`; link to the folder with `url`.
- Licence: the course is MIT-licensed; show the licence and attribution on the site (see `LICENSE`).

## Options (pick later)
1. **Link out** to the GitHub folder (`url`): zero effort, always current.
2. **Fetch at build time** with `index.json` and the READMEs, as above. Recommended.
3. **Git submodule** or a copy on release: stable versions, more maintenance.

## Compatibility promise
- Slugs and folder names are never renamed after publication.
- `index.json` keeps `schema: 1` fields as documented; new fields may be added.
- Front matter keys are the seven listed in [CONTRIBUTING.md](../CONTRIBUTING.md); the tool fails CI when one is missing.

## Still open (not done here)
- A `LICENSE` note on the site stating the course is MIT-licensed with attribution.
- Update clone URLs in `docs/SETUP.md` if the repository moves (the owner and name are constants at the top of `scripts/LessonTool.java`).
