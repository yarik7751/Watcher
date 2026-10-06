import re

PATH = "app/src/main/java/com/yarik/watcher/system_design.txt"
LIMIT = 100

with open(PATH, encoding="utf-8") as f:
    lines = f.read().split("\n")

out = []
in_code = False

def wrap_line(line):
    """Greedy wrap at spaces, preferring sentence-ending punctuation near the limit."""
    result = []
    rest = line
    while len(rest) > LIMIT:
        window = rest[:LIMIT + 1]
        # candidate breaks: end of a sentence/clause punctuation followed by space
        punct_breaks = [m.end() for m in re.finditer(r'[.!?:;»)\]]\s', window)]
        good = [b for b in punct_breaks if b >= LIMIT * 0.5]
        if good:
            cut = good[-1]
        else:
            cut = window.rfind(" ") + 1
            if cut <= 0:
                cut = LIMIT  # extremely long word; hard cut (shouldn't happen)
        result.append(rest[:cut].rstrip())
        rest = rest[cut:].lstrip()
    result.append(rest)
    return result

for line in lines:
    if line.strip().startswith("```"):
        in_code = not in_code
        out.append(line)
        continue
    if in_code or len(line) <= LIMIT:
        out.append(line)
    else:
        out.extend(wrap_line(line))

with open(PATH, "w", encoding="utf-8") as f:
    f.write("\n".join(out))

long_lines = [l for l in out if len(l) > LIMIT and not l.strip().startswith("```")]
print(f"total lines: {len(out)}")
print(f"lines over {LIMIT} chars (outside code blocks): {len(long_lines)}")
for l in long_lines[:10]:
    print(len(l), l[:80])
