#!/bin/bash

START=`date +%s`

CWD=`pwd`

BUILD_DIR=$CWD/../gadsu_release_build
ARTIFACTS_DIR=$BUILD_DIR/release_artifacts
# unfortunately the fatJar plugin uses the folder name as the jar filename :-/
CHECKOUT_DIR=$BUILD_DIR/Gadsu
GIT_URL=https://github.com/christophpickl/gadsu.git

myEcho() {
    echo "[RELEASE] $1"
}

checkLastCommand() {
    if [ $? -ne 0 ] ; then
        myEcho "Last command did not end successful!"
        exit 1
    fi
}

changeVersion() {
    echo
    myEcho "Changing version in [version.properties] to: $1"
    echo "version=$1" > version.properties
    checkLastCommand
}

source version.properties

echo
myEcho "Preparing new GADSU release"
myEcho "===================================="
myEcho "Current version is: $version"

echo
read -p "Enter RELEASE Version: " VERSION_RELEASE
read -p "Enter next DEVELOPMENT Version: " VERSION_DEVELOPMENT
# maybe also prompt for git credentials?!?

echo
myEcho "Release Summary:"
myEcho "------------------------------------"
myEcho "  Release Version: $VERSION_RELEASE"
myEcho "  Development Version: $VERSION_DEVELOPMENT"
myEcho "  Build Directory: $BUILD_DIR"

echo


while true; do
    read -p "[RELEASE] Do you confirm this release? [y/n] >> " yn
    case $yn in
        [Yy]* ) break;;
        [Nn]* ) echo "Aborted"; exit;;
        * ) myEcho "Please answer y(es) or n(o)";;
    esac
done
echo

if [ -d "$BUILD_DIR" ]; then
    echo
    myEcho "Removing old build directory at: $BUILD_DIR"
    rm -rf $BUILD_DIR
fi
mkdir $BUILD_DIR
mkdir $ARTIFACTS_DIR

cd $BUILD_DIR



echo
myEcho "Checking out source to: $CHECKOUT_DIR"
myEcho "------------------------------------"
git clone $GIT_URL $CHECKOUT_DIR
cd $CHECKOUT_DIR

# check there are no snapshots

echo
myEcho "Running test build."
myEcho "------------------------------------"
./gradlew test testUi check
checkLastCommand

changeVersion $VERSION_RELEASE

echo
myEcho "Creating assemblies."
myEcho "------------------------------------"
./gradlew createDmg fatJar -Dgadsu.enableMacBundle=true
checkLastCommand

cp build/distributions/*.dmg $ARTIFACTS_DIR
cp build/libs/*.jar $ARTIFACTS_DIR
echo
myEcho "Copied artifacts to: $ARTIFACTS_DIR"

echo
myEcho "GIT committing and tagging result"
myEcho "------------------------------------"
git add .
git commit -m "[Auto-Release] current release version: $VERSION_RELEASE"
checkLastCommand
git tag $VERSION_RELEASE
checkLastCommand

changeVersion $VERSION_DEVELOPMENT

git add .
git commit -m "[Auto-Release] next development version: $VERSION_DEVELOPMENT"
checkLastCommand

git push
checkLastCommand

END=`date +%s`
ELAPSED=$(( $END - $START ))

echo
echo
myEcho "Release $VERSION_RELEASE SUCCESSFULL"
myEcho "===================================="
echo
myEcho "Time needed: $ELAPSED seconds"
myEcho "Copy the contents of the artifacts directory: $ARTIFACTS_DIR"
ls -l $ARTIFACTS_DIR
echo

exit 0
