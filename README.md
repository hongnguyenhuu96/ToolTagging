
# Installation
- Link [download](https://drive.google.com/open?id=1YowOGQM2oa5nElHc63jySshBcTMLuiEm)

- Open the containing folder, and run in the terminal: `java -jar PreprocessSentence.jar`
![image](https://user-images.githubusercontent.com/12449250/55288025-ac968680-53db-11e9-9729-60dd83f3f8fa.png)


![image](https://user-images.githubusercontent.com/12449250/55287944-3fcebc80-53da-11e9-85e8-9884a01768b8.png)

---

## 1. Input file
- **File sentence**: containing text data needed to tag
- **File label**: containing labels

## 2. File input format

#### File data (example file: `sentences.csv`)
| Sentence | Status/Comment |
| :--------: | :--------: |
| I want to buy a new car    | has intent |



#### File label (example file: `label.txt`)

| Label | Abbreviation |
| :----:|:------------:|
| Object| obj |
| Action| act |


## 3. Flow
1. Choose label file (example file: `label.txt`)
2. Choose file data(example file: `sentences.csv`)
3. Click `Add/Remove Label` button to change the label (the content of original label file will be rewrited)
4. Start tagging


| Button | Action |
| -------- | -------- |
| **undo** |back to the previous state text area (only 1 step back) |
| **restore** | back to the inital state of text area |
| **back** | back a row in table (previous sentence) |
| **next** | next a row in table (next sentence)|
| **un/consider** |  mark or unmark "consider" status for the current data row|
| **status** | - show and edit status/comment to explain more. <br> - All comments must be written in only 1 row, do not enter in the text box when typing, it will break the structure of csv file |
|**rm label** | - All label in the selected text will be removed. <br> - For example: you select `<prc>5000 dong </prc>`, then click **rm label**. `<prc>5000 dong </prc>` -> `5000 dong` |
| **remove** | Remove the current row data (Be careful when deciding to remove a row because It cannot be restored) |


## 4. Result
 - The result file after tagging will be generated and saved in file `tagged_*.csv` in the same folder with the tool

 - You must open this "tagged_*.csv" file to continue in the next time.
If you try to open the original file, the tagged file will be regenerated and rewrited.

- For example: `sentences.csv` is the original file, and `tagged_sentences.csv` is the tagged file. You must open `tagged_sentences.csv` in the next time to continue to tag without losing all tagged data from the previous time. <br> (If you try to open `sentences.csv` as the input `file sentence`, the `tagged_sentences.csv` file will be regenerated and replace the existed `tagged_sentences.csv` file)
