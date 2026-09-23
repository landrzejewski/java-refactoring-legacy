#!/usr/bin/env bash
# Narzędzie prowadzącego do pokazów na żywo - warsztat CineLegacy (moduły 3-8).
#
#   scripts/warsztat.sh list [m6]              lista scen i kroków
#   scripts/warsztat.sh test m6/s08            testy jednej sceny (albo całego modułu: m6)
#   scripts/warsztat.sh reset m6/s08           przywraca pakiet start do wersji z repozytorium
#   scripts/warsztat.sh jump m6/s08 2          kopiuje step2 do start (przeskok w pokazie)
#   scripts/warsztat.sh next [m6/s08]          następny krok do start (bez sceny: ostatnio używana)
#   scripts/warsztat.sh prev [m6/s08]          poprzedni krok do start
#   scripts/warsztat.sh status [m6/s08]        który krok jest teraz w start
#   scripts/warsztat.sh diff m6/s08 1 2        co zmienia krok 2 względem kroku 1 (0 = start), w kolorach
#   scripts/warsztat.sh diff m6/s08 1 2 --word ten sam diff, ale zmiany podświetlone w obrębie linii
#   scripts/warsztat.sh html                   generuje HTML z md/warsztat (wymaga Node.js)
#
# Scenę można podać krótko (m6/s08) albo pełną nazwą pakietu (m6/s08_state).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MAIN="$ROOT/src/main/java/pl/training/workshop"
TEST="$ROOT/src/test/java/pl/training/workshop"
STATE="$ROOT/.warsztat-state"

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

# Stan pokazu: ostatnio używana scena i krok, który jest w start (plik poza gitem).
save_state() {
    printf 'scene=%s\nstep=%s\n' "$1" "$2" > "$STATE"
}

state_value() {
    [ -f "$STATE" ] && sed -n "s/^$1=//p" "$STATE" || true
}

scene_arg() {
    local scene="${1:-$(state_value scene)}"
    [ -n "$scene" ] || die "podaj scenę (np. m6/s08) - nie ma jeszcze ostatnio używanej"
    echo "$scene"
}

# Kopia wariantu z nazwą pakietu zamienioną na SCENE - do porównywania kroków.
normalized_copy() {
    local variant="$1" target="$2"
    mkdir -p "$target"
    cp -R "$variant/." "$target/"
    find "$target" -name '*.java' -exec sed -i '' \
        "s/$(package_of "$variant" | sed 's/\./\\./g')/SCENE/g" {} +
}

steps_of() {
    find "$1" -maxdepth 1 -type d -name 'step*' -exec basename {} \; | sed 's/step//' | sort -n
}

# Który krok jest teraz w start: 0..N, albo "?" gdy start zmieniono ręcznie.
detect_step() {
    local dir="$1" tmp found="?"
    tmp=$(mktemp -d)
    normalized_copy "$dir/start" "$tmp/start"
    for n in $(steps_of "$dir" | sort -rn); do
        normalized_copy "$dir/step$n" "$tmp/step$n"
        if diff -rq "$tmp/start" "$tmp/step$n" >/dev/null 2>&1; then found="$n"; break; fi
    done
    if [ "$found" = "?" ] && git -C "$ROOT" ls-files --error-unmatch "$dir/start" >/dev/null 2>&1 \
        && [ -z "$(git -C "$ROOT" status --porcelain -- "$dir/start")" ]; then
        found=0
    fi
    rm -rf "$tmp"
    echo "$found"
}

current_step() {
    local scene="$1" dir="$2" step
    step=$(detect_step "$dir")
    if [ "$step" = "?" ]; then
        if [ "$(state_value scene)" = "$scene" ]; then
            step=$(state_value step)
            echo "warsztat: start zmieniony ręcznie - przyjmuję ostatni zapisany krok $step" >&2
        else
            step=0
            echo "warsztat: start zmieniony ręcznie - przyjmuję krok 0" >&2
        fi
    fi
    echo "$step"
}

cmd_move() {
    local scene; scene=$(scene_arg "${2:-}")
    local dir; dir=$(scene_dir "$scene")
    local current target last
    current=$(current_step "$scene" "$dir")
    last=$(steps_of "$dir" | tail -1)
    if [ "$1" = "next" ]; then target=$((current + 1)); else target=$((current - 1)); fi
    if [ "$target" -gt "$last" ]; then
        echo "$(basename "$dir"): start to już ostatni krok (step$last). Powrót: scripts/warsztat.sh reset $scene"
        save_state "$scene" "$current"
        return
    fi
    if [ "$target" -lt 0 ]; then
        echo "$(basename "$dir"): start jest już w stanie wyjściowym"
        save_state "$scene" 0
        return
    fi
    # podsumowanie zmian liczone przed podmianą: obecny start -> krok docelowy
    local stat
    if [ "$target" -eq 0 ]; then
        local color=never
        [ -t 1 ] && color=always
        stat=$(cd "$dir" && git diff --color="$color" --stat -R --relative -- start)
        cmd_reset "$scene"
    else
        stat=$(cmd_diff "$scene" 0 "$target" --stat-only)
        cmd_jump "$scene" "$target"
    fi
    echo
    [ -n "$stat" ] && echo "$stat"
    if [ "$current" -gt 0 ] && [ "$target" -gt 0 ]; then
        echo "Pełny diff: scripts/warsztat.sh diff $scene $current $target"
    else
        echo "Pełny diff: scripts/warsztat.sh diff $scene 0 $((current > target ? current : target))"
    fi
}

cmd_status() {
    local scene; scene=$(scene_arg "${1:-}")
    local dir; dir=$(scene_dir "$scene")
    local step last
    step=$(detect_step "$dir")
    last=$(steps_of "$dir" | tail -1)
    if [ "$step" = "?" ]; then
        echo "$(basename "$dir"): start zmieniony ręcznie (ostatni zapisany krok: $(state_value step)), kroki 0..$last"
    else
        echo "$(basename "$dir"): start = krok $step z $last"
    fi
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
    save_state "$1" 0
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
    save_state "$1" "${2/start/0}"
    echo "start = $(basename "$source") (powrót: scripts/warsztat.sh reset $1)"
}

cmd_diff() {
    local dir; dir=$(scene_dir "$1")
    local a b tmp
    a=$(variant_dir "$dir" "$2"); b=$(variant_dir "$dir" "$3")
    tmp=$(mktemp -d)
    for v in "$a" "$b"; do
        normalized_copy "$v" "$tmp/$(basename "$v")"
    done
    local mode="${4:-}" color=always
    [ -t 1 ] || color=never
    local args=(--no-index --color="$color" --diff-algorithm=histogram --stat --patch)
    [ "$mode" = "--word" ] && args+=(--word-diff=color)
    [ "$mode" = "--stat-only" ] && args=(--no-index --color="$color" --stat)
    if command -v delta >/dev/null && [ -t 1 ] && [ -z "$mode" ]; then
        # delta (brew install git-delta): podświetlanie składni, numery linii, zmiany w obrębie linii
        (cd "$tmp" && git --no-pager diff --no-index --color=always --diff-algorithm=histogram \
            "$(basename "$a")" "$(basename "$b")" | delta --line-numbers --paging=never) || true
    else
        (cd "$tmp" && git --no-pager diff "${args[@]}" "$(basename "$a")" "$(basename "$b")") || true
    fi
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
    next | prev) cmd_move "$1" "${2:-}" ;;
    status) cmd_status "${2:-}" ;;
    diff) cmd_diff "${2:?scena}" "${3:?krok A}" "${4:?krok B}" "${5:-}" ;;
    *) sed -n '2,15p' "$0" | sed 's/^# \{0,1\}//' ;;
esac
