""" 
Author: Grace MacDonald
Using cltk (Classical language toolkit) to lemmatize terms
then it writes out the original term, lemmatized version, and pos
to a csv

This is intended to be used as last step of process to lemmatize terms,
starting with LEMLAT, then taking terms -- most of which are Greek through
CLTK
"""

from cltk.lemmatize.lat import LatinBackoffLemmatizer
from cltk.lemmatize.grc import GreekBackoffLemmatizer
from cltk.tag.pos import POSTag
from cltk.data.fetch import FetchCorpus
import os
import csv

#needed these the first time, then it worked without
corpus_downloader = FetchCorpus(language="lat")
corpus_downloader.import_corpus("lat_models_cltk")
corpus_downloader = FetchCorpus(language="grc")
corpus_downloader.import_corpus("grc_models_cltk")


latin_lem = LatinBackoffLemmatizer()
greek_lem = GreekBackoffLemmatizer()

PARENT = os.getcwd()
PARENT = os.path.dirname(PARENT)
PARENT = os.path.dirname(PARENT)
PARENT = os.path.dirname(PARENT)

OUTPUT_NAME = PARENT + "/src/main/resources/indexing/pompeiiCLTKLemma.csv"



posMap = {"n":"noun", "v":"verb", "t":"participle",
          "a":"adjective", "d":"adverb", "c":"conjunction",
          "r":"preposition", "p":"pronoun", "m":"numeral",
          "i":"interjection", "e":"exclamation", "u":"punctuation"}

latin_tagger = POSTag('lat')
greek_tagger = POSTag('grc')



def lemmatize(term):
    """Lemmatizes first with Greek, then for certain
    terms which did not get a result, tries Latin"""
    lemma = greek_lem.lemmatize([term])
    if lemma[0][0] == lemma[0][1]:
        lemma = latin_lem.lemmatize([term])
    lemma = lemma[0][1]
    lemma = lemma.replace("1", "").replace("2", "").replace("-", "")
    return lemma

def getPOS(term):
    pos = latin_tagger.tag_unigram(term)[0][1]
    if pos != None and pos[0].lower() in posMap:
        pos = posMap[pos[0].lower()]
    else:
        pos = greek_tagger.tag_unigram(term)[0][1]
        if pos != None and pos[0].lower() in posMap:
            pos = posMap[pos[0].lower()]
        else:
            pos = "unknown"

    return pos

def lemmatizeFromTXT(fileName):
    """Reads in a .txt file to lemmatize the contents"""
    terms=[]
    with open(PARENT+ "/src/main/resources/indexing/" + fileName) as file:
        for line in file:
            for term in line.split():
                lemm = lemmatize(term)
                pos = getPOS(term)
                terms.append((term, lemm, pos))
    print("finished lemmatizing...")
    writeToCSV(terms)


def writeToCSV(terms):
    """Writes out term and lemmatized version to a csv file"""
    with open (OUTPUT_NAME, 'w', newline='') as csvfile:
        fieldnames = ['term', 'lemmatized', 'pos']
        writer = csv.writer(csvfile, delimiter=',')
        for term in terms:
            writer.writerow([term[0],term[1], term[2]])


def main():
    lemmatizeFromTXT("hercUniqueTerms.txt.unk.unk")


if __name__ == '__main__':
    main()


    
