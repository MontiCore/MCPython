# Reference: https://docs.python.org/3.15/reference/expressions.html#string-literal-concatenation
# Grammar:   strings
# Since:     original syntax (before Python 2.0)
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
p = re.compile("[A-Za-z_]"       # letter or underscore
               "[A-Za-z0-9_]*"   # letter, digit or underscore
              )
