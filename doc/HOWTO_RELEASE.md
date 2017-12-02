
Used variables:
* RELEASE_SCRIPT = `/bin/release.sh`
* BUILD_DIR = `../gadsu_release_build`

# Preconditions

* Check everything has been **pushed** to GIT!
* Check all GitHub **issues** are **closed**
* Check if the [build is green](https://travis-ci.org/christophpickl/gadsu)
* Verify fundamental functionality via manual **smoke tests** (start app, create/update client, create treatment)
* Check there are no TODO or FIXME
    * This is **IMPORTANT!** as otherwise the release build will fail!
* Optional: Check if [dependencies are up2date](https://www.versioneye.com/user/projects/572880644a0faa000b782062)

# Release Script

The actual doing consists of:

1. Start the RELEASE_SCRIPT: `$ ./bin/release.sh` (invoke the script from project directory rather inside the `scripts` folder)
1. Specify release and next dev version; e.g.: `1.4.0` and `1.5.0-SNAPSHOT`
1. Wait until the build is done (takes about 5 minutes) and find artifacts in the BUILD_DIR

**Sample output:**
```
$ ./bin/release.sh 

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
Writing objects: 100% (7/7), 642 bytes | 214.00 KiB/s, done.
Total 7 (delta 2), reused 0 (delta 0)
remote: Resolving deltas: 100% (2/2), completed with 1 local object.
To https://github.com/christophpickl/gadsu.git
   d2dd7c41..d2a1c465  master -> master
Total 0 (delta 0), reused 0 (delta 0)
To https://github.com/christophpickl/gadsu.git
 * [new tag]           1.13.0 -> 1.13.0


[RELEASE] Release 1.13.0 SUCCESSFULL
[RELEASE] ====================================

[RELEASE] Time needed: 458 seconds
[RELEASE] Contents of: /Users/foobar/gadsu_release_build/release_artifacts
total 357432
-rw-r--r--@ 1 Foo  staff  62793667 Dec  2 22:39 Gadsu-1.13.0.dmg
-rw-r--r--  1 Foo  staff  60071457 Dec  2 22:39 Gadsu-1.13.0.jar
-rwxr-xr-x  1 Foo  staff  60135457 Dec  2 22:39 Gadsu.exe
[RELEASE] 
[RELEASE] Now start the Post Release Kotlin script.
[RELEASE] 
 
$ 
```

# Postdoings

* _Optional_: Make a backup of local `~/.gadsu` folder (or just a DB backup)
* Start the APP and JAR (and EXE if possible), and verify functionality via **smoke tests**
    * Find them in: `/Users/John/Dev/gadsu/../gadsu_release_build/release_artifacts`
    * If there was an **error**, fix it, and create new x.x.1 bugfix version
* **Pull** changes from remote GIT repository to local
* Start the **Post Release Generator** Kotlin script in `release_generator.kt`
    * This will verify issues are closed, closes the milestone, drafts a new release and locally moves + uploads the artifacts to GitHub.

## Optional

* Clean up the [taskboard](https://github.com/christophpickl/gadsu/projects/1)
* Go through `TODO.md`
* Maybe create a new **milestone**
* Update **screenshot** on website (use the development action to prepare database accordingly)
