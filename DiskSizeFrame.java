import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DiskSizeFrame extends JFrame {
    int size;

    JFrame diskFrame=new JFrame();
    JLabel diskLabel=new JLabel("Укажите размер диска в КБ");
    JTextField diskTextField=new JTextField();
    JButton diskButton=new JButton("OK");
    public DiskSizeFrame(){
        diskFrame.setSize(200,100);
        diskFrame.add(diskLabel,BorderLayout.NORTH);
        diskFrame.add(diskTextField, BorderLayout.CENTER);
        diskFrame.add(diskButton,BorderLayout.SOUTH);
        diskFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        diskFrame.setLocationRelativeTo(null);
        diskFrame.setVisible(true);
        diskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                size=Integer.parseInt(diskTextField.getText());
                diskFrame.dispose();
                SectorSizeFrame sectorSizeFrame = new SectorSizeFrame(size);
            }
        });
    }

}
