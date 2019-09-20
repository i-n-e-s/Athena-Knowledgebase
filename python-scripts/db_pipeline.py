import os
import pymysql
pymysql.install_as_MySQLdb()
import util
from txt_to_json import tokenize_txt, get_tokens, write_json
from process_output import order_data, write_processed_json
import pyhocon
from pathlib import Path

def remove_duplicate_file(filepath):
	ifile = Path(filepath)
	if ifile.is_file():
		os.remove(filepath)
	
def get_json(plaintext, pid, json_file):
	tokens = get_tokens(plaintext) # tokenize_txt(plaintext) # 
	write_json(tokens, json_file, pid)
	
def get_elmo(json_path, hdf5_path):
	with open("generate_elmo.py","r") as elmo_file:
		lines = elmo_file.readlines()
	with open("generate_elmo.py","w") as elmo_file:
		for line in lines:
			if not line.startswith("fn =") and not line.startswith("outfn =") and not line.startswith("Elmo(fn, outfn)"):
				elmo_file.write(line)
		elmo_file.write("fn = '" + json_path + "'\n")
		elmo_file.write("outfn = '" + hdf5_path + "'\n")
		elmo_file.write("Elmo(fn, outfn)")
	elmo_file.close()
	
	import generate_elmo
	
def preprocess(json_file, hdf5_file):

	db = pymysql.connect(host="localhost",
						 user="athena",
						 passwd="ihavesecurityissues",
						 db="athena")
						 
	cursor = db.cursor()
	
	cursor.execute("SELECT paperID FROM paper")
	ids = cursor.fetchall()
	for i in ids:
		cursor.execute("SELECT p.paperAbstract FROM paper p WHERE paperID=%s", int(i[0]))
		plaintext = cursor.fetchall()
		if plaintext[0][0] is not None: # and i[0] <= 2000:
			#print(str(i[0]) + " plaintext: " + str(plaintext[0][0]))
			#if "[In Chinese]" in plaintext[0][0]: continue
			#if "'" in plaintext[0][0]: plaintext.replace("'", "''")
			get_json(plaintext[0][0], str(i[0]), json_file)

	db.close()
	
	#for i in range(1, 11):
	#	get_json("./txts/db_texts_text_{}.txt".format(str(i)), str(i), json_file)
	print("GOT JSONS!")
		
	get_elmo(json_file, hdf5_file)
	print("GOT HDF5S!")


def adapt_conf_paths(lm_path_dev, eval_path, output_path, exp):
	in_exp = False
	with open('experiments.conf', 'r+') as cfile, open('experiments_temp.conf', 'w') as tempfile:
		cf = cfile.readlines()
		for l, line in enumerate(cf):
			if line.startswith(exp): in_exp = True
			if in_exp is True:
				if line.startswith("}"): in_exp = False
				if "lm_path_dev" in line:
					tempfile.write("".join((line[:17], lm_path_dev, '"\n')))
				elif "eval_path" in line: 
					tempfile.write("".join((line[:15], eval_path, '"\n')))
				elif "output_path" in line: 
					tempfile.write("".join((line[:17], output_path, '"\n')))
				else: tempfile.write(line)
			else: tempfile.write(line)
		tempfile.close()
		cfile.truncate(0)
		cfile.close()
		with open('experiments.conf', 'w') as cfile, open('experiments_temp.conf', 'r') as tempfile:
			for l in tempfile.readlines():
				cfile.write(l)
		tempfile.close()
		cfile.close()

# adapt these paths
lm_path_dev = "./data/processed_data/elmo/testPipeline.hdf5" 
eval_path = "./data/processed_data/json/testPipeline.json"
output_path = "./data/processed_data/json/outputMyTestPipeline.json"
processed_output_path = "./data/processed_data/json/outputMyTestPipelineProcessed.json"

# if json-files already exist: remove them (otherwise the new elements are appended to the old file)
remove_duplicate_file(lm_path_dev)
remove_duplicate_file(eval_path)
remove_duplicate_file(processed_output_path)

# get json and hdf5 files out of plaintexts
preprocess(eval_path, lm_path_dev)
print("DATA PREPROCESSING: DONE!")

# adapt paths in file exoeriments.conf
config = pyhocon.ConfigFactory.parse_file("experiments.conf")["scientific_best_ner"] 
adapt_conf_paths(lm_path_dev, eval_path, output_path, "scientific_best_ner")
print("ADAPTING CONFIG PATHS: DONE!")

# predict NEs
os.system('python2 write_single.py "scientific_best_ner" ')
print("PREDICTION OF NERS: DONE!")

# process output
order_data(output_path, eval_path, processed_output_path)
print("OUTPUT PROCESSING: DONE!")

