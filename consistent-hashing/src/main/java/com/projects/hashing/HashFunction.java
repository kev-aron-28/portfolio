package com.projects.hashing;

import java.math.BigInteger;

public interface HashFunction {
    BigInteger hash(String key);
}
