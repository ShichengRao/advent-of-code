#!/usr/bin/env bash
# Rebuilds this repo's history from the per-year GitHub repos.
#
#   tools/migration/import-history.sh <new-target-dir> [aoc2019-src-dir]
#
# For each year it clones the default branch and rewrites it with git-filter-repo:
#   1. strips puzzle inputs/answers and build output from every commit
#   2. moves everything under <year>/
#   3. sets every author/committer to Shicheng Rao (GitHub noreply) and adds a
#      Co-authored-by: abnew123 trailer, so both accounts get credit
#   4. qualifies "Merge pull request #N" so links still point at the old repo
# 2019 was never committed, so it is added from local files as a single commit.
# The years are then joined with one merge commit, and per-year tooling that now
# lives at the root (CI, Sonar, pre-commit, IDE files, tiles) is removed.
#
# Re-runnable: if more PRs land in the old repos before this repo is published,
# rebuild into a fresh directory and cherry-pick the later commits on top.
set -euo pipefail

TARGET=${1:?usage: import-history.sh <new-target-dir> [aoc2019-src-dir]}
Y2019=${2:-$HOME/Projects/IdeaProjects/aoc2019}
[ -e "$TARGET" ] && { echo "refusing: $TARGET already exists" >&2; exit 1; }
command -v git-filter-repo >/dev/null || { echo "needs git-filter-repo (brew install git-filter-repo)" >&2; exit 1; }

NAME="Shicheng Rao"
EMAIL="shichengrao@users.noreply.github.com"
COAUTHOR="Co-authored-by: abnew123 <25024051+abnew123@users.noreply.github.com>"
YEARS=(2021 2022 2023 2024 2025)
WORK="${TARGET%/}.work"
mkdir -p "$WORK"

repo_for() { case $1 in 2025) echo ShichengRao/advent-of-code-2025 ;; *) echo "abnew123/aoc$1" ;; esac; }

for y in "${YEARS[@]}"; do
  repo=$(repo_for "$y")
  dir="$WORK/$y"
  [ -e "$dir" ] && { echo "refusing: $dir already exists" >&2; exit 1; }
  echo "== $y <- $repo"
  git clone -q --single-branch --no-tags "https://github.com/$repo.git" "$dir"
  # Pass 1: drop inputs, answers, build output and anything cookie-like from all history.
  git -C "$dir" filter-repo --quiet --force --invert-paths \
    --path-glob '*.class' --path-glob 'out/*' --path-glob 'bin/*' --path-glob 'build/*' \
    --path-glob 'data/*' --path-regex '(^|/)[dD]ay[0-9]+\.txt$' --path-glob '*expectedResults*' \
    --path-glob '*session.cookie*' --path-glob '*.env' --path-glob '*.env.*' --path-regex '(^|/)\.DS_Store$'
  # Pass 2: move under <year>/, unify identity, add the co-author trailer, qualify PR refs.
  git -C "$dir" filter-repo --quiet --force --to-subdirectory-filter "$y" \
    --name-callback "return b'$NAME'" \
    --email-callback "return b'$EMAIL'" \
    --message-callback "
import re
m = re.sub(rb'Merge pull request #(\d+)', rb'Merge pull request $repo#\1', message)
return m.rstrip(b'\n') + b'\n\n$COAUTHOR\n'
"
done

git init -q -b main "$TARGET"
cd "$TARGET"
git config user.name "$NAME"
git config user.email "$EMAIL"

# 2019: never committed anywhere, so one commit dated to when the files were last written.
mkdir -p 2019
rsync -a --exclude .DS_Store "$Y2019/src" 2019/
d2019=$(find "$Y2019/src" -type f -not -name .DS_Store -exec stat -f '%m' {} \; | sort -n | tail -1)
d2019=$(date -r "$d2019" '+%Y-%m-%dT%H:%M:%S%z')
git add 2019
GIT_AUTHOR_DATE="$d2019" GIT_COMMITTER_DATE="$d2019" \
  git commit -q -m "Add 2019 solutions (days 1, 2, 5)" -m "$COAUTHOR"

# One merge commit whose parents are 2019 plus each year's rewritten tip. git's octopus
# strategy refuses unrelated histories, but every year lives in its own directory, so the
# merged tree is just the union of the parents' trees.
parents=(-p HEAD)
git read-tree HEAD
for y in "${YEARS[@]}"; do
  git fetch -q --no-tags "$WORK/$y" "HEAD:refs/import/$y"
  git read-tree --prefix="$y/" "refs/import/$y:$y"
  parents+=(-p "refs/import/$y")
done
merge=$(git commit-tree "$(git write-tree)" "${parents[@]}" \
  -m "Combine 2021-2025 into one repository" \
  -m "Each year's history is preserved under its own directory. Imported from abnew123/aoc2021, abnew123/aoc2022, abnew123/aoc2023, abnew123/aoc2024 and ShichengRao/advent-of-code-2025." \
  -m "$COAUTHOR")
git reset -q --hard "$merge"
for y in "${YEARS[@]}"; do git update-ref -d "refs/import/$y"; done

# Per-year tooling now handled once at the root.
for y in 2019 "${YEARS[@]}"; do
  if [ -d "$y/.aoc_tiles/tiles/$y" ]; then
    mkdir -p .aoc_tiles/tiles
    git mv "$y/.aoc_tiles/tiles/$y" ".aoc_tiles/tiles/$y"
  fi
  git rm -r -q --ignore-unmatch -- "$y/.aoc_tiles" "$y/.github" "$y/.pre-commit-config.yaml" \
    "$y/sonar-project.properties" "$y/.classpath" "$y/.project" "$y/.settings" "$y/.gitignore" "$y/"*.iml
  if [ -f "$y/README.md" ] && grep -q 'AOC TILES BEGIN' "$y/README.md"; then
    sed -i '' '/<!-- AOC TILES BEGIN -->/,/<!-- AOC TILES END -->/d' "$y/README.md"
    git add "$y/README.md"
  fi
done
git commit -q -m "Remove per-year tooling now handled at the repo root" \
  -m "CI, Sonar, pre-commit, Eclipse/IntelliJ files and AoC Tiles run once for the whole repo. Existing tile images move to .aoc_tiles/tiles/<year>/; the stale tile blocks are dropped from the per-year READMEs." \
  -m "$COAUTHOR"

# Sanity checks: a single identity, and no inputs anywhere in history.
idents=$(git log --all --format='%an <%ae>%n%cn <%ce>' | sort -u)
[ "$idents" = "$NAME <$EMAIL>" ] || { echo "unexpected identities:"; echo "$idents"; exit 1; }
if git log --all --name-only --format= | grep -qE '(^|/)(data/|[dD]ay[0-9]+\.txt$|expectedResults)|\.class$'; then
  echo "inputs or class files survived the filter" >&2; exit 1
fi
echo "done: $(git rev-list --count HEAD) commits in $TARGET (scratch clones left in $WORK)"
