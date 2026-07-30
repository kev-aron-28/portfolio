# TreeMap

Unlike HashMap, which organizes entries by buckets, TreeMap stores entries in a Red-Black Tree, so everything is always sorted by key.

# firstKey()

Returns the smallest key

# lastKey()
Returns the largest key

# firstEntry()
Returns both key and value

# lastEntry()

# higherKey()
Returns the strictly greater key
- higherEntry()

# lowerKey()
Its the opposite of higherKey()
- lowerEntry()

# ceilingKey()
Smallest key greater than or equal to the given key
- ceiling()

# floorKey()
This is the opposite of ceiling, <=
- floorEntry()

# subMap()
You can do some range queries in the map with this

``` java
NavigableMap<Integer, String> sub = map.subMap(20, true, 40, true);
```

# headMap()
Everthing before a key

# tailMap()
Everything after a key