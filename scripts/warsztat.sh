#!/usr/bin/env bash
# Narzędzie prowadzącego do pokazów na żywo - warsztat CineLegacy (moduły 3-8).
#
#   scripts/warsztat.sh list [m6]              lista scen i kroków
#   scripts/warsztat.sh test m6/s08            testy jednej sceny (albo całego modułu: m6)
#   scripts/warsztat.sh reset m6/s08           przywraca pakiet start do wersji z repozytorium
#   scripts/warsztat.sh jump m6/s08 2          kopiuje step2 do start (przeskok w pokazie)
#   scripts/warsztat.sh diff m6/s08 1 2        co zmienia krok 2 względem kroku 1 (0 = start)
#   scripts/warsztat.sh html                   generuje HTML z md/warsztat (wymaga Node.js)
#
# Scenę można podać krótko (m6/s08) albo pełną nazwą pakietu (m6/s08_state).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MAIN="$ROOT/src/main/java/pl/training/workshop"
TEST="$ROOT/src/test/java/pl/training/workshop"

die() { echo "warsztat: $*" >&2; exit 1; }

scene_dir() {
    local module="${1%%/*}" scene="${1#*/}"
    [ "$module" != "$1" ] || die "podaj scenę jako mN/sNN, np. m6/s08"
    local match
    match=$(find "$MAIN/$module" -maxdepth 1 -type d -name "${scene}*" 2>/dev/null | sort | head -1)
    [ -n "$match" ] || die "nie ma sceny $1"
    echo "$match"
}

variant_dir() {
    if [ "$2" = "0" ] || [ "$2" = "start" ]; then echo "$1/start"; else echo "$1/step$2"; fi
}

package_of() {
    echo "pl.training.workshop.${1#"$MAIN"/}" | tr '/' '.'
}

cmd_list() {
    local filter="${1:-}"
    for module in "$MAIN"/m*/; do
        local m; m=$(basename "$module")
        [ -z "$filter" ] || [ "$filter" = "$m" ] || continue
        echo "$m"
        for scene in "$module"*/; do
            [ -d "$scene/start" ] || [ -d "$scene/step1" ] || continue
            local steps
            steps=$(find "$scene" -maxdepth 1 -type d \( -name 'start' -o -name 'step*' \) -exec basename {} \; \
                | sort -V | tr '\n' ' ')
            printf '  %-40s %s\n' "$(basename "$scene")" "$steps"
        done
    done
}

cmd_test() {
    local target="$1" pkg
    if [[ "$target" == */* ]]; then
        pkg=$(package_of "$(scene_dir "$target")")
    else
        pkg="pl.training.workshop.$target"
    fi
    (cd "$ROOT" && mvn -q test -Dtest="${pkg}.**" -Dsurefire.failIfNoSpecifiedTests=false)
    echo "OK: $pkg"
}

cmd_reset() {
    local dir; dir=$(scene_dir "$1")
    (cd "$ROOT" && git checkout -- "$dir/start" && git clean -fdq -- "$dir/start")
    echo "start przywrócony: $(package_of "$dir")"
}

cmd_jump() {
    local dir; dir=$(scene_dir "$1")
    local source; source=$(variant_dir "$dir" "$2")
    [ -d "$source" ] || die "nie ma kroku $2 w $(basename "$dir")"
    local from to
    from=$(package_of "$source"); to=$(package_of "$dir/start")
    rm -rf "$dir/start"
    mkdir -p "$dir/start"
    cp -R "$source/." "$dir/start/"
    find "$dir/start" -name '*.java' -exec sed -i '' "s/${from//./\\.}/$to/g" {} +
    echo "start = $(basename "$source") (powrót: scripts/warsztat.sh reset $1)"
}

cmd_diff() {
    local dir; dir=$(scene_dir "$1")
    local a b tmp
    a=$(variant_dir "$dir" "$2"); b=$(variant_dir "$dir" "$3")
    tmp=$(mktemp -d)
    for v in "$a" "$b"; do
        mkdir -p "$tmp/$(basename "$v")"
        cp -R "$v/." "$tmp/$(basename "$v")/"
        find "$tmp/$(basename "$v")" -name '*.java' -exec sed -i '' \
            "s/$(package_of "$v" | sed 's/\./\\./g')/SCENE/g" {} +
    done
    (cd "$tmp" && diff -ru "$(basename "$a")" "$(basename "$b")") || true
    rm -rf "$tmp"
}

cmd_html() {
    local dir="$ROOT/scripts/warsztat-html"
    [ -d "$dir/node_modules" ] || (cd "$dir" && npm ci --silent)
    (cd "$dir" && node build.mjs)
}

case "${1:-}" in
    html) cmd_html ;;
    list) cmd_list "${2:-}" ;;
    test) cmd_test "${2:?scena}" ;;
    reset) cmd_reset "${2:?scena}" ;;
    jump) cmd_jump "${2:?scena}" "${3:?krok}" ;;
    diff) cmd_diff "${2:?scena}" "${3:?krok A}" "${4:?krok B}" ;;
    *) sed -n '2,11p' "$0" | sed 's/^# \{0,1\}//' ;;
esac
