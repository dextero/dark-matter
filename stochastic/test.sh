#!/bin/bash

set -e

SCRIPT_DIR="$(dirname $(readlink -f "$0"))"

NUM_LENSES=1
X_RANGE='-5;5'
Y_RANGE='0.5;0.50001'
Z_RANGE='1;9'
RADIUS_RANGE='2;10'
HEIGHT_RANGE='0.5;1.5'
LIGHT_SOURCE='0;0;10'
IMAGES='70;0'

function sighandler() {
    exit 1
}

trap sighandler SIGINT

function generate_config() {
    local MAX_DEPTH="$1"
    local NUM_METAEPOCHS="$2"
    local MUTATION_RATE_0="$3"
    local POPULATION_SIZE_0="$4"
    local SESSION_NAME="$5"

    local MUTATION_RATE_1="$(bc <<<"$MUTATION_RATE_0 / 10.0")"
    local MUTATION_RATE_2="$(bc <<<"$MUTATION_RATE_1 / 10.0")"

    local POPULATION_SIZE_1="$(bc <<<"$POPULATION_SIZE_0 / 10")"
    local POPULATION_SIZE_2="$(bc <<<"$POPULATION_SIZE_1 / 10")"

    sed -e "s/##MAX_DEPTH##/$MAX_DEPTH/g" \
        -e "s/##NUM_METAEPOCHS##/$NUM_METAEPOCHS/g" \
        -e "s/##SESSION_NAME##/$SESSION_NAME/g" \
        -e "s/##MUTATION_RATE_0##/$MUTATION_RATE_0/g" \
        -e "s/##MUTATION_RATE_1##/$MUTATION_RATE_1/g" \
        -e "s/##MUTATION_RATE_2##/$MUTATION_RATE_2/g" \
        -e "s/##POPULATION_SIZE_0##/$POPULATION_SIZE_0/g" \
        -e "s/##POPULATION_SIZE_1##/$POPULATION_SIZE_1/g" \
        -e "s/##POPULATION_SIZE_2##/$POPULATION_SIZE_2/g" \
        "$SCRIPT_DIR/resourcemanager.template.xml"
}

for ITERATION in $(seq 10); do
    mkdir -p "$ITERATION"
    pushd "$ITERATION"
    >times.txt

    for METAEPOCHS in 10 20 50; do
    for LEVELS in 1 2 3; do
    for MUTATION_RATE in 0.1 0.2 0.5; do
    for POPULATION_SIZE in 1000 2000 5000; do
        mkdir -p current_config
        generate_config \
            $LEVELS \
            $METAEPOCHS \
            $MUTATION_RATE \
            $POPULATION_SIZE \
            "${METAEPOCHS}_metaepochs_${LEVELS}_levels_${MUTATION_RATE}_mut_rate_${POPULATION_SIZE}_population" \
            | tee "current_config/${METAEPOCHS}_metaepochs_${LEVELS}_levels_${MUTATION_RATE}_mut_rate_${POPULATION_SIZE}_population.resourcemanager.xml" \
            > current_config/resourcemanager.xml

        echo "* $METAEPOCHS metaepochs, $LEVELS levels, mutation rate $MUTATION_RATE, population size $POPULATION_SIZE" | tee -a times.txt
        /usr/bin/time -o >(tee -a times.txt) -f '\t%E real,\t%U user,\t%S sys' \
            java -jar "$SCRIPT_DIR/target/stochastic-0.0.1-SNAPSHOT-jar-with-dependencies.jar" \
                --num-lenses "$NUM_LENSES" \
                --lens-z "$Z_RANGE" \
                >/dev/null 2>&1
    done
    done
    done
    done
popd
done
