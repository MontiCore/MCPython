# Reference: https://docs.python.org/3.15/reference/compound_stmts.html#wildcard-patterns
# Grammar:   named_expression
# PEP:       634
# Since:     Python 3.10
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
match _:
    case 1 if _:
        _ = 2
