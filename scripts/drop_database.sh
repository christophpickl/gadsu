#!/usr/bin/env bash

USER=`whoami`
PATH_TO_DELETE=/Users/$USER/.gadsu_dev/database*

echo "Going to delete contents of: $PATH_TO_DELETE"
echo

rm -rv ${PATH_TO_DELETE}

echo
echo "DONE"
