# Python grammar coverage samples

Small Python files, **one language feature per file**, used to measure how much of Python the MCPython grammar parses.

**Not all of Python is supported yet.** Failing files are expected: they show what is still missing.
That's why these tests do not run in the normal `test` task and never fail the build.

## Running

| Command | What it does |
|---|---|
| `gradlew pythonCoverage` | Parses every file here (one JUnit test per file). Failures are recorded, not fatal. Report: `build/reports/pythonCoverage/index.html` |
| `gradlew compareCoverage` | Collects the samples of the current branch **and** of every other remote branch (a sample added on a parallel branch is run too), and runs them against the grammar of every branch. Prints the coverage per branch and a table of the samples whose result differs between branches (🆕 marks samples that don't exist on a branch). Fails if a sample that parses on `dev` or on the parent branch no longer parses. Deleting a sample doesn't hide this: samples of `dev` and the parent are always run. It also fails if a parser on the current branch hangs and doesn't stop after its timeout: the following samples of that run are then skipped ("not run"). On other branches, "not run" samples count as unknown (neither parsed nor lost). |
| `gradlew compareCoverage -PcoverageParent=foo` | The same, with `foo` as the parent branch. Without it, the parent is `dev`. Git history can't tell whether a branch was forked from another one or the other way round, so the parent is never guessed. |
| `gradlew compareCoverage -PcoverageBranches=dev,foo` | The same, but uses only the listed branches (plus the parent). |
| `gradlew checkPythonSamples` | Checks that every sample of the current branch is valid Python (`python -m compileall` in the Docker image `python:3.15-slim`). Needs a running Docker. Fails if a sample is not valid Python. |

A file counts as supported if it parses without errors and an AST is returned (no symbol table, no CoCos, no pretty printing).

**CI:** the `python-coverage` job in `.github/workflows/test_branch.yml` runs `compareCoverage` on every push (except to `dev`)
and adds the report to the job summary. The parent branch is the base branch of the open pull request (`dev` without one). It can also be started by hand with "Run workflow" in the Actions tab.
The workflow `.github/workflows/check_python_samples.yml` runs `checkPythonSamples` on every push that changes samples (except to `dev`), and can be started by hand too.
The results of the other branches are cached (in `build/coverage-base-cache/`, one entry per branch commit) until that branch, the samples or the test change.

## Layout

The folders follow the [Python Language Reference](https://docs.python.org/3.15/reference/):
the chapter, then the section, named after the section titles (not the numbers, which shift between versions).

| Folder | Reference chapter |
|---|---|
| `lexical/` | [2. Lexical analysis](https://docs.python.org/3.15/reference/lexical_analysis.html) |
| `expressions/` | [6. Expressions](https://docs.python.org/3.15/reference/expressions.html) |
| `simple_statements/` | [7. Simple statements](https://docs.python.org/3.15/reference/simple_stmts.html) |
| `compound_statements/` | [8. Compound statements](https://docs.python.org/3.15/reference/compound_stmts.html) |

Example: `compound_statements/try/except_star.py`. A feature goes where the current reference describes it.

## File format

Every file starts with a header, followed by as little code as possible (usually one line; `pass` as the body where a block is needed).
Undefined names like `f()` or `xs` are fine, only the syntax matters.

```python
# Reference: https://docs.python.org/3.15/reference/compound_stmts.html#except-star
# Grammar:   except_star_block
# PEP:       654
# Since:     Python 3.11
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-19
try:
    f()
except* ValueError:
    pass
```

| Line | Required | Content |
|---|---|---|
| `Reference` | always | Link to the section, pinned to the Python version the file was written against |
| `Grammar` | always | Rule name in CPython's grammar, [`Grammar/python.gram`](https://github.com/python/cpython/blob/v3.15.2/Grammar/python.gram). Lexical features that `python.gram` only sees as tokens use the lexical definition from the reference (e.g. `hexinteger`, `stringprefix`), or, where the reference has none, the token name from Python's [`token`](https://docs.python.org/3.15/library/token.html) module (e.g. `INDENT`, `COMMENT`, `ENCODING`) |
| `PEP` | if one exists | Number of the PEP that introduced the feature |
| `Since` | always | First Python version with this syntax (from the reference's "Added in version" notes or the [What's New](https://docs.python.org/3.15/whatsnew/) pages); `original syntax (before Python 2.0)` for older features |
| `Generated` | if AI-generated | The AI model that wrote the file (name and model ID) and the date. Leave it out for files written by hand |

To find the tests for a definition, search this folder for the anchor (e.g. `#except-star`) or the grammar rule name.

## Adding a file

1. Find the section in the [reference](https://docs.python.org/3.15/reference/) and create the file in the matching folder.
2. Write the header and check that the link and the grammar rule exist.
3. Check that the file is valid Python with a Python version that supports the feature, without writing `__pycache__` files into this folder:
   `python -c "import sys; compile(open(sys.argv[1], 'rb').read(), sys.argv[1], 'exec')" <file>`
   (prints nothing if the file is valid; unlike `ast.parse` it also finds errors such as `return` outside a function).
   Or check all samples at once with `gradlew checkPythonSamples` (needs Docker).
4. Supported range: Python 3.10 up to the newest stable version.
