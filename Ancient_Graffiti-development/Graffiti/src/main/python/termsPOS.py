""" 
Author: Grace MacDonald
Using cltk (Classical language toolkit) to obtain the part of 
speech of terms then it writes out the  term and its part of speech 
to a csv

Currently using both unigram and ngram_123_backoff, trying to see
which gives better results
"""

from cltk.tag.pos import POSTag
from cltk.data.fetch import FetchCorpus

import csv
import re
import os

PARENT = os.getcwd()
PARENT = os.path.dirname(PARENT)
PARENT = os.path.dirname(PARENT)
PARENT = os.path.dirname(PARENT)

FILE_NAME = PARENT + "/data/AGPData/terms.csv"
OUTPUT_NAME = PARENT + "/data/AGPData/new_pos.csv"

posMap = {"n":"noun", "v":"verb", "t":"participle",
          "a":"adjective", "d":"adverb", "c":"conjunction",
          "r":"preposition", "p":"pronoun", "m":"numeral",
          "i":"interjection", "e":"exclamation", "u":"punctuation"}

#needed these the first time, then it worked without
#corpus_downloader = FetchCorpus(language="lat")
#corpus_downloader.import_corpus("lat_models_cltk")
#corpus_downloader = FetchCorpus(language="grc")
#corpus_downloader.import_corpus("grc_models_cltk")

latin_tagger = POSTag('lat')
greek_tagger = POSTag('grc')


def posFromCSV(fileName):
    """Reads in a csv file of term_id and terms, finds the 
    part of speech and then calls a function to write out
    the results to a csv"""
    terms=[]

    with open(fileName, newline='') as file:
        reader = csv.reader(file, delimiter=',')
        for row in reader:
            if row[0] != "term_id":
                term_id = row[0]
                term = row[1].split(",")[0]
        
                pos = latin_tagger.tag_unigram(term)[0][1]
                if pos != None and pos[0].lower() in posMap:
                    pos = posMap[pos[0].lower()]
                else:
                    pos = greek_tagger.tag_unigram(term)[0][1]
                    if pos != None and pos[0].lower() in posMap:
                        pos = posMap[pos[0].lower()]
                    else:
                        pos = "review"

                
                pos2 = latin_tagger.tag_ngram_123_backoff(term)[0][1]
                if pos2 != None and pos2[0].lower() in posMap:
                    pos2 = posMap[pos2[0].lower()]
                else:
                    pos2 = greek_tagger.tag_ngram_123_backoff(term)[0][1]
                    if pos2 != None and pos2[0].lower() in posMap:
                        pos2 = posMap[pos2[0].lower()]
                    else:
                        pos2 = "review"
                
                terms.append((row[1], pos, pos2))

        writeToCSV(terms)

def posFromTXT(fileName):
    """Reads in a txt file of terms, finds the 
    part of speech and then calls a function to write out
    the results to a csv"""
    terms=[]
    with open(fileName) as file:
        for line in file:
            for term in line.split():
                pos = latin_tagger.tag_unigram(term)[0][1]
                if pos != None and pos[0].lower() in posMap:
                    pos = posMap[pos[0].lower()]
                else:
                    pos = greek_tagger.tag_unigram(term)[0][1]
                    if pos != None and pos[0].lower() in posMap:
                        pos = posMap[pos[0].lower()]
                    else:
                        pos = "review"

                pos2 = latin_tagger.tag_ngram_123_backoff(term)[0][1]
                if pos2 != None and pos2[0].lower() in posMap:
                    pos2 = posMap[pos2[0].lower()]
                else:
                    pos2 = greek_tagger.tag_ngram_123_backoff(term)[0][1]
                    if pos2 != None and pos2[0].lower() in posMap:
                        pos2 = posMap[pos2[0].lower()]
                    else:
                        pos2 = "review"


                terms.append((term, pos, pos2 ))
    writeToCSV(terms)

def writeToCSV(terms):
    """Accepts a list of triples containing the term and pos found
    using unigram and pos found using ngram_123, uses this to write out
    to a csv file"""
    with open (OUTPUT_NAME, 'w', newline='') as csvfile:
        fieldnames = ['term', 'pos unigram', 'pos 123 ngram', 'uni vs 123 ngram']
        writer = csv.writer(csvfile, delimiter=',')
        writer.writerow(['term','unigram pos', '123 ngram pos', 'uni vs 123 ngram'])
        x=""
        for term in terms:
            if term[1] == term[2]: 
                x="Same"
            else: 
                x="NOT same"
            writer.writerow([term[0],term[1], term[2], x])


def main():
    posFromTXT(PARENT+ "/src/main/resources/testTerms.txt")
    #posFromCSV(FILE_NAME)


if __name__ == '__main__':
    main()
    
