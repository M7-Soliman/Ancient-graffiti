from cltk.tag.pos import POSTag
import csv
import re
import os

## Navigate up a few directories
##for x in range(1):
##   os.chdir("..")
PATH = os.path.abspath(os.curdir)


FILE_NAME = PATH + "/data/AGPData/terms.csv"
OUTPUT_NAME = PATH + "/data/AGPData/gen_pos.csv"

latin_tagger = POSTag('latin')
greek_tagger = POSTag('greek')

terms = []

posMap = {"n":"noun", "v":"verb", "t":"participle",
          "a":"adjective", "d":"adverb", "c":"conjunction",
          "r":"preposition", "p":"pronoun", "m":"numeral",
          "i":"interjection", "e":"exclamation", "u":"punctuation"}

with open(FILE_NAME, newline='') as file:
    reader = csv.reader(file, delimiter=',')
    for row in reader:
        if row[0] != "term_id":
            term_id = row[0]
            term = row[1].split(",")[0]
            try:
                if re.match("[a-z]", term):
                    pos = latin_tagger.tag_ngram_123_backoff(term)[0][1]
                    if pos != None and pos[0].lower() in posMap:
                        pos = posMap[pos[0].lower()]
                    else:
                        pos = "review" 
                else:
                    pos = greek_tagger.tag_ngram_123_backoff(term)[0][1]
                    if pos != None and pos[0].lower() in posMap:
                        pos = posMap[pos[0].lower()]
                    else:
                        pos = "review"
            except:
                pos = "review"
            terms.append((term_id, row[1], pos))

with open (OUTPUT_NAME, 'w', newline='') as csvfile:
    fieldnames = ['term_id','term','pos']
    writer = csv.writer(csvfile, delimiter=',')
    for term in terms:
        writer.writerow([term[0], term[1], term[2]])
