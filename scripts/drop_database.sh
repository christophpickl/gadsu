#!/usr/bin/env bash

PATH_TO_DELETE=/Users/wu/.gadsu_dev/database*

echo "Going to delete contents of: $PATH_TO_DELETE"
echo

rm -rv ${PATH_TO_DELETE}

echo
echo "DONE"
