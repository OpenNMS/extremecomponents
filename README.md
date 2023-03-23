# Extreme Components Redux

This repository is based on the original extremecomponents 1.x java code, imported from https://code.google.com/archive/p/extremetable/source/default/source

## What

This is a minimal port of the original extremecomponents 1.0.1 code to the latest dependencies.

It is almost entirely untested, except for the minimal unit tests provided in the original codebase, and a very small use of PDF and Excel file generation.

## Why?

OpenNMS has some ancient code using the original extremecomponents-1.0.1.jar to create PDFs or Excel files of one particular component.

In theory, JMesa is a substitute, but it has veered pretty far from this original API, and it seemed easier to uplift the original code to the latest dependencies rather than port our code to JMesa.

## What's Different?

First of all, I updated most of the dependencies to the latest (or at least non-CVE-laden) versions, most notably Fop and Apache POI.

Additionally, I did a few refactorings recommended by [Sonar](https://sonarcloud.io), although there's a ton more that _could_ be done.
These changes should mostly be transparent to you other than the adaptation of some methods and classes to now support generics.

Finally, I put it under `org.opennms.extremecomponents` so I can publish it to Maven Central without conflicting with existing stuff.

## Can You...?

No.

(lol)

But really, this is here mostly to solve a specific need to clean up some security issues in dependencies without breaking existing functionality.
I have no intention of keeping this up-to-date unless a transient dependency becomes problematic, or we find a regression from how things used to work.

Use at your own risk.

## In Conclusion...

No really, do not use this.
Please.
I beg of you.
