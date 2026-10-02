public class Main {
    public static void main(String[] args) {
        CustomStringBuilder text = new CustomStringBuilder("Hello");
        text.append(", world!");
        System.out.println(text);
        text.undo();
        System.out.println(text);
    }
}
