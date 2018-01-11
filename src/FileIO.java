
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * For write and read file purpose
 * @author hong
 */
public class FileIO {

    /**
     *
     * get all labels in file
     * 
     * @param file the file contains all labels
     * @return all labels in the file
     */
    public static ArrayList<Label> getLabelsFromFile(File file) {
        ArrayList<Label> label = new ArrayList<Label>();
        FileInputStream fileInPutStream = null;
        try {
            fileInPutStream = new FileInputStream(file);
            Reader reader = new java.io.InputStreamReader(fileInPutStream, "utf8");
            BufferedReader br = new BufferedReader(reader);
            Scanner sc = new Scanner(br);
            while (sc.hasNext()) {
                String nextLine = sc.nextLine();
                if (!"".equals(nextLine)) {
                    String[] labelComponents = nextLine.trim().split(":");
                    Label alabel = new Label(labelComponents[0].trim(), labelComponents[1].trim());
                    label.add(alabel);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileInPutStream.close();
            } catch (IOException ex) {
                Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return label;
    }

    /**
     * get all sentence from file
     * 
     * @param file name of the file contains sentence (query)
     * @return an ArrayList of Sentences from file
     */
    public static ArrayList<Sentence> getSentecnesFromFile(File file) {
        ArrayList<Sentence> sentences = new ArrayList<>();
        try {
            FileInputStream fileInputStream = null;
            fileInputStream = new FileInputStream(file);
            Reader reader = new java.io.InputStreamReader(fileInputStream, "utf8");
            CSVReader csvReader = new CSVReader(reader);
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                if(nextRecord.length == 1){
                    sentences.add(new Sentence(nextRecord[0], ""));
                }else{
                    sentences.add(new Sentence(nextRecord[0], nextRecord[1]));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sentences;
    }
    /**
     * get content of any file
     * 
     * @param file file that want to get content
     * @return content of the file
     */
    public static String getContent(File file) {
        String content = "";
        FileInputStream fileInPutStream = null;
        try {
            fileInPutStream = new FileInputStream(file);
            Reader reader = new java.io.InputStreamReader(fileInPutStream, "utf8");
            BufferedReader br = new BufferedReader(reader);
            Scanner sc = new Scanner(br);
            while (sc.hasNext()) {
                content += sc.nextLine() + "\n";
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileInPutStream.close();
            } catch (IOException ex) {
                Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return content;
    }

    /**
     *
     * write content to specified file
     * 
     * @param file a file want to write
     * @param content a String contains the content to write
     * @return the content of the file
     */
    public static String writeFile(File file, String content) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            Writer writer = new java.io.OutputStreamWriter(fileOutputStream, "utf8");
            BufferedWriter bw = new BufferedWriter(writer);
            PrintWriter out = new PrintWriter(bw);

            out.print(content);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return content;
    }

    /**
     *
     * write some labels to specified file
     * 
     * @param file the file contains all labels
     * @param label the label list want to write to label file
     * @return the content of the file
     */
    public static String writeLabelsFile(File file, ArrayList<Label> label) {
        String content = "";
        int size = label.size();
        for (int i = 0; i < size; i++) {
            Label aLabel = label.get(i);
            content += aLabel.name + ":" + aLabel.symbol + "\n";
        }
        writeFile(file, content);
        return content;
    }

    /**
     * 
     * write file to save the current state, (to continue in the next time (after quit program))
     * 
     * @param file contains labeled sentences with comment from user when tagging
     * @param sentence (content, status/comment)
     * @return the content of the file
     */
    public static void writeLabeledSentencesFile(File file, ArrayList<Sentence> sentences) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            Writer writer = new java.io.OutputStreamWriter(fileOutputStream, "utf8");
            CSVWriter csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END
            );
            for (int i = 0; i < sentences.size(); i++) {
                csvWriter.writeNext(new String[]{sentences.get(i).getContent(), sentences.get(i).getStatus()});
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
