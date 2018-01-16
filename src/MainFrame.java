
import com.sun.glass.events.KeyEvent;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Hong
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form Frame
     */
    public MainFrame() {
        initComponents();
        btOpenSentence.requestFocus();
        this.setTitle("Preprocessing sentence program");
        if(labelFile != null) setLabel();
        if(sentenceFile != null) {
            if (sentenceFile.getName().contains("tagged")) {
                taggedFile = sentenceFile;
                setEditorAndTable(taggedFile);
            } else {
                taggedFile = new File("tagged_" + sentenceFile.getName());
                setEditorAndTable(sentenceFile);
            }
        }
    }
    
    public ArrayList<Label> labels; // contains all labels read from file
    public ArrayList<Sentence> sentences; // to save the state of sentence after add labels and categories to
    public ArrayList<Sentence> copySentences; // to save the initial state of sentences (right after read from file)
    public ArrayList<JCheckBox> cbLabel; // contains all check boxs for labels
    static int sizeLabel; // the number of label
    static int sizeSentence; // the number of sentence
    
    // some mode use to operate file
    private static final int OPEN_SENTENCE = 0;
    private static final int OPEN_LABEL = 1;
    
    int currentSentenceNum; // the current sentence (the content of current sentence show in text area)
    String temp; // state of current sentence display in text area, it is used for undo function
    File labelFile = null; // labels file
    File sentenceFile = null; // file contains all sentences need to be tagged
    File taggedFile = null; // file to save to current stage of all sentences in tagging process

//    File labelFile = new File("/home/hong/Desktop/code/ToolTagging/label.txt");
//    File sentenceFile = new File("/home/hong/Desktop/code/ToolTagging/tagged_sentences.csv");
//    File taggedFile = null;
    
    /**
     * regenerate labels(check box) for label panels and assign action for each of them
     */
    public void setLabel() {
        pnLabels.removeAll();
        labels = FileIO.getLabelsFromFile(labelFile);
        sizeLabel = labels.size();
        cbLabel = new ArrayList<>();
        createCbLabel();
        showCbLabel();
        setActionForAllCbLabel();
        pnLabels.doLayout();
    }
    /**
     * set content for text area and table
     * set value of variable temp(use for undo) and copySentence (use to restore the initial sentence)
     * @param file 
     */
    public void setEditorAndTable(File file) {
        sentences = FileIO.getSentecnesFromFile(file);
        copySentences = new ArrayList<>();
        cloneSentence(copySentences, sentences);
        sizeSentence = sentences.size();
        currentSentenceNum = 0;
        tpSentence.setText(sentences.get(currentSentenceNum).content);
        temp = tpSentence.getText();
        loadTable(0);
    }
    
    /**
     * 
     * @param copy to save a copy of source
     * @param source the source contains all Sentences
     */
    public void cloneSentence(ArrayList<Sentence> copy, ArrayList<Sentence> source) {
        int size = source.size();
        for (int i = 0; i < size; i++) {
            Sentence aSentence = source.get(i);
            copy.add(new Sentence(aSentence.content, aSentence.status));
        }
    }
    /**
     * create all check box label (correspond to ArrayList labels(read from label file))
     */
    public void createCbLabel() {
        sizeLabel = labels.size();
        for (int i = 0; i < sizeLabel; i++) {
            JCheckBox aCbLabel = new JCheckBox(labels.get(i).name);
            aCbLabel.setFont(new java.awt.Font("DejaVu Serif", 1, 11));
            cbLabel.add(aCbLabel);
            btgrLabels.add(aCbLabel);
        }
    }
    
    public void showCbLabel() {
        Border labelBorder = BorderFactory.createTitledBorder("Label");
        pnLabels.setBorder(labelBorder);
        GridLayout layout = new GridLayout(0, 6);
        layout.setHgap(0);
        layout.setVgap(0);
        pnLabels.setLayout(layout);

        for (int i = 0; i < sizeLabel; i++) {
            pnLabels.add(cbLabel.get(i));
        }
    }

    /**
     * create table with that contains data from ArrayList sentences
     * @param i the selected row in the created table
     */
    public void loadTable(int i) {
        TableModel dataModel = new DefaultTableModel(loadRowData(), loadCollumName()) {
            public boolean isCellEditable(int row, int column) {
                return false; //This causes all cells to be not editable
            }
        };
        tbSentence.setModel(dataModel);

        tbSentence.getColumnModel().getColumn(0).setPreferredWidth(20);
        tbSentence.getColumnModel().getColumn(1).setPreferredWidth(700);
        tbSentence.getColumnModel().getColumn(2).setPreferredWidth(80);
        tbSentence.setRowSelectionInterval(i, i);
    }
    /**
     * load data for rows of table correspond to ArrayList sentences
     * @return dataInTable
     */
    public String[][] loadRowData() {
        sizeSentence = sentences.size();
        String dataInTable[][] = new String[sizeSentence][3];
        for (int i = 0; i < sizeSentence; i++) {
            Sentence aSentence = sentences.get(i);
            dataInTable[i][0] = i + 1 + "";
            dataInTable[i][1] = aSentence.content;
            if(!"consider".equals(aSentence.status) && !"".equals(aSentence.status)){
                dataInTable[i][2] = "comment";
            } else{
                dataInTable[i][2] = aSentence.status;
            }
        }
        return dataInTable;
    }
    /**
     * set collumName for the table
     * @return 
     */
    public String[] loadCollumName() {
        return new String[]{"No.", "Sentence", "Status"};
    }

    public void operateFile(String title, int type) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        int approveOrNot = -1;
        switch (type) {
            case OPEN_LABEL:
            case OPEN_SENTENCE:
                approveOrNot = chooser.showOpenDialog(null);
                break;
        }

        if (approveOrNot == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            switch (type) {
                case OPEN_LABEL:
                    labelFile = file;
                    setLabel();
                    break;
                case OPEN_SENTENCE:
                    // from name of selected file decide the way to read file
                    //(0: source file(txt), 1: tagged file(after tag some sentence))
                    if (file.getName().contains("tagged")) {
                        taggedFile = file;
                        setEditorAndTable(taggedFile);
                    } else {
                        sentenceFile = file;
                        taggedFile = new File("tagged_" + file.getName());
                        setEditorAndTable(sentenceFile);
                    }
                    btOpenLabel.requestFocus();
                    break;
            }
        }
    }
    
    /**
     * when click to another sentence in table
     * save the current sentence, and update text area(tpSentence) to the selected sentence
     * update the current text from text area to temp
     */
    public void actWithTable() {
        saveSentence(currentSentenceNum);
        currentSentenceNum = tbSentence.getSelectedRow();
        Sentence asentence = sentences.get(currentSentenceNum);
        loadTable(currentSentenceNum);
        tpSentence.setText(asentence.content);
        temp = tpSentence.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btgrCategories = new javax.swing.ButtonGroup();
        btgrLabels = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        pnLabels = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btUndo = new javax.swing.JButton();
        btReStore = new javax.swing.JButton();
        btBack = new javax.swing.JButton();
        btNext = new javax.swing.JButton();
        btConsider = new javax.swing.JButton();
        btStatus = new javax.swing.JButton();
        btRemove = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbSentence = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        btOpenLabel = new javax.swing.JButton();
        btOpenSentence = new javax.swing.JButton();
        btAddLabel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tpSentence = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        javax.swing.GroupLayout pnLabelsLayout = new javax.swing.GroupLayout(pnLabels);
        pnLabels.setLayout(pnLabelsLayout);
        pnLabelsLayout.setHorizontalGroup(
            pnLabelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 940, Short.MAX_VALUE)
        );
        pnLabelsLayout.setVerticalGroup(
            pnLabelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 169, Short.MAX_VALUE)
        );

        jPanel1.add(pnLabels);

        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        btUndo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btUndo.setText("Undo");
        btUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btUndoActionPerformed(evt);
            }
        });
        jPanel2.add(btUndo);

        btReStore.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btReStore.setText("Restore");
        btReStore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btReStoreActionPerformed(evt);
            }
        });
        jPanel2.add(btReStore);

        btBack.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btBack.setText("Back");
        btBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btBackActionPerformed(evt);
            }
        });
        jPanel2.add(btBack);

        btNext.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btNext.setText("Next");
        btNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNextActionPerformed(evt);
            }
        });
        jPanel2.add(btNext);

        btConsider.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btConsider.setText("Un/Consider");
        btConsider.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConsiderActionPerformed(evt);
            }
        });
        jPanel2.add(btConsider);

        btStatus.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btStatus.setText("Status");
        btStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStatusActionPerformed(evt);
            }
        });
        jPanel2.add(btStatus);

        btRemove.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btRemove.setText("Remove");
        btRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRemoveActionPerformed(evt);
            }
        });
        jPanel2.add(btRemove);

        tbSentence.setFont(new java.awt.Font("DejaVu Serif", 0, 12)); // NOI18N
        tbSentence.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbSentenceMousePressed(evt);
            }
        });
        tbSentence.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbSentenceKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tbSentence);

        jPanel4.setLayout(new java.awt.GridLayout(1, 0));

        btOpenLabel.setText("Chose File (Label)");
        btOpenLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOpenLabelActionPerformed(evt);
            }
        });
        jPanel4.add(btOpenLabel);

        btOpenSentence.setText("Choose Data File");
        btOpenSentence.setToolTipText("");
        btOpenSentence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOpenSentenceActionPerformed(evt);
            }
        });
        jPanel4.add(btOpenSentence);

        btAddLabel.setText("Add/ Remove Label");
        btAddLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAddLabelActionPerformed(evt);
            }
        });
        jPanel4.add(btAddLabel);

        jLabel1.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel1.setText("https://github.com/hongnguyenhuu96/ToolTagging");

        tpSentence.setFont(new java.awt.Font("DejaVu Serif", 0, 12)); // NOI18N
        tpSentence.setToolTipText("");
        jScrollPane1.setViewportView(tpSentence);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * set text of text area (tpSentence) to the previous state
     * @param evt 
     */
    private void btUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btUndoActionPerformed
        if (taggedFile == null) {
            return;
        }
        // temp is the previous state and is updated when adding label
        tpSentence.setText(temp);
    }//GEN-LAST:event_btUndoActionPerformed
    /**
     * restore the state of current sentence to the first state (before modify anything)
     * @param evt 
     */
    private void btReStoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btReStoreActionPerformed
        if (taggedFile == null) {
            return;
        }
        // sentences: contains modified sentences
        // copySentences: contains pured sentences (read from file)
        Sentence a = copySentences.get(currentSentenceNum);
        Sentence b = sentences.get(currentSentenceNum);
        tpSentence.setText(a.content);
        b.status = a.status;
        saveSentence(currentSentenceNum);
        loadTable(currentSentenceNum);
        temp = tpSentence.getText();
    }//GEN-LAST:event_btReStoreActionPerformed
    /**
     * when press next, save the current sentence state if the current sentence is modified
     * update text area (tpSentence) and table to the next sentence 
     * @param evt 
     */
    private void btNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btNextActionPerformed
        if (taggedFile == null) {
            return;
        }
        if (!temp.equals(tpSentence.getText())) {
            saveSentence(currentSentenceNum);
        }
        if (currentSentenceNum == sentences.size() - 1) {
            loadTable(currentSentenceNum);
            return;
        }
        currentSentenceNum++;
        Sentence asentence = sentences.get(currentSentenceNum);
        loadTable(currentSentenceNum);
        tpSentence.setText(asentence.content);
        temp = tpSentence.getText();
    }//GEN-LAST:event_btNextActionPerformed
    
    /**
     * when press back, save the current sentence state if the current sentence is modified
     * update text area (tpSentence) and table to the back sentence 
     * @param evt 
     */
    private void btBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBackActionPerformed
        if (taggedFile == null) {
            return;
        }
        // update current sentence state
        if (!temp.equals(tpSentence.getText())) {
            saveSentence(currentSentenceNum);
        }
        if (currentSentenceNum == 0) {
            loadTable(currentSentenceNum);
            return;
        }
        // change content of text area and update table
        currentSentenceNum--;
        Sentence asentence = sentences.get(currentSentenceNum);
        loadTable(currentSentenceNum);
        tpSentence.setText(asentence.content);
        temp = tpSentence.getText();
    }//GEN-LAST:event_btBackActionPerformed
    
    /**
     * when press consider button the status of current sentence should change to consider or unconsider
     * and save new state of current sentence and update table
     * @param evt 
     */
    private void btConsiderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btConsiderActionPerformed
        if (taggedFile == null) {
            return;
        }
        Sentence aSentence = sentences.get(currentSentenceNum);
        if (aSentence.status.equals("")) {
            aSentence.status = "consider";
        } else {
            aSentence.status = "";
        }
        FileIO.writeLabeledSentencesFile(taggedFile, sentences);
        loadTable(currentSentenceNum);
    }//GEN-LAST:event_btConsiderActionPerformed
    
    /**
     * when click to "add label" button -> create new frame
     * if the label file is specified
     * it shows the current labels in label file to modify
     * @param evt 
     */
    private void btAddLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAddLabelActionPerformed
        if (labelFile == null) {
            JOptionPane.showMessageDialog(rootPane, "You must choose label file first");
            return;
        }
        ChangeLabel changeLabel = new ChangeLabel();
        changeLabel.setVisible(true);
        changeLabel.setLabelFile(labelFile);
        changeLabel.setMainFrame(this);
    }//GEN-LAST:event_btAddLabelActionPerformed
    
   
    /**
     * call operate file with mode open_label
     * @param evt 
     */
    private void btOpenLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOpenLabelActionPerformed
        operateFile("Open a file contains your all labels", OPEN_LABEL);
    }//GEN-LAST:event_btOpenLabelActionPerformed
    
    /**
     * call operateFile with mode open_sentence
     * @param evt 
     */
    private void btOpenSentenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOpenSentenceActionPerformed
        operateFile("Open a file contains your all sentences", OPEN_SENTENCE);
    }//GEN-LAST:event_btOpenSentenceActionPerformed
    
   
    /**
     * call actWithTable() when click to the table
     * @param evt 
     */
    private void tbSentenceMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbSentenceMousePressed
        actWithTable();
    }//GEN-LAST:event_tbSentenceMousePressed
    
    /**
     * update text area(tpSentence) when the position of
     * selected sentence in table is changed by press key (up, down, enter)  
     * @param evt 
     */
    private void tbSentenceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbSentenceKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_DOWN){
            if(currentSentenceNum == sentences.size() - 1) return;
            currentSentenceNum++;
            Sentence a = sentences.get(currentSentenceNum);
            tpSentence.setText(a.content);
            temp = tpSentence.getText();
        }
        if(evt.getKeyCode() == KeyEvent.VK_UP){
            if(currentSentenceNum == 0) return;
            currentSentenceNum--;
            Sentence a = sentences.get(currentSentenceNum);
            tpSentence.setText(a.content);
            temp = tpSentence.getText();
        }
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            currentSentenceNum = (currentSentenceNum + 1) % sentences.size();
            Sentence a = sentences.get(currentSentenceNum);
            tpSentence.setText(a.content);
            temp = tpSentence.getText();
        }
    }//GEN-LAST:event_tbSentenceKeyPressed
    /**
     * add or edit status/comment to the current sentence
     * @param evt 
     */
    private void btStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btStatusActionPerformed
        ChangeStatusOfSentence changeStatus = new ChangeStatusOfSentence();
        changeStatus.setMainFrame(this);
        changeStatus.setStatus(sentences.get(currentSentenceNum).status);
        changeStatus.setVisible(true);
    }//GEN-LAST:event_btStatusActionPerformed

    private void btRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRemoveActionPerformed
        if (taggedFile == null) {
            return;
        }
        sentences.remove(currentSentenceNum);
        sizeSentence = sentences.size();
        if(currentSentenceNum == sentences.size()){
            currentSentenceNum = currentSentenceNum -1;
        }
        Sentence asentence = sentences.get(currentSentenceNum);
        loadTable(currentSentenceNum);
        tpSentence.setText(asentence.content);
        temp = tpSentence.getText();
    }//GEN-LAST:event_btRemoveActionPerformed
    
    /**
     * use save the status string pass from ChangeStatusOfSentence frame via mainFrame
     * @param status 
     */
    protected void saveStatus(String status){
        if(!sentences.get(currentSentenceNum).status.trim().equals(status.trim())){
            sentences.get(currentSentenceNum).status = status.trim();
            FileIO.writeLabeledSentencesFile(taggedFile, sentences);
            loadTable(currentSentenceNum);
        }
    }
    
    /**
     * set action for all labels check box when click to
     * It will trigger function labelSentenceBySymbol to surround the selected text
     * in text area by symbol of the label
     */
    public void setActionForAllCbLabel() {
        int size = cbLabel.size();
        for (int i = 0; i < size; i++) {
            JCheckBox currentLabel = cbLabel.get(i);
            String symbol = labels.get(i).symbol;
            currentLabel.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent evt) {
                    if (currentLabel.isSelected()) {
                        labelSentenceBySymbol(symbol);
                    }
                    btgrLabels.clearSelection();
                }
            });
        }
    }

    /**
     * add label to selected text in text area: tpSentence
     * play -> <act>play</act>
     * @param typeLabel label like <act></act> for action, <prc></prc> for price
     */
    private void labelSentenceBySymbol(String typeLabel) {
        temp = tpSentence.getText();
        int selectionStart = tpSentence.getSelectionStart();
        int selectionEnd = tpSentence.getSelectionEnd();
        if (selectionStart == selectionEnd) {
            return;
        }
        while (temp.charAt(selectionStart) == ' ') {
            selectionStart++;
        }
        while (temp.charAt(selectionEnd - 1) == ' ') {
            selectionEnd--;
        }
        if (selectionStart == selectionEnd) {
            return;
        }
        StringBuilder strBuilder = new StringBuilder(tpSentence.getText());
        strBuilder.replace(selectionStart, selectionEnd, "<" + typeLabel + ">" + tpSentence.getSelectedText().trim() + "</" + typeLabel + ">");
        tpSentence.setText(strBuilder.toString());
        tpSentence.requestFocus();
        // set cusor to the next word
        int caretPosition = selectionEnd + 5 + typeLabel.length() * 2 + 1;
        if (caretPosition < tpSentence.getText().length()) {
            tpSentence.setCaretPosition(caretPosition);
        }
    }
    /**
     * set content of text area to sentences[i](only content and category) and save to file
     * @param i index of sentence in ArrayList<Sentence> sentences that need to update
     */
    public void saveSentence(int i) { // save sentence to arraylist and file
        Sentence a = sentences.get(i);
        a.content = tpSentence.getText();
        FileIO.writeLabeledSentencesFile(taggedFile, sentences);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        try {
            //</editor-fold>
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAddLabel;
    private javax.swing.JButton btBack;
    private javax.swing.JButton btConsider;
    private javax.swing.JButton btNext;
    private javax.swing.JButton btOpenLabel;
    private javax.swing.JButton btOpenSentence;
    private javax.swing.JButton btReStore;
    private javax.swing.JButton btRemove;
    private javax.swing.JButton btStatus;
    private javax.swing.JButton btUndo;
    private javax.swing.ButtonGroup btgrCategories;
    private javax.swing.ButtonGroup btgrLabels;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnLabels;
    private javax.swing.JTable tbSentence;
    private javax.swing.JEditorPane tpSentence;
    // End of variables declaration//GEN-END:variables
}
