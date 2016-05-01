

bug, wenn aenderung verwerfen, treatment list ist leer!


https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet




# build matrix


env:
	- RELEASE=development
 	- RELEASE=master


==> command for DEV build
	git checkout dev
	- gradlew test

==> command for RELEASE build
	git checkout master
	- gradlew test
	./release
	git merge master > dev


# blacklist
branches:
  except:
    - legacy
    - experimental

# whitelist
branches:
  only:
    - master
    - stable





#before_script:
#  - chmod +x before_script.sh

before_script: .travis/setup.sh
script: .travis/build.sh


if [ $TRAVIS_BRANCH == 'develop' ]; then

if [[ $TRAVIS_BRANCH == 'master' ]]
  test
  release
else
  test
fi

script:
  - if [ "$TRAVIS_BRANCH" == "develop" ]; then
      mkdir foobar ... deploy;
      ./gradlew test;
    fi




# Setup Git
git config user.name "Travis-CI"
git config user.email "travis@no.reply"

# If there is a new version of the master branch
if git status | grep patterns > /dev/null 2>&1
then
  # it should be committed
  git add .
  git commit -m ":sparkles: :up: Automagic Update via Travis-CI"
  git push --quiet "https://${GH_TOKEN}:x-oauth-basic@${GH_REF}" gh-pages > /dev/null 2>&1
fi



master:
	script: "bundle exec rake"
development:
	script: "bundle exec rspec spec"
