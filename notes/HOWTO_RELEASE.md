
* RELEASE SCRIPT = `$ ./scripts/release.sh`
* BUILD DIR = `${CWD}/../gadsu_release_build`

# Preconditions

* check all GitHub issues are closed
* execute manual smoke tests
* check everything has been pushed to GIT
* check the build dir does not exist

# Release Script

* start the RELEASE SCRIPT
* specify release and next dev version; e.g.: `1.4.0` and `1.5.0-SNAPSHOT`
* wait until the build is done and find artifacts in the BUILD DIR

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
-rw-r--r--@ 1 wu  staff  56525158 Jul 14 18:48 Gadsu-1.4.0.dmg
-rw-r--r--  1 wu  staff  55629462 Jul 14 18:48 Gadsu-1.4.0.jar
-rwxr-xr-x  1 wu  staff  55693462 Jul 14 18:48 Gadsu.exe
 
$ 
```

# Postdoings

* make a backup of local `~/.gadsu` folder
* start the APP and JAR (and EXE if possible), and verify functionality via smoke tests
* draft a new release on github: https://github.com/christophpickl/gadsu/releases
* ...

# Optional

* update screenshot on website

