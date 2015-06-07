#!/bin/bash

INPUT_FILE="$1"

if [[ -z "$INPUT_FILE" ]]; then
    INPUT_FILE="$(mktemp)"
    cat /dev/stdin > "$INPUT_FILE"
fi

function column() {
    local COLUMN="$1"
    local INPUT="$2"

    tail -n +2 "$INPUT" | cut -d\  -f"$COLUMN" | sort | uniq
}

function plot() {
    local INPUT_FILE="$1"
    local OUTPUT_FILE="$2"
    local X_LABEL="$3"
    local Y_LABEL="$4"
    shift; shift; shift; shift

    local PLOT_FILE="$(mktemp)"
    echo "set term png size 1920, 1080 lw 2" >"$PLOT_FILE"
    echo "set xlabel '$X_LABEL'" >>"$PLOT_FILE"
    echo "set ylabel '$Y_LABEL'" >>"$PLOT_FILE"
    echo "set output '$OUTPUT_FILE'" >>"$PLOT_FILE"
    echo "plot \\" >>"$PLOT_FILE"

    while [[ "$#" -gt 1 ]]; do
        local SUFFIX=
        [[ "$#" -ge 4 ]] && SUFFIX=", \\"
        echo "'$INPUT_FILE' using $2 title '$1' with linespoints $SUFFIX" >>"$PLOT_FILE"
        shift; shift
    done

    gnuplot "$PLOT_FILE"
}

DEFAULT_METAEPOCHS=$(column 1 "$INPUT_FILE" | tail -n 1)
DEFAULT_LEVELS=$(column 2 "$INPUT_FILE" | tail -n 1)
DEFAULT_MUTATION_RATE=$(column 3 "$INPUT_FILE" | tail -n 1)
DEFAULT_POPULATION=$(column 4 "$INPUT_FILE" | tail -n 1)

mkdir -p plots

TMPFILE="$(mktemp)"
grep -E "^[0-9]+ $DEFAULT_LEVELS $DEFAULT_MUTATION_RATE $DEFAULT_POPULATION" "$INPUT_FILE" > "$TMPFILE"
plot "$TMPFILE" 'plots/time_metaepochs.png' 'metaepochs' 'time[s]' 'min' '1:5' 'avg' '1:6' 'max' '1:7'
plot "$TMPFILE" 'plots/fit_metaepochs.png' 'metaepochs' 'fit' 'min' '1:8' 'avg' '1:9' 'max' '1:10'

grep -E "^$DEFAULT_METAEPOCHS [0-9]+ $DEFAULT_MUTATION_RATE $DEFAULT_POPULATION" "$INPUT_FILE" > "$TMPFILE"
plot "$TMPFILE" 'plots/time_levels.png' 'levels' 'time[s]' 'min' '2:5' 'avg' '2:6' 'max' '2:7'
plot "$TMPFILE" 'plots/fit_levels.png' 'levels' 'fit' 'min' '2:8' 'avg' '2:9' 'max' '2:10'

grep -E "^$DEFAULT_METAEPOCHS $DEFAULT_LEVELS [0-9]+\\.[0-9]+ $DEFAULT_POPULATION" "$INPUT_FILE" > "$TMPFILE"
plot "$TMPFILE" 'plots/time_mutation_rate.png' 'mutation_rate' 'time[s]' 'min' '3:5' 'avg' '3:6' 'max' '3:7'
plot "$TMPFILE" 'plots/fit_mutation_rate.png' 'mutation_rate' 'fit' 'min' '3:8' 'avg' '3:9' 'max' '3:10'

grep -E "^$DEFAULT_METAEPOCHS $DEFAULT_LEVELS $DEFAULT_MUTATION_RATE [0-9]+" "$INPUT_FILE" > "$TMPFILE"
plot "$TMPFILE" 'plots/time_population.png' 'population' 'time[s]' 'min' '4:5' 'avg' '4:6' 'max' '4:7'
plot "$TMPFILE" 'plots/fit_population.png' 'population' 'fit' 'min' '4:8' 'avg' '4:9' 'max' '4:10'
