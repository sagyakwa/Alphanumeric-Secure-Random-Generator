# Alphanumeric Secure Random Generator
Source code for the Software Engineering class at Framingham State, Spring 2020

## Description
In this assignment, Professor Nelson is the customer, and a group of 4 are the clients making his product.
We are to generate 24 alphanumeric random strings given a sample CSV file. We are to incorporate the CSV file
somehow, any way we want, into our random number generation.

## What we did and why we did it
We used the SecureRandom implementation from java.security, and the DRBG implementation. Since Math.Random() and the Random()
implementations use a linear congruential generator (LCG), it is not cryptographically strong. The SecureRandom implementation
from java.security.SecureRandom uses a cryptographically strong pseudo-random number generator (CSPRNG)
suited for a thread safe, true random generator. On Windows, the default implementation for SecureRandom is SHA1PRNG on
Windows, and on Linux/Solaris/Mac, the default implementation is NativePRNG. SHA1PRNG can be 17 times faster than NativePRNG,
but seeding options are fixed. Another implementation is AESCounterRNG, which is 10x faster than SHA1PRNG, and also
continuously receives entropy from /dev/urandom, unlike the other PRNGs, but you sacrifice stability.
The DRBG implementation in Java 9+ returns a SecureRandom object of the specific algorithm supporting the specific
instantiate parameters. The implementation's effective instantiated parameters must match this minimum request but is
not necessarily the same. So, we set the SecureRandom algorithm to DRBG, with 256 bits of security strength, 
Prediction resistance + reseeding, which means it's unpredictable as long as the seed is unknown.

## How is the customer's information used?
The customer's information bits is used as a personalization string. The personalization string in the DRBG parameters. 
is combined with a secret entropy input and (possibly) a nonce to produce a seed for the secure random generation.

## Why not a True Random Bit Generator (TRBG) or Quantom Random Bit Generator (QRBG)?
Previously, it was thought about to use a TRNG or a QRBG, but from a business stand point, it has holes in it. A truly 
random generator is impossible without some deep knowledge in hardware engineering and more money to gather natural entropy. 
The only other way was to use the API from Random.org or the QRBG generator from the Rudjer Boskovic Institute, in Laboratory 
for Stochastic Signals and Process Research in Croatia. The downsides I see here are that:

1. Every API key for both services 
has an allotted quota, and if you as a company wanted to increase this quota, that would cost extra, assuming such services
were provided. 

2. Using and trusting a third party source for a service you want to be secure is always a bit nerve racking.

3. If the service goes down, then your business goes down, unless you pay for some kind of maintenance, which again,
increases overall cost. Even if you had the DRBG implementation to fall back on, not all of your generation would be 
truly random anymore.
