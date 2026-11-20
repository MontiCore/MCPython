# Reference: https://docs.python.org/3.15/reference/compound_stmts.html#the-async-for-statement
# Grammar:   else_block
# PEP:       492
# Since:     Python 3.5
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
async def f():
    async for x in xs:
        pass
    else:
        pass
