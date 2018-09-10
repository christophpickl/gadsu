#!/bin/bash

START=`date +%s`

CWD=`pwd`

BUILD_DIR=${CWD}/../gadsu_release_build
ARTIFACTS_DIR=${BUILD_DIR}/release_artifacts
# unfortunately the fatJar plugin uses the folder name as the jar filename :-/
CHECKOUT_DIR=${BUILD_DIR}/Gadsu
GIT_URL=https://github.com/christophpickl/gadsu.git
VERSION_PROPERTIES_FILE=version.properties
VERSION_PROPERTIES_PATH="$CWD/$VERSION_PROPERTIES_FILE"
VERSION_LATEST_FILE=version_latest.txt

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
    myEcho "Changing version in [${VERSION_PROPERTIES_FILE}] to: $1"
    echo "version=$1" > ${VERSION_PROPERTIES_FILE}
    checkLastCommand
}
changeLatestVersion() {
    echo
    myEcho "Changing latest version in [${VERSION_LATEST_FILE}] to: $1"
    echo "$1" > ${VERSION_LATEST_FILE}
    checkLastCommand
}

# loads the 'version' property from a properties file
version="N/A"
source ${VERSION_PROPERTIES_PATH}
#echo "Loaded version '$version' from: $VERSION_PROPERTIES_PATH"

echo
myEcho "Preparing new GADSU release"
myEcho "===================================="
myEcho "Current version is: $version"

DEFAULT_RELEASE_VERSION=$(echo ${version}| cut -d "-" -f 1)

CURRENT_MINOR_VERSION=$(echo ${DEFAULT_RELEASE_VERSION}| cut -d "." -f 2)
NEXT_MINOR_VERSION=$(($CURRENT_MINOR_VERSION + 1))
DEFAULT_DEVELOPMENT_VERSION="1.$NEXT_MINOR_VERSION.0-SNAPSHOT"

echo
read -p "Enter RELEASE Version [$DEFAULT_RELEASE_VERSION]: " ENTERED_VERSION_RELEASE
read -p "Enter next DEVELOPMENT Version [$DEFAULT_DEVELOPMENT_VERSION]: " ENTERED_VERSION_DEVELOPMENT
# maybe also prompt for git credentials?!?


if [ ! -z "$ENTERED_VERSION_RELEASE" ]; then
    VERSION_RELEASE=${ENTERED_VERSION_RELEASE}
else
    VERSION_RELEASE=${DEFAULT_RELEASE_VERSION}
fi
if [ ! -z "$ENTERED_VERSION_DEVELOPMENT" ]; then
    VERSION_DEVELOPMENT=${ENTERED_VERSION_DEVELOPMENT}
else
    VERSION_DEVELOPMENT=${DEFAULT_DEVELOPMENT_VERSION}
fi


echo
myEcho "Release Summary:"
myEcho "------------------------------------"
myEcho "  Release Version: $VERSION_RELEASE"
myEcho "  Development Version: $VERSION_DEVELOPMENT"
myEcho "  Build Directory: $BUILD_DIR"
myEcho ""
./gradlew -version
git --version

echo


while true; do
    read -p "[RELEASE] Do you confirm this release? [y/n] >> " yn
    case ${yn} in
        [Yy]* ) break;;
        [Nn]* ) echo "Aborted"; exit;;
        * ) myEcho "Please answer y(es) or n(o)";;
    esac
done
echo

if [ -d "$BUILD_DIR" ]; then
    echo
    myEcho "Removing old build directory at: $BUILD_DIR"
    rm -rf ${BUILD_DIR}
fi
mkdir ${BUILD_DIR}
mkdir ${ARTIFACTS_DIR}

cd ${BUILD_DIR}



echo
myEcho "Checking out source to: $CHECKOUT_DIR"
myEcho "------------------------------------"
git clone ${GIT_URL} ${CHECKOUT_DIR}
cd ${CHECKOUT_DIR}


echo
myEcho "Change release version"
myEcho "------------------------------------"
changeVersion ${VERSION_RELEASE}
changeLatestVersion ${VERSION_RELEASE}


echo
myEcho "Verifying release build (TODOs, test)"
myEcho "------------------------------------"
#./gradlew check checkTodo test testUi
./gradlew check test testUi
checkLastCommand


echo
myEcho "Creating assemblies"
myEcho "------------------------------------"
./gradlew createDmg fatJar buildExe -Dgadsu.enableMacBundle=true
checkLastCommand

cp build/distributions/*.dmg ${ARTIFACTS_DIR}
cp build/libs/*.jar ${ARTIFACTS_DIR}
cp build/*.exe ${ARTIFACTS_DIR}

echo
myEcho "Copied artifacts to: $ARTIFACTS_DIR"


echo
myEcho "GIT committing and tagging result"
myEcho "------------------------------------"
git add .
git commit -m "[Auto-Release] current release version: $VERSION_RELEASE"
checkLastCommand

# https://git-scm.com/book/en/v2/Git-Basics-Tagging
# or a heavy weight annotated tag? $ git tag -a 1.1.0 -m "[Auto-Release] current release version: 1.1.0"
git tag ${VERSION_RELEASE}
checkLastCommand

echo
myEcho "Change next version"
myEcho "------------------------------------"
changeVersion ${VERSION_DEVELOPMENT}

git add .
git commit -m "[Auto-Release] next development version: $VERSION_DEVELOPMENT"
checkLastCommand

echo
myEcho "GIT push"
myEcho "------------------------------------"
# first do a usual push, then with tags, otherwise it wont work, i dont get it...
git push
checkLastCommand
git push origin --tags
checkLastCommand

END=`date +%s`
ELAPSED=$(( $END - $START ))

echo
echo
myEcho "Release $VERSION_RELEASE SUCCESSFULL"
myEcho "===================================="
echo
myEcho "Time needed: $ELAPSED seconds"
myEcho "Contents of: $ARTIFACTS_DIR"
ls -l ${ARTIFACTS_DIR}
myEcho
myEcho "Now start the Post Release Kotlin script (release_generator.kt)."
myEcho
echo

exit 0
