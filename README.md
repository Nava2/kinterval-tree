# kinterval-tree

![Build](https://github.com/Nava2/kinterval-tree/actions/workflows/gradle-check-pristine.yml/badge.svg)

## Introduction

This project consists of a single class: `IntervalTree`. This is a fork of @charcuterie/interval-tree.

IntervalTree is an implementation of a red-black interval-tree for half-open
integer intervals. Details can be found either explicitly or as exercises in
[Introduction to Algorithms](https://mitpress.mit.edu/books/introduction-algorithms).
It has the basic functionality one would expect from an interval-tree:
insertion, deletion, and overlap query.

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
