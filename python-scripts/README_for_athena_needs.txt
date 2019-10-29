SciIE is a model that combines extractors for scientific entities, relations and coreference resolutions and is trained especially on a scientific corpus.
The scientific entities (which are interesting in our case) are Task, Method, Metric, Material, Generic and Other-ScientificTerm.
Pretrained models can be downloaded to make predictions on new data.

To run the tool, you can follow the instructions on the official bitbucket site: https://bitbucket.org/luanyi/scierc/src/master/ (also summarized in the following:)
- install all needed requirements and download the repository
- run the script ./scripts/build_custom_kernels.sh to convert srl_kernels.cc to a corresponding srl_kernels.so
	- you may need to adapt the script depending on your gcc-version
- run the script ./scripts/fetch_required_data.sh (you don't need the 5GB-large glove.840B.300d.txt, the filtered version is sufficient)
- download the Best NER Model from the last section (Best Models) and put it into a logs-folder (logs/scientific_best_ner) 
	-> this folder is the name of the experiment that has to be defined in the prediction step
- adapt the path in experiments.conf: nearly at the end of the file there is the section scientific_best_ner
	- here you have to add 
		- lm_path_dev = "..." (the path to the hdf5-file of your data)
		- eval_path = "..." (the path to the json-file of your data)
		- output_path = "..." (the path to where the predictions will be written)

# CASE 1: you already have your data in the correct json format
- run generate_elmo.py to achieve the corresponding hdf5-file
	- adapt the input and output path in the file
- then run >>> python write_single.py scientific_best_ner


# CASE 2: you want to run the system on paper plaintexts from the database
- install additional requirements:
	wheel, pymysql, pathlib, nltk
- adapt the four paths in db_pipeline.py (they will overwrite the paths in generate_elmo.py and in experiments.conf - section scientific_best_ner)
- make sure you also have the files txt_to_json.py and process_output.py
- then call db_pipeline.py
	- a connection to the database will be established to fetch the plaintexts of the papers, tokenize them and convert them into a json file and to get the corresponding hdf5 file
	- write_single.py will be called with the argument scientific_best_ner to predict the NEs
	- the output json file will be re-written into another json file containing the document ID and a list of all NE-labels together with all words/ word groups in the document that are labeled with it


json-formats:
--> input and output json-files contain one json-element (== one line) for each document, 
	- a document is a list of sentences, where one sentence is a list of all tokens in the sentence
input
	{"clusters": [[],...,[]], "doc_key": "...", "ner": [[],...,[]], "relations": [[],...,[]], "sentences": [[],...,[]]}
output
	{"doc_key": "...", "ner": [[[3, 5, "Metric"],...], "relation": [[[11, 12, 1, 2, "USED-FOR"]]...]}
processed ouput
	{"doc_key": " ", "task": [], "generic": [], "metric": [], "material": [], "otherscientificterm": [], "method": []}
	