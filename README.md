# Mule Lettuce Redis Connector
![Maven Central](https://img.shields.io/maven-central/v/cloud.anypoint/mule-lettuce-redis-connector)

A low level connector providing mule applications the ability to use redis commands directly.

The intention of this connector is to fully support all [documented redis commands](https://redis.io/commands).

## Installation

Add this dependency to your application pom.xml

```
<groupId>cloud.anypoint</groupId>
<artifactId>mule-lettuce-redis-connector</artifactId>
<version>0.1.21</version>
<classifier>mule-plugin</classifier>
```

## Features

### Dynamic Command

The Send Command operation allows a developer to send any arbitrary command and arguments to the server, and block until the reply is received.

### Commands

#### Server Commands
* `PING`

#### Key Value Commands
* `APPEND`
* `COPY`
* `DECR`
* `DEL`
* `EXPIRE`
* `GET`
* `GETDEL`
* `GETEX`
* `GETRANGE`
* `GETSET`
* `INCR`
* `MSET`
* `PERSIST`
* `PEXPIRE`
* `PTTL`
* `SCAN`
* `SET`
* `TOUCH`
* `TTL`

#### Hash Commands
* `HEXISTS`
* `HGET`
* `HGETALL`
* `HLEN`
* `HMGET`
* `HSCAN`
* `HSET`

#### List Commands
* `BLPOP`
* `BLMOVE`
* `BRPOP`
* `LMOVE`
* `LPOP`
* `LPUSH`
* `LSET`
* `RPUSH`
* `RPOP`

#### Set Commands
* `SADD`
* `SCARD`
* `SDIFF`
* `SISMEMBER`
* `SMEMBERS`
* `SMISMEMBER`
* `SPOP`
* `SRANDMEMBER`
* `SREM`
* `SSCAN`

#### Sorted Set Commands
* `ZADD`
* `ZRANK`
* `ZSCORE`

#### Geospatial Commands
* `GEOADD`
* `GEODIST`
* `GEOPOS`
* `GEOSEARCH`

#### Channel Commands
* `PUBLISH`

#### Stream Commands
* `XADD`
* `XDEL`
* `XRANGE`
* `XREAD`
* `XTRIM`

### Sources
* `SUBSCRIBE`
* `PSUBSCRIBE`

### Search Operations

These high level operations automate the `*SCAN` commands by internally keeping track of the cursor
received in the reply from redis, and sending the scan command with the new cursor value repeatedly
until a cursor value of 0 is received.

* Search keys
* Search hash fields
* Search set members
