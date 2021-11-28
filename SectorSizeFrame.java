import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SectorSizeFrame {
    int size;

    JFrame sectorFrame =new JFrame();
    JLabel sectorLabel =new JLabel("Укажите размер сектора в КБ");
    JTextField sectorTextField =new JTextField();
    JButton sectorButton =new JButton("OK");
    public SectorSizeFrame(int diskSize){
        sectorFrame.setSize(200,100);
        sectorFrame.add(sectorLabel, BorderLayout.NORTH);
        sectorFrame.add(sectorTextField, BorderLayout.CENTER);
        sectorFrame.add(sectorButton,BorderLayout.SOUTH);
        sectorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        sectorFrame.setLocationRelativeTo(null);
        sectorFrame.setVisible(true);
        sectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                size=Integer.parseInt(sectorTextField.getText());
                sectorFrame.dispose();
                JFrame frame=new JFrame();
                frame.setSize(900,600);
                frame.add(new FormDisk(size, diskSize).getPanel());
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
