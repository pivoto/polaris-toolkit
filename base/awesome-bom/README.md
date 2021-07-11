# awesome-bom

生成`mvnw`：
```
mvn -N io.takari:maven:wrapper -Dmaven=3.6.1
```

切换版本号:
```
mvn versions:set -DnewVersion=xxx
```

`versions-maven-plugin`用法:
```
#**********************************************************************************************************************
#
# versions:commit
#   Removes the initial backup of the pom, thereby accepting the changes.
#
# versions:compare-dependencies
#   Compare dependency versions of the current project to dependencies or
#   dependency management of a remote repository project. Can optionally update
#   locally the project instead of reporting the comparison
#
# versions:dependency-updates-report
#   Generates a report of available updates for the dependencies of a project.
#
# versions:display-dependency-updates
#   Displays all dependencies that have newer versions available. It will also
#   display dependencies which are used by a plugin or defined in the plugin
#   within a pluginManagement.
#
# versions:display-parent-updates
#   Displays any updates of the project's parent project
#
# versions:display-plugin-updates
#   Displays all plugins that have newer versions available, taking care of Maven
#   version prerequisites.
#
# versions:display-property-updates
#   Displays properties that are linked to artifact versions and have updates
#   available.
#
# versions:force-releases
#   Replaces any -SNAPSHOT versions with a release version, older if necessary (if
#   there has been a release).
#
# versions:help
#   Display help information on versions-maven-plugin.
#   Call mvn versions:help -Ddetail=true -Dgoal=<goal-name> to display parameter
#   details.
#
# versions:lock-snapshots
#   Attempts to resolve unlocked snapshot dependency versions to the locked
#   timestamp versions used in the build. For example, an unlocked snapshot
#   version like '1.0-SNAPSHOT' could be resolved to '1.0-20090128.202731-1'. If a
#   timestamped snapshot is not available, then the version will remained
#   unchanged. This would be the case if the dependency is only available in the
#   local repository and not in a remote snapshot repository.
#
# versions:plugin-updates-report
#   Generates a report of available updates for the plugins of a project.
#
# versions:property-updates-report
#   Generates a report of available updates for properties of a project which are
#   linked to the dependencies and/or plugins of a project.
#
# versions:resolve-ranges
#   Attempts to resolve dependency version ranges to the specific version being
#   used in the build. For example a version range of '[1.0, 1.2)' would be
#   resolved to the specific version currently in use '1.1'.
#
# versions:revert
#   Restores the pom from the initial backup.
#
# versions:set
#   Sets the current project's version and based on that change propagates that
#   change onto any child modules as necessary.
#
# versions:set-property
#   Set a property to a given version without any sanity checks. Please be careful
#   this can lead to changes which might not build anymore. The sanity checks are
#   done by other goals like update-properties or update-property etc. they are
#   not done here. So use this goal with care.
#
# versions:set-scm-tag
#   Updates the current project's SCM tag.
#
# versions:unlock-snapshots
#   Attempts to resolve unlocked snapshot dependency versions to the locked
#   timestamp versions used in the build. For example, an unlocked snapshot
#   version like '1.0-SNAPSHOT' could be resolved to '1.0-20090128.202731-1'. If a
#   timestamped snapshot is not available, then the version will remained
#   unchanged. This would be the case if the dependency is only available in the
#   local repository and not in a remote snapshot repository.
#
# versions:update-child-modules
#   Scans the current projects child modules, updating the versions of any which
#   use the current project to the version of the current project.
#
# versions:update-parent
#   Sets the parent version to the latest parent version.
#
# versions:update-properties
#   Sets properties to the latest versions of specific artifacts.
#
# versions:update-property
#   Sets a property to the latest version in a given range of associated
#   artifacts.
#
# versions:use-dep-version
#
# versions:use-latest-releases
#   Replaces any release versions with the latest release version.
#
# versions:use-latest-snapshots
#   Replaces any release versions with the latest snapshot version (if it has been
#   deployed).
#
# versions:use-latest-versions
#   Replaces any version with the latest version.
#
# versions:use-next-releases
#   Replaces any release versions with the next release version (if it has been
#   released).
#
# versions:use-next-snapshots
#   Replaces any release versions with the next snapshot version (if it has been
#   deployed).
#
# versions:use-next-versions
#   Replaces any version with the latest version.
#
# versions:use-reactor
#   Replaces any versions with the corresponding version from the reactor.
#
# versions:use-releases
#   Replaces any -SNAPSHOT versions with the corresponding release version (if it
#   has been released).
#
#**********************************************************************************************************************
```

