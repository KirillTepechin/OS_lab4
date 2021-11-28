import javax.swing.*;
import java.awt.*;
import java.util.Vector;


public class MyPanel extends JPanel {
    int sectorSize;
    int diskSize;
    Vector<Integer> sectors = new Vector<>();
    int size;

    public MyPanel(int sectorSize, int diskSize) {
        this.sectorSize = sectorSize;
        this.diskSize = diskSize;
        size = diskSize / sectorSize;
        for (int i = 0; i < size; i++) {
            sectors.add(0);
        }
    }

    /**
     * Отрисовка, исходя из состояния кластера
     */
    public void paintComponent(Graphics g) {
        int x = 30;
        int y = 30;
        super.paintComponent(g);
        for (int i = 0; i < size; i++) {
            if (sectors.get(i) == 0) g.setColor(Color.gray);
            if (sectors.get(i) == 1) g.setColor(Color.blue);
            if (sectors.get(i) == 2) g.setColor(Color.red);

            if ((i + 1) % 50 == 0) {
                g.fillRect(x, y, 10, 10);
                x = 30;
                y += 15;
            } else {
                g.fillRect(x, y, 10, 10);
                x += 12;
            }
        }
    }
}
