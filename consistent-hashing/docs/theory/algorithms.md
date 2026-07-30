# SHA-256

SHA: Secure Hash Algorithm

And its part of the SHA-2 family designed by the NSA and standardized the 256 means the output
always is 256 bits (32 bytes = 64 hexadecimal charat)

# MD5 (Message digest algorithm 5)

It was designed by Ron Rivest in 1991, it always produces 128 bits (16 bytes, 32 hexadecimal characters)

# CRC (Cyclic Redundancy check)



## How to use in Java?

All standard hash functions are implemented through java.security.MessageDigest

``` java
MessageDigest md = MessageDigest.getInstance("SHA-256");
MessageDigest.getInstance("MD5");
MessageDigest.getInstance("SHA-1");
MessageDigest.getInstance("SHA-512");
MessageDigest.getInstance("SHA3-256");
```

# Full example

```
 public static String sha256(String input) throws Exception {

        MessageDigest md =
                MessageDigest.getInstance("SHA-256");

        byte[] hash =
                md.digest(input.getBytes(StandardCharsets.UTF_8));

        return HexFormat.of().formatHex(hash);
    }
```