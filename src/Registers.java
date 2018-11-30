public class Registers {
    private int current = 0;
    public String getNewRegister() {
        return "%" + (++current);
    }

    public void increment() {
        ++current;
    }
}