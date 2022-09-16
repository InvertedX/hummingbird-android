# Hummingbird-Android

### Android implementation of Uniform Resources (UR)

Hummingbird is a Java implementation of the [Uniform Resources (UR)](https://github.com/BlockchainCommons/Research/blob/master/papers/bcr-2020-005-ur.md) specification.
It is a direct port of the [URKit](https://github.com/BlockchainCommons/URKit) implementation by Wolf McNally. 
It contains both the classes to represent a UR, and a UR encoder and decoder to encode and decode to/from the QR representations.
Hummingbird requires a minimum of Java 8. 

## Setup

Hummingbird is hosted in Maven Central and can be added as a dependency with the following:

```
implementation('com.sparrowwallet:hummingbird:1.6.4')
```
 