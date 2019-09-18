import json

def order_data(json_pred, json_sents, json_processed):
	with open(json_pred, 'r') as json_pred, open(json_sents, 'r') as json_sents:
		jsents = [json.loads(jsonline) for jsonline in json_sents.readlines()] # json.load(json_sents)
		# jpred = json.load(json_pred)
		jpredictions = [json.loads(jsonline) for jsonline in json_pred.readlines()]
		for j, jpred in enumerate(jpredictions):
		#	print(jpred["doc_key"])
			predicted_ners_sents = jpred["ner"]
			labels = ["task", "method", "metric", "material", "otherscientificterm", "generic"]
			ners = {l: [] for l in labels}
			for i, sent in enumerate(predicted_ners_sents):
				for ner in sent:
					ners[ner[2].encode("utf-8").lower()].append([jsents[j]["sentences"][i][word].encode("utf-8") for word in range(ner[0], ner[1]+1)])
		#	for key in ners.keys(): print key + ": \t" + str(ners[key])
		#	print "-----------------------------------------------------------"
			write_processed_json(ners, json_processed, jpred["doc_key"])

		
def write_processed_json(ners, output_path, doc_key):
	with open(output_path, "a") as out:
		data = {}
		data["doc_key"] = doc_key
		for ner in ners.keys():
			data[ner] = ners[ner]
		json.dump(data, out)
		out.write("\n")
	out.close()
		
'''
json_pred = "./data/processed_data/json/outputMyTestFiles.json"
json_sents = "./data/processed_data/json/myTestFiles.json"
json_processed = "./data/processed_data/json/processed/myTestFilesProcessed.json"

order_data(json_pred, json_sents, json_processed)

#write_processed_json(ner_dict, json_processed
'''
