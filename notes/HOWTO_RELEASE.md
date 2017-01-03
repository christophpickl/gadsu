
* RELEASE_SCRIPT = `/scripts/release.sh`
* BUILD_DIR = `../gadsu_release_build`

# Preconditions

* Check everything has been pushed to GIT!!!
* Check all GitHub issues are closed
* Check [TravisCI Build](https://travis-ci.org/christophpickl/gadsu) is green
* Check [Dependecy Report](https://www.versioneye.com/user/projects/572880644a0faa000b782062) to be up2date
* Execute manual smoke tests (app starts, create client, treatment, protocol)

# Release Script

* Start the RELEASE SCRIPT: `$ ./scripts/release.sh` (better to invoke from project directory rather inside the `scripts` folder)
* Specify release and next dev version; e.g.: `1.4.0` and `1.5.0-SNAPSHOT`
* Wait until the build is done (takes about 5 minutes) and find artifacts in the BUILD DIR

```
$ ./scripts/release.sh 

[RELEASE] Preparing new GADSU release
[RELEASE] ====================================
[RELEASE] Current version is: 1.4.0-SNAPSHOT

Enter RELEASE Version: 1.4.0
Enter next DEVELOPMENT Version: 1.5.0-SNAPSHOT

[RELEASE] Release Summary:
[RELEASE] ------------------------------------
[RELEASE]   Release Version: 1.4.0
[RELEASE]   Development Version: 1.5.0-SNAPSHOT
[RELEASE]   Build Directory: /Users/John/Dev/gadsu/../gadsu_release_build

[RELEASE] Do you confirm this release? [y/n] >>
 
...

[RELEASE] GIT push
[RELEASE] ------------------------------------
Counting objects: 7, done.
Delta compression using up to 4 threads.
Compressing objects: 100% (4/4), done.
Writing objects: 100% (7/7), 643 bytes | 0 bytes/s, done.
Total 7 (delta 2), reused 0 (delta 0)
To https://github.com/christophpickl/gadsu.git
   c2cd588..ec52680  master -> master


[RELEASE] Release 1.4.0 SUCCESSFULL
[RELEASE] ====================================

[RELEASE] Time needed: 283 seconds
[RELEASE] Copy the contents of the artifacts directory: /Users/John/Dev/gadsu/../gadsu_release_build/release_artifacts
total 327848
-rw-r--r--@ 1 John  staff  56525158 Jul 14 18:48 Gadsu-1.4.0.dmg
-rw-r--r--  1 John  staff  55629462 Jul 14 18:48 Gadsu-1.4.0.jar
-rwxr-xr-x  1 John  staff  55693462 Jul 14 18:48 Gadsu.exe
 
$ 
```

# Postdoings

* Make a backup of local `~/.gadsu` folder
* Start the APP and JAR (and EXE if possible), and verify functionality via smoke tests
    * If there was an error, fix it, and create new x.x.1 bugfix version
* Pull changes from remote git repo to local
* Copy the 3 released artifacts in $BUILD_DIR/release_artifacts to the local __Gadsu/Releases/v1.x.0 directory

## GitHub Release

* Draft a new release on github: https://github.com/christophpickl/gadsu/releases
    * Select existing tag, e.g. `v1.99.0`
    * Set release title to: `Release 1.99.0`
    * Attach all three binary artifacts (exe, dmg, jar); this can take a moment as of 3x50 MB to upload
    * List all resolved issues for this milestone and enter the following description and publish afterwards:
---
Windows users please use the EXE, Apple users the DMG and for all other nixes the platform independent JAR file.

New stuff:

* #11 We did this
* #22 And that
* #33 And finally this
---

* Close the current milestone in GitHub

# Optional

* Create a new milestone, so we always have 3 ahead of us
    * Think about what could be included in next version(s)
* Clean up the [taskboard](https://github.com/christophpickl/gadsu/projects/1)
* Update screenshot on website (use the development action to prepare database accordingly)
