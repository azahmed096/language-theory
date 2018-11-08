import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Reader;

public class Config {
    private OutputStreamWriter texWriter;
    private String error;
    private Boolean verbose = false;
    private Reader reader;

    public Config(String[] argv) {
        int filePosition = 0;
        try {
            if (argv[0].equals("-v")) {
                verbose = true;
                ++filePosition;
            }
            if (argv[filePosition].equals("-wt")) {
                prepareOutputStream(argv[++filePosition]);
                ++filePosition;
            }
            prepareFortranReader(argv[filePosition]);
        } catch (ArrayIndexOutOfBoundsException e) {
            this.error = "Usage : -jar part2.jar [-v] [-wt <latexFile>] <superFortranFile>";
        }
    }

    public boolean hasTexFile() {
        return texWriter != null;
    }

    public Reader getFortranReader() {
        return this.reader;
    }

    private void prepareFortranReader(String filename) {
        try {
            FileInputStream stream = new FileInputStream(filename);
            this.reader = new InputStreamReader(stream, "UTF-8");
        } catch (FileNotFoundException e) {
            this.error = "File not found " + filename;
        } catch (IOException e) {
            this.error = e.getMessage();
        }
    }

    private void prepareOutputStream(String filename) {
        try{
            texWriter = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
        } catch (IOException e){
            this.error = e.getMessage();
        }
    }

    public OutputStreamWriter getTexStream() {
        return this.texWriter;
    }

    public Boolean isCorrect() {
        return this.error == null;
    }

    public String getError() {
        return this.error;
    }

    public boolean isVerbose() {
        return this.verbose;
    }
}