# Rhymer
## by Willhs
*Takes text or a query and compiles a list of rhyming sentences.*

## How?
Uses a phonic dictionary to break words down in smaller phonetic parts (called phonemes), compares phonemes of words to identify rhyming syllables
### Example:
R e  g  u  l a  t  io n
R EH GY **AH L EY SH AH N**

I  n s u  l a  t  io n
IH N S **AH L EY SH AH N**
## Given a query
Generates google search URL, downloads linked pages, removes boilerplate code (ads etc), parses the remaining text for rhyming sentences.
## Given a text file
Finds rhyming sentences in the text file.

## Notes
- skips words not in phonic dictionary
- phonic representations of words pertain to one accent (english I think)
- outputs rhymes to rhymes.txt
