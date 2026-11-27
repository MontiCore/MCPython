# Reference: https://docs.python.org/3.15/reference/compound_stmts.html#mapping-patterns
# Grammar:   double_star_pattern
# PEP:       634
# Since:     Python 3.10
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
match x:
    case {'a': a, **rest}:
        pass
