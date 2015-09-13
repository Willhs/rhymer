# Rhymer
### *by Willhs*
*Takes a query and compiles a list of rhyming sentences.*

## How?
Uses a phonic dictionary to break words down in smaller phonetic parts (called phonemes), compares phonemes of words to identify rhyming syllables
### Example:
Regulation  
R 	EH	GY **AH L EY SH AH N**

Insulation  
IH N S **AH L EY SH AH N**

## To use
`java -jar rhymer.jar [query] [numResults]`  
(query and numResults are optional arguments).  
numResults = number of google search results 

### Query
Generates google search URL, downloads linked pages, removes boilerplate code (ads etc), parses the remaining text for rhyming sentences.

## Notes
- skips words not in phonic dictionary
- splits sentences by \n and .
- phonic representations of words in dictionary pertain to an accent (english I think)
- outputs rhymes to rhymes.txt