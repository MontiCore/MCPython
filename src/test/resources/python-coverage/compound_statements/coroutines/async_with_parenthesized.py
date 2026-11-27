# Reference: https://docs.python.org/3.15/reference/compound_stmts.html#the-async-with-statement
# Grammar:   with_stmt
# Since:     Python 3.10
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
async def f():
    async with (
        a as x,
        b as y,
    ):
        pass
