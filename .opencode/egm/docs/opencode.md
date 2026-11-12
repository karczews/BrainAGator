# OpenCode integration

Project-scope install of the Elephant-Goldfish Model (EGM) for OpenCode V2.
Files live in `.opencode/` and are committed to this repository: `.gitignore`
whitelists `.opencode/{agents,commands,skills,egm}` while ignoring the rest of
`.opencode/` (sessions, local scratch).

## Install layout

```text
.opencode/
├── commands/egm-*.md        # /egm-brainstorm, /egm-draft, /egm-review, ...
├── agents/egm-*.md          # OpenCode subagent adapters (mode: subagent)
├── skills/egm-*/SKILL.md    # upstream skill text, IDs in kebab-case
│   ├── agents      -> ../../egm/agents       # bundled personas
│   ├── resources   -> ../../egm/resources    # document standards
│   ├── docs        -> ../../egm/docs          # harness docs
│   └── references/                            # skill-local references
└── egm/                     # vendored upstream plugin content
    ├── agents/  resources/  docs/  LICENSE
    └── install.py           # regenerates this install from an upstream clone
```

## Invocation

* Slash commands: `/egm-brainstorm`, `/egm-draft`, `/egm-review`,
  `/egm-review-loop`, `/egm-resolve`, `/egm-drift-report`, `/egm-bootstrap`.
  Arguments are passed through, for example `/egm-review docs/designs/foo.md`.
* Explicit skill mention: `@egm-review`.
* Plain request: "run the egm-review skill on docs/designs/foo.md".

Upstream sets `disable-model-invocation: true` on every skill, so OpenCode does
not advertise them for automatic selection. They stay registered and load by
exact ID; each command tells the agent to load the skill and falls back to
reading the `SKILL.md` in the thread if the skill tool refuses it.

## Path resolution

Skills address bundled files with plugin-root paths (`agents/<file>.md`,
`resources/document_standards/...`) and, for the Codex and Claude branches,
`../../docs/...`. The `agents`, `resources`, and `docs` symlinks inside each
skill directory make those references resolve from the skill's own base
directory, so nothing depends on the plugin cache layout the other harnesses
use. Treat `.opencode/egm/` as the plugin root.

## Delegation

OpenCode registers the personas as named subagents, so `spawn a subagent` maps
to the `subagent` tool with an `egm-*` agent ID:

| Persona file | OpenCode subagent | Write | Shell |
| --- | --- | --- | --- |
| `design_critic.md` | `egm-design` | no | no |
| `comprehension.md` | `egm-comprehension` | no | no |
| `zoom_out.md` | `egm-zoom-out` | no | no |
| `structural.md` | `egm-structural` | no | no |
| `style.md` | `egm-style` | no | no |
| `implementation_readiness.md` | `egm-implementation-readiness` | no | no |
| `codebase_details.md` | `egm-codebase-details` | no | no |
| `synthesizer.md` | `egm-synthesizer` | yes | no |
| `bootstrap_orchestrator.md` | `egm-bootstrap-orchestrator` | yes | yes |

Adapters mirror the Claude ones: read-only reviewers deny `edit` and `shell`;
the synthesizer may write reports; the bootstrap orchestrator keeps shell access
because the bootstrap skill scans the tree. Each adapter reads its canonical
persona from `.opencode/egm/agents/` and inherits the parent session model.

Each reviewer must run in a fresh subagent context. Do not review in the main
thread: a review in the same context is not a Goldfish review.

## Artifacts

Briefs, drafts, and reports are written to the working directory. Add a line
`EGM artifact directory: <path>` to `AGENTS.md` (project or user level) to move
them elsewhere; a relative path resolves against the working directory.

## Differences from the plugin harnesses

* No hooks: OpenCode reviewers read `.opencode/egm/` inside the workspace, so no
  external-path approval hook is needed.
* No marketplace, plugin cache, or `${CLAUDE_PLUGIN_ROOT}` substitution: use
  `.opencode/egm/` as the root.
* `docs/codex.md` and `docs/claude.md` are vendored for reference only; the
  OpenCode path is this document.

## Updating

See `.opencode/egm/UPSTREAM.md`.
