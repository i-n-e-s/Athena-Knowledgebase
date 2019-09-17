from nltk.tokenize import sent_tokenize, word_tokenize
import json
import h5py

# use this method in case the text should be read from a .txt-file
def get_tokens(input_path):
	with open(input_path, "r") as input_path:
		text = input_path.read()
		sents = sent_tokenize(text)
		tokens = []
		for sent in sents:
			tokens.append(word_tokenize(sent))
		return tokens

# use this method in case the text is already passed as plaintext
def tokenize_txt(text):
	sents = sent_tokenize(text)
	tokens = []
	for sent in sents:
		tokens.append(word_tokenize(sent))
	return tokens
		
def write_json(tokens, output_path, doc_key):
	with open(output_path, "a") as out:
		data = {}
		data["clusters"] = [[]] * len(tokens)
		data["sentences"] = tokens
		data["ner"] = [[]] * len(tokens)
		data["relations"] = [[]] * len(tokens)
		data["doc_key"] = doc_key # "X96_1059" # "S0"
		json.dump(data, out)
		out.write("\n")
	out.close()
	
'''
filename = '../../elmo/test.hdf5'
f = h5py.File(filename, 'r')
keys = list(f.keys())
#print keys
#doc_X96_1059 = []
#for i in range(-9, 0):
#	doc_X96_1059.append(list(f[keys[i]]))
#print(f[keys[-9]])
	
data_file = h5py.File('../../elmo/X96_1059.hdf5', 'w')
for i in range(-9, 0):
	data_file.create_dataset(keys[i], data=list(f[keys[i]]))
#data_file.create_dataset('dataset2', data=list(f[keys[-1]]))
data_file.close()
print "DONE!"

filename = '../../elmo/X96_1059.hdf5'
f = h5py.File(filename, 'r')
keys = list(f.keys())
print(keys)
print(list(f[keys[0]]))
'''	

'''	
source = "./data/processed_data/json/toJson/testAbstract.txt"
goal = "../testAbstract.json"
doc_key = "X96_1059"
tokens = get_tokens(source)
write_json(tokens, goal, doc_key)
'''

# example
# {"clusters": [[[90, 91], [107, 107]]], 
#	"sentences": [["Past", "work", "of", "generating", "referring", "expressions", "mainly", "utilized", "attributes", "of", "objects", "and", "binary", "relations", "between", "objects", "."], ["However", ",", "such", "an", "approach", "does", "not", "work", "well", "when", "there", "is", "no", "distinctive", "attribute", "among", "objects", "."], ["To", "overcome", "this", "limitation", ",", "this", "paper", "proposes", "a", "method", "utilizing", "the", "perceptual", "groups", "of", "objects", "and", "n-ary", "relations", "among", "them", "."], ["The", "key", "is", "to", "identify", "groups", "of", "objects", "that", "are", "naturally", "recognized", "by", "humans", "."], ["We", "conducted", "psychological", "experiments", "with", "42", "subjects", "to", "collect", "referring", "expressions", "in", "such", "situations", ",", "and", "built", "a", "generation", "algorithm", "based", "on", "the", "results", "."], ["The", "evaluation", "using", "another", "23", "subjects", "showed", "that", "the", "proposed", "method", "could", "effectively", "generate", "proper", "referring", "expressions", "."]],
#	"ner": [[[4, 5, "OtherScientificTerm"], [12, 13, "OtherScientificTerm"]], [], [[52, 53, "OtherScientificTerm"]], [], [[81, 82, "OtherScientificTerm"], [90, 91, "Method"]], [[107, 107, "Generic"], [112, 113, "OtherScientificTerm"]]],
#	"relations": [[], [], [], [], [], []],
#	"doc_key": "C04-1096"}
