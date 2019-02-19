#!/bin/bash

git checkout master && ./gradlew clean release -Prelease.disableChecks -Prelease.pushTagsOnly -Prelease.forceVersion=$1 && git checkout develop && git merge master && git push && VERSION=`./gradlew cV -q -Prelease.quiet` && sleep 30 && curl https://jitpack.io/com/cxpublic/cxense-android/$VERSION/cxense-android-$VERSION.pom
