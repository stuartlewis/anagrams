# Trustpilot Code Challenge

## The challenge: 
Solve an anagram. You know you have found the right anagrams when the MD5 checksum matches those given.
A word list of possible words is given.

## My solution:
The most simple solution is to form a tree of every possible combination of words, identify which are true
anagrams, calculate their MD5 checksums and find those that match.

This however is computationally expensive with each word that is searched as it grows exponentially.

### Step one - Reduce the word list as much as possible
The word list is almost 100,000 words long.  100,000 x 100,000 x 100,000 is a very large number and would take hours or days to 
traverse a tree that large.

The method chosen to reduce this number is to examine every word, remove duplicates (there are 129 in the word list given),
and then check which words could be in the anagram.  Or to put this another way, the anagram does not contain a 'h', so
any word containing a 'h' can be removed from the candidate word list.

Doing this reduces the word list down to a much more manageable 1,659 words.

### Step two - Identify which of these candidates is a true anagram
Look at each candidate to see if it is a true anagram. The initial (cheap) step is to see if it is the same
length as the anagram.  if it is, then a character by character comparison is undertaken to see if all characters match.

### Step three - Test anagrams
If an anagram is successfully found, compute its MD5 hash, and compare to the hashes we are looking for.  If found, party!

### Step four - Recursion
For each word in the word list, go back to step one, however this time the anagram is shorter, as it has had one word removed.

For example: If we are testing with the word trustpilot, then the anagram (which was 'poultry outwits ants') becomes 'y ouw ants'.

The steps are repeated - for example reduce the list of 1,659 words again, to only those that contain the letters in 'youwants'.

A stop-condition is set (or depth of the tree) depending on how many potential words to search in the anagram. Through experimentation, 
the easiest and more difficult anagrams were solved with three words (three levels of recursion) and the hardest with four.

## Performance notes
The code is not multi-threaded, and the time taken to search all three-word anagrams is a few minutes.  Depending on the use
case, this may be acceptable.

To go to four words takes several hours, which is less likely to be acceptable.  How would I address this?

Thankfully this problem can be solved in parallel very easily, as no thread depends on any other thread or has to share information.

Trustpilot runs in the AWS cloud, so I would address this as follows:

* Adapt the code (add additional maven dependencies) so that it can run as a Lambda function.
* Use an API gateway to accept anagram solving requests.
* The Lambda would run one iteration, and the fire new lambdas to solve the next level.
* Depending on the speed required, the second lambda (once it has been given the first word to examine) probably can complete itself rather than spawning more lambdas
* This means that the first lambda would spawn 1,659 more: one for each starting word.
* When a lambda finds one of the anagrams, it can report this back via the Simple Notification Service

This means that 1,660 lambda invocations would take place. Using the minimum of 128 MB RAM, and assuming they take 5 minutes to run, 
is 498,000 compute seconds, with 62,250 total compute-gigabyte-seconds, which has a total cost of $1.04 USD at current pricing.

![AWS system diagram](https://raw.githubusercontent.com/stuartlewis/anagrams/main/diagram.jpg)