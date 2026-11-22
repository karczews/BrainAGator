#!/usr/bin/env python3
"""Regenerate the repo-scoped OpenCode install of the Elephant-Goldfish Model.

Usage:
    git clone https://github.com/google/elephant-goldfish-model "$TMPDIR/egm-inspect"
    python3 .opencode/egm/install.py "$TMPDIR/egm-inspect"

Reads an upstream clone and rewrites .opencode/{skills,agents,commands,egm}.
Keeps .opencode/egm/{UPSTREAM.md,install.py,docs/opencode.md}.
"""

import os
import re
import shutil
import sys

REPO = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
SRC = sys.argv[1] if len(sys.argv) > 1 else os.path.join(os.environ.get("TMPDIR", "/tmp"), "egm-inspect")
OC = os.path.join(REPO, ".opencode")
EGM = os.path.join(OC, "egm")

# upstream skill directory -> OpenCode skill ID
SKILLS = {
    "egm_brainstorm": "egm-brainstorm",
    "egm_draft": "egm-draft",
    "egm_review": "egm-review",
    "egm_review_loop": "egm-review-loop",
    "egm_resolve": "egm-resolve",
    "egm_drift_report": "egm-drift-report",
    "egm_bootstrap": "egm-bootstrap",
}

# persona file -> (OpenCode agent ID, write access, shell access)
AGENTS = {
    "design_critic": ("egm-design", False, False),
    "comprehension": ("egm-comprehension", False, False),
    "zoom_out": ("egm-zoom-out", False, False),
    "structural": ("egm-structural", False, False),
    "style": ("egm-style", False, False),
    "implementation_readiness": ("egm-implementation-readiness", False, False),
    "codebase_details": ("egm-codebase-details", False, False),
    "synthesizer": ("egm-synthesizer", True, False),
    "bootstrap_orchestrator": ("egm-bootstrap-orchestrator", True, True),
}


def split_frontmatter(path):
    text = open(path, encoding="utf-8").read()
    if not text.startswith("---\n"):
        return {}, text
    end = text.index("\n---", 4)
    return text[4:end], text[end + 4:].lstrip("\n")


def frontmatter_value(frontmatter, key):
    """Read a scalar or folded (>) frontmatter value without a YAML dependency."""
    match = re.search(rf"^{key}:\s*(.*)$", frontmatter, re.MULTILINE)
    if not match:
        return ""
    value = match.group(1).strip()
    if value in (">", ">-", "|", "|-"):
        rest = frontmatter[match.end():].splitlines()
        parts = []
        for line in rest:
            if line.strip() and not line.startswith((" ", "\t")):
                break
            parts.append(line.strip())
        return " ".join(part for part in parts if part)
    return value


def relink(path, target):
    if os.path.islink(path) or os.path.exists(path):
        os.remove(path)
    os.symlink(target, path)


def install_skills():
    for upstream, skill_id in SKILLS.items():
        src_dir = os.path.join(SRC, "skills", upstream)
        dst_dir = os.path.join(OC, "skills", skill_id)
        os.makedirs(dst_dir, exist_ok=True)
        shutil.copy2(os.path.join(src_dir, "SKILL.md"), dst_dir)
        src_refs = os.path.join(src_dir, "references")
        if os.path.isdir(src_refs):
            dst_refs = os.path.join(dst_dir, "references")
            shutil.rmtree(dst_refs, ignore_errors=True)
            shutil.copytree(src_refs, dst_refs)
        relink(os.path.join(dst_dir, "agents"), "../../egm/agents")
        relink(os.path.join(dst_dir, "resources"), "../../egm/resources")
        relink(os.path.join(dst_dir, "docs"), "../../egm/docs")


def install_vendor_tree():
    shutil.copy2(os.path.join(SRC, "LICENSE"), os.path.join(EGM, "LICENSE"))
    for name in os.listdir(os.path.join(SRC, "agents")):
        shutil.copy2(os.path.join(SRC, "agents", name), os.path.join(EGM, "agents", name))
    for name in ("harnesses.md", "codex.md", "claude.md"):
        shutil.copy2(os.path.join(SRC, "docs", name), os.path.join(EGM, "docs", name))
    src_resources = os.path.join(SRC, "resources")
    for root, _dirs, files in os.walk(src_resources):
        for name in files:
            src = os.path.join(root, name)
            dst = os.path.join(EGM, "resources", os.path.relpath(src, src_resources))
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            shutil.copy2(src, dst)


def install_agents():
    for persona_file, (agent_id, can_write, can_shell) in AGENTS.items():
        frontmatter, _body = split_frontmatter(os.path.join(SRC, "agents", persona_file + ".md"))
        description = frontmatter_value(frontmatter, "description")
        permissions = []
        if not can_write:
            permissions.append('  - action: edit\n    resource: "*"\n    effect: deny')
        if not can_shell:
            permissions.append('  - action: shell\n    resource: "*"\n    effect: deny')
        content = f"""---
description: >-
  EGM Goldfish persona ({persona_file}.md): {description}
mode: subagent
permissions:
{chr(10).join(permissions)}
---

Read `.opencode/egm/agents/{persona_file}.md` before starting. Follow that
canonical persona's instructions exactly; this adapter only sets your OpenCode
tools and context isolation. Resolve bundled `agents/`, `resources/`, and
`docs/` paths against `.opencode/egm/`. Remain read-only and use only the task
and the review context you were given. Before returning findings, read the
target document and every standard you were assigned. If a required file cannot
be read, report an incomplete review instead of a verdict.
"""
        with open(os.path.join(OC, "agents", agent_id + ".md"), "w", encoding="utf-8") as handle:
            handle.write(content)


def install_commands():
    for upstream, skill_id in SKILLS.items():
        frontmatter, _body = split_frontmatter(os.path.join(SRC, "skills", upstream, "SKILL.md"))
        description = frontmatter_value(frontmatter, "description").replace("\n", " ")
        content = f"""---
description: {description}
---

Load the `{skill_id}` skill with the skill tool and follow it exactly for this
session. If the skill tool refuses it, read `.opencode/skills/{skill_id}/SKILL.md`
and follow it in this thread instead.

User input: $ARGUMENTS
"""
        with open(os.path.join(OC, "commands", skill_id + ".md"), "w", encoding="utf-8") as handle:
            handle.write(content)


def patch_skills_for_opencode():
    for skill_id in SKILLS.values():
        path = os.path.join(OC, "skills", skill_id, "SKILL.md")
        text = open(path, encoding="utf-8").read()
        text = text.replace(
            "In Claude Code or Cowork, read `${CLAUDE_PLUGIN_ROOT}/docs/claude.md` first.\n",
            "In Claude Code or Cowork, read `${CLAUDE_PLUGIN_ROOT}/docs/claude.md` first.\n"
            "In OpenCode, read `docs/opencode.md` first.\n",
        )
        text = text.replace("Codex: `spawn_agent`)", "Codex: `spawn_agent`; OpenCode: `subagent`)")
        with open(path, "w", encoding="utf-8") as handle:
            handle.write(text)


def main():
    if not os.path.isdir(SRC):
        sys.exit(f"upstream clone not found: {SRC}")
    for sub in ("agents", "commands", "skills", "egm/agents", "egm/resources", "egm/docs"):
        os.makedirs(os.path.join(OC, sub), exist_ok=True)
    install_vendor_tree()
    install_skills()
    install_agents()
    install_commands()
    patch_skills_for_opencode()
    commit = os.popen(f"git -C {SRC} rev-parse --short HEAD").read().strip()
    print(f"installed EGM from {SRC} at {commit}")


if __name__ == "__main__":
    sys.exit(main())
