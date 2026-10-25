# Vendored upstream: Elephant-Goldfish Model

| Field | Value |
| --- | --- |
| Source | https://github.com/google/elephant-goldfish-model |
| Commit | `3d0b34821ce88ad328b0c9bf0e06d7eb5f1cd669` |
| Commit date | 2026-10-23 |
| Plugin version | 0.1.0 |
| License | Apache-2.0 (`LICENSE` in this directory) |
| Installed | 2026-10-25, project-scope OpenCode install (tracked in `.opencode/`) |

EGM is a non-official Google project. Upstream supports Antigravity, Claude
Code, Claude Cowork, Codex, and ChatGPT; OpenCode support here is a local
adapter (see `docs/opencode.md`).

## What is vendored

| Path | Upstream source |
| --- | --- |
| `agents/` | `agents/` (all nine persona files, unmodified) |
| `resources/` | `resources/` (execution instructions and document standards) |
| `docs/harnesses.md`, `docs/codex.md`, `docs/claude.md` | `docs/` |
| `docs/opencode.md` | Written for this install, not upstream |
| `../skills/` | `skills/<egm_*>/SKILL.md`, `skills/*/references/` |
| `../agents/`, `../commands/` | Generated adapters, not upstream |

Excluded because they are not used at runtime: `tests/`, `site/`,
`docs/designs/`, `docs/faq.md`, `docs/getting_started.md`, `docs/index.md`,
skill `TEST.md` and `test_scenarios.yaml` files, `pyproject.toml`,
`requirements-dev.txt`, and the per-harness manifests.

## Local changes to upstream text

`install.py` applies two edits to every vendored `SKILL.md` so the delegation
and documentation branches mention OpenCode:

* after the Claude/Codex preamble line, insert
  `In OpenCode, read `docs/opencode.md` first.`
* extend the subagent tool list with `OpenCode: `subagent``.

Nothing else in the skill bodies is modified.

## Updating

```sh
git clone https://github.com/google/elephant-goldfish-model "$TMPDIR/egm-inspect"
git -C "$TMPDIR/egm-inspect" pull --ff-only
python3 .opencode/egm/install.py "$TMPDIR/egm-inspect"
```

Then update the commit, date, and version rows above. Re-read
`docs/harnesses.md` for new personas or skills that need adapters; `install.py`
holds the persona and skill maps.
