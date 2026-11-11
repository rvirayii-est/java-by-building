# Website integration notes

The course is built so a separate website can link to or embed lessons **without changing this repo's layout**.

## What is already integration-ready
- **One lesson = one folder = one `README.md`** with YAML front matter (`title`, `slug`, `week`, `hours`,
  `level`, `milestone`, `objectives`). A site can read this as a content-collection entry.
- **Stable slugs** (`01-hello-java`): never renamed once published; use them as URL paths.
- **Relative links only** inside lessons, so rendering from a copy or a submodule still works.
- **No site-specific syntax**: plain GitHub-flavoured Markdown, fenced code blocks with language tags.
- **Raw URLs** work: `https://raw.githubusercontent.com/rvirayii-est/java-by-building/main/lessons/<slug>/README.md`.

## Options (pick later)
1. **Link out** to the GitHub folder — zero effort, always current.
2. **Fetch at build time** — the site's build downloads `lessons/*/README.md` (or uses a git submodule) and
   renders it with its own Markdown pipeline. Recommended.
3. **Copy on release** — copy lessons into the site on tagged releases for stable versions.

## Suggested follow-ups (not done yet)
- Generate `lessons/index.json` from front matter in CI so the site never hand-maintains a list.
- Add a `LICENSE` note on the site stating the course is MIT-licensed with attribution.
- Update clone URLs in `docs/SETUP.md` if the repo moves.
