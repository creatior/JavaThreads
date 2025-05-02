import javax.swing.*;
import java.awt.*;

public class GameLog extends JScrollPane {
    private final JTextArea textArea;

    public GameLog() {
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        setViewportView(textArea);

        // Устанавливаем предпочтительный размер для лога
        setPreferredSize(new Dimension(300, 0));

        // Добавляем границу для визуального отделения
        setBorder(BorderFactory.createTitledBorder("Ход игры"));
    }

    public void addMessage(String message) {
        textArea.append(message + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public void clear(){
        textArea.setText("");
        textArea.setCaretPosition(0);
    }
}