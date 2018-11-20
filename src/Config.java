import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Reader;

/**
 * Parse commandline arguments
 */
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
            this.error = "(The order of params should be the same) "+
            "Usage : -jar part2.jar [-v] [-wt <latexFile>] <superFortranFile>";
        }
    }

    /**
     * 
     * @return True if the user specified a latex file with -wt
     */
    public boolean hasTexFile() {
        return texWriter != null;
    }

    /** 
     * @return The reader for the fortran file to parse
     */
    public Reader getFortranReader() {
        return this.reader;
    }

    /**
     * Open the file in read mode and prepare the attribute reader
     * @param filename filename of the fortran file
     */
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

    /**
     * Open the file in read mode and prepare the attribute texWriter
     * @param filename filename of the latex file
     */
    private void prepareOutputStream(String filename) {
        try{
            texWriter = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
        } catch (IOException e){
            this.error = e.getMessage();
        }
    }

    /**
     * 
     * @return the output stream writer to the latex file
     */
    public OutputStreamWriter getTexStream() {
        return this.texWriter;
    }

    /**
     * Check if the argument list are correct
     * @return True if it is correct
     */
    public Boolean isCorrect() {
        return this.error == null;
    }

    /**
     * 
     * @return String of error null if there is no error
     */
    public String getError() {
        return this.error;
    }

    /**
     * The verbose output is specified by -wt
     * @return True if the user asked for verbose ouput
     */
    public boolean isVerbose() {
        return this.verbose;
    }
}