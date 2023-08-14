# kinterval-tree

![License](https://img.shields.io/github/license/Nava2/kinterval-tree)

![Build](https://img.shields.io/github/actions/workflow/status/Nava2/kinterval-tree/gradle-check-pristine.yml)
![Release](https://img.shields.io/github/v/release/Nava2/kinterval-tree)

## Introduction

This project consists of a single class: `IntervalTree`. This is a fork of [@charcuterie/interval-tree](https://github.com/charcuterie/interval-tree).

IntervalTree is an implementation of a red-black interval-tree for half-open
integer intervals. Details can be found either explicitly or as exercises in
[Introduction to Algorithms](https://mitpress.mit.edu/books/introduction-algorithms).
It has the basic functionality one would expect from an interval-tree:
insertion, deletion, and overlap query.

## Use in your project

```kotlin
dependencies {
    implementation("net.navatwo:kinterval-tree:0.1.0")
}
```

## Why write this?

The short story is that I needed a data structure to represent a collection of
gene annotations in the manner that the IntervalSetTree does. I don't know of
any implementation online that does this. The simpler `IntervalTree` is included
with the hope that others may find it helpful, since it

1. is documented
2. doesn't have public methods that return null
3. doesn't expose the underlying node structure
4. is tested

## Why not use another implementation?

There is a lot of debate on the Internet about which sort of implementation is
"best": top-down vs bottom-up, 2-3 vs 2-3-4, etc. Whatever the negatives of the
CLRS implementation may be, the benefit is that clear, thorough documentation
can be found in any university library.

## How do I use this?

Using this classes should be straightforward. In the following examples, Impl
implements the Interval interface.

### Creating an empty tree

Creating trees is done through the class constructors.

Empty tree:

```kotlin
val tree = IntervalTree<Impl>()
```

One-element tree:

```kotlin
val tree = IntervalTree(Impl(1, 100))
```

### Adding intervals

```kotlin
tree.insert(Impl(3, 10))
```

This method returns a boolean if the value was added (that is, no duplicate
found), so feel free to do something like

```kotlin
if (tree.insert(interval)) {
  celebrate(goodTimes)
} else {
  cry()
}
```

### Querying the tree

Querying the tree is simply

```kotlin
interval in tree
```

If you're looking for, say, the maximum value

```kotlin
val max = tree.maximum().orElseThrow { SomeTypeOfException("cant find the max!") }
```

You can also iterate through the tree

```kotlin
for (i in tree) {
  println(i.toString())
}
```

```kotlin
tree.forEach { println(it.toString()) }
```

```kotlin
val it = tree.iterator()
while (it.hasNext()) {
  println(it.next().toString())
}
```

Overlap queries are pretty much the same.

```kotlin
tree.overlappers(someInterval)
    .forEach { println(it.toString()) }
```

### Removing intervals

Removing intervals is as you might guess.

```kotlin
if (tree.delete(someInterval)) {
  println("Get outta here!")
}
```

```kotlin
if (tree.deleteOverlappers(someInterval)) {
  println("All y'all get outta here!")
}
```

## Development

### Releasing

To release, run the following (or set the env variables via `export FOO='BAR'`):

Setup your local `~/.gradle/gradle.properties` with the following variables, or pass as `-Pvariable=value` arguments

```
signing.keyId=<last eight digits of key id>
signing.password=<password>
signing.secretKeyRingFile=/Users/my_user/.gnupg/secring.gpg

sonatypeUsername=<sonatype user token>
sonatypePassword=<sonatype user token password>
```

```shell
# Clean the repo first to not have any old artifacts
./gradlew clean

# Verify the repo is in good shape
./gradlew check

# Tag a version
git tag v0.0.0

# Publish a new build - BE MINDFUL OF SHELL HISTORY PRESERVING ENVIRONMENT VARIABLES
RELEASE=1 ./gradlew build publishToSonatype closeAndReleaseSonatypeStagingRepository

# Push tags to github
git push --tags

# Create a new release: https://github.com/Nava2/kinterval-tree/releases
```