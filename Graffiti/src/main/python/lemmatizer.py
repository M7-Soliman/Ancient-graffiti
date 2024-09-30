from cltk.lemmatize.latin.backoff import BackoffLatinLemmatizer
from cltk.lemmatize.greek.backoff import BackoffGreekLemmatizer
from cltk.stem.latin.declension import CollatinusDecliner

import sys

latin_lem = BackoffLatinLemmatizer()
greek_lem = BackoffGreekLemmatizer()
dec = CollatinusDecliner()

def smartV2U(term):
    ret = ""
    vowels = ["a", "e", "i", "o", "u"]
    for i in range(len(term)):
        if i > 0 and i < len(term)-1:
            if (not (term[i-1] in vowels) \
               or not (term[i+1] in vowels))\
               and term[i]=="v":
                   ret += "u"
            else:
                ret += term[i]       
        else:
            ret += term[i]
    return ret

def lemmatize(term):
    lemma = latin_lem.lemmatize([term])
    if lemma[0][0] == lemma[0][1]:
        lemma = greek_lem.lemmatize([term])
    lemma = lemma[0][1]
    lemma = lemma.replace("1", "").replace("2", "").replace("-", "")
    return lemma

def decline(term):
    try:
        word = dec.decline(term, flatten=True)
        #Handle Verbs
        if len(word)==263 or len(word)==262 or len(word)==150:
            entry = word[0] + ", " + word[67]
        #Handle Deponent Verbs
        elif len(word)==156:
            entry = word[0] + ", " + word[36]
        #Handle Nouns
        elif len(word) == 12 or len(word) == 6 \
             or len(word)==14 or len(word) ==13 \
             or len(word)==20:
            entry = word[0] + ", " + word[3]       
        #Handle Adjectives of the 1st and 2nd Declension
        elif len(word) == 107 or len(word)==36:
            entry = word[0] + ", " + word[12] + ", " + word[24]
        elif len(word)==43:
            entry = word[0] + ", " + word[14] + ", " + word[29]
        #Handle Adjectives of the 3rd Declension
        elif len(word)==109:
            entry = word[0] + ", " + word[3]
        #Handle Other Parts of Speech
        else:
            entry = word[0]
    except:
        entry = term
    return entry

term = smartV2U(sys.argv[1])

print(decline(lemmatize(term)).replace("j", "i"))



    
