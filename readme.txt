File input:
	- File sentence: contains text data need to tag label
	- File label: contains label

File input format:
File data (sentence | status or comment (optional))
==================
I want to buy a new car | has intent
==================

File label (Name of label: Sign)
==================
Object: obj
Action: act
=================

View example
	- data file:"sentences.csv"
	- label file:"label.txt"
above in the code folder for more details

Flow:
1. Choose label file (labels are pre-defined in this file as my "label.txt" file)
2. Choose file data(file contain text data need to tag save in "csv" format as my "sentences.csv")
3. Change the label if you want
4. Start tagging
	-	undo: back to the previous state text area (only 1 step back)
	- restore: back to the inital state of text area
	- back: back a row in table
	- next: next a row in table
	- Un/Consider: mark or unmark "consider" status for the current data row;
	- Status: show and edit status/comment to explain more
		because comments are save in 1 row in csv output file
		all comments must be written in only 1 row
		do not enter here to split comments
	- Remove: Remove the current row data (the removed row can't go back -> careful)

The results of tagging process will be automatically saved in the generated file:"tagged_*.csv" in the same folder with the tool
You must open this "tagged_*" file to continue in the next time
If you try to open the original file, the tagged file will be regenerate and rewrite.

For example "sentences.csv" is the original file and "tagged_sentences.csv" is the tagged file.
You must open "tagged_sentences.csv" for the next time if you want to continue to tag without losing tagged data of the previous time.