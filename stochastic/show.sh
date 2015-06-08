#!/bin/bash

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

INPUT="$1"

[[ "$INPUT" ]] || exit 1

CONFIG="$(dirname "$INPUT")/current_config/$(basename "$INPUT")"
CONFIG="${CONFIG%.txt}"

cp -f "current_config/${CONFIG}.resourcemanager.xml" "current_config/resourcemanager.xml"
java -jar "$SCRIPT_DIR/target/stochastic-0.0.1-SNAPSHOT-jar-with-dependencies.jar" --num-lenses 1 --lens-z '1;9' --show "$INPUT"
