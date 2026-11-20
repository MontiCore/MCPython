# Reference: https://docs.python.org/3.15/reference/expressions.html#asynchronous-comprehensions
# Grammar:   listcomp
# PEP:       530
# Since:     Python 3.6
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
async def f():
    return [await fun() for fun in funcs if await condition()]
