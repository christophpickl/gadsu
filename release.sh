#!/bin/bash

checkLastCommand() {
    if [ $? -ne 0 ] ; then
        echo "Last command did not end successful!"
        exit 1
    fi
}

changeVersion() {
    echo ""
    echo "Changing version in [version.properties] to: $1"
    echo "version=$1" > version.properties

}

source version.properties

echo "Preparing new GADSU release"
echo "======================="
echo "Current version is: $version"

echo
read -p "Enter RELEASE Version: " VERSION_RELEASE
read -p "Enter DEVELOPMENT Version: " VERSION_DEVELOPMENT
# maybe also prompt for git credentials?!?

echo ""
echo "Release Summary:"
echo "-----------------------"
echo "  Release Version: $VERSION_RELEASE"
echo "  Development Version: $VERSION_DEVELOPMENT"
echo ""


while true; do
    read -p "Do you confirm this release? [y/n] " yn
    case $yn in
        [Yy]* ) break;;
        [Nn]* ) echo "Aborted"; exit;;
        * ) echo "Please answer y(es) or n(o)";;
    esac
done

cd ..

BUILD_DIR=gadsu_release_build
ARTIFACTS_DIR=release_artifacts

mkdir $BUILD_DIR
cd $BUILD_DIR
mkdir $ARTIFACTS_DIR
CWD=`pwd`

ABSOLUTE_ARTIFACTS_DIR="$CWD/$ARTIFACTS_DIR"

echo ""
echo "Checking out source to: $CWD/gadsu"
git clone https://github.com/christophpickl/gadsu.git gadsu
cd gadsu

# TODO check there are no snapshots

echo ""
echo "Running test build."
./gradlew test testUi check
checkLastCommand

changeVersion $VERSION_RELEASE

echo ""
echo "Creating assemblies."
./gradlew createDmg fatJar -Dgadsu.enableMacBundle=true
checkLastCommand

cp build/distributions/*.dmg $ABSOLUTE_ARTIFACTS_DIR
cp build/libs/*.jar $ABSOLUTE_ARTIFACTS_DIR
echo "Copied artifacts to: $ABSOLUTE_ARTIFACTS_DIR"

echo ""
echo "GIT committing and tagging result"
git commit -m "[Auto-Release] current release version: $VERSION_RELEASE"
git tag $VERSION_RELEASE

changeVersion $VERSION_DEVELOPMENT

git commit -m "[Auto-Release] next development version: $VERSION_DEVELOPMENT"

# git push
echo "TODO TODO TODO GIT push"

echo ""
echo ""
echo "Release done"
# TODO keep track of time needed

exit 0
