#!/bin/bash

classes_path="$1"
echo "The game files are stored in ${classes_path}"

read -p "How many games do you want to run? " -r number_of_games_to_run

folder_id=$(ls -l | grep -cE0 "run_[0-9]+")
((folder_id++))

mkdir -p "run_${folder_id}"
mkdir -p "run_${folder_id}/games"

output=-1

run_game(){
    WAIT_BEFORE_END=0 \
      java -cp "${classes_path}" \
      trifle.TrifleConsole 2 \
      --output-moves "run_${folder_id}/games/game_$1.in"

    output="$?"
}

game_count=0

while [ "$game_count" -lt "$number_of_games_to_run" ]
do
  echo "Game no.${game_count}"
  run_game "${game_count}"

  if [ "${output}" != 0 ]; then
    echo "Error: Game execution failed!"
    echo "Failed with code ${output}"
    exit 1
    break
  fi
  ((game_count++))
done

# Print results
echo "$game_count games have been run"