# Reference: https://docs.python.org/3.15/reference/simple_stmts.html#the-nonlocal-statement
# Grammar:   nonlocal_stmt
# PEP:       3104
# Since:     Python 3.0
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
def f():
    x = 0
    def g():
        nonlocal x
        x += 1
