import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class FormDisk {
    private JPanel panel;
    private JTree tree;
    private JButton buttonAdd;
    private JButton buttonRemove;
    private JTextField textField;
    private JButton buttonFolder;
    private JButton buttonCopy;
    private JButton buttonPaste;
    private JButton buttonMove;
    private final MyPanel drawPanel;
    private final Vector<Integer> emptySectors;
    private final ArrayList<File> files = new ArrayList<>();
    private DefaultMutableTreeNode bufferNode;

    public FormDisk(int sectorSize, int diskSize) {
        emptySectors = new Vector<>();
        drawPanel = new MyPanel(sectorSize, diskSize);
        for (int i = 0; i < drawPanel.sectors.size(); i++) {
            emptySectors.add(i);
        }
        panel.add(drawPanel);
        initTree();

        /**
         * Добавление файла
         */
        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File();
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                //Проверка на "Файл в файл"
                if (!selectedNode.getAllowsChildren()) {
                    JOptionPane.showMessageDialog(drawPanel, "Нельзя!!!");
                    return;
                }
                boolean flag = addToDisk(file);
                //Проверка на возможность добавления файла
                if (!flag) return;
                files.add(file);
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file, false);
                selectedNode.add(newNode);
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                model.reload();
                drawPanel.repaint();
            }
        });
        /**
         * Удаление файла/папки
         */
        buttonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                //Обход дерева для удаления всех дочерних элементов
                Enumeration<TreeNode> children = selectedNode.depthFirstEnumeration();
                LinkedList<DefaultMutableTreeNode> forDelete = new LinkedList<>();
                while (children.hasMoreElements()) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.asIterator().next();
                    forDelete.add(child);
                }
                forDelete.forEach(node -> delFromDisk(node));
                drawPanel.repaint();
            }
        });
        /**
         * Добавление папки
         */
        buttonFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                //Проверка на "Файл в файл"
                if (!selectedNode.getAllowsChildren()) {
                    JOptionPane.showMessageDialog(drawPanel, "Нельзя!!!");
                    return;
                }
                File file = new File();
                addFolder(file);
                files.add(file);
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file);
                selectedNode.add(newNode);
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                model.reload();
                drawPanel.repaint();
            }
        });
        /**
         * Копирование файла/каталога
         */
        buttonCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bufferNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                buttonMove.setEnabled(true);
                buttonPaste.setEnabled(true);
            }
        });
        /**
         * Перемещение файла/каталога
         */
        buttonMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                //Проверка на "Файл в файл"
                if (!selectedNode.getAllowsChildren()) {
                    JOptionPane.showMessageDialog(drawPanel, "Нельзя!!!");
                    return;
                }
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                model.removeNodeFromParent(bufferNode);
                selectedNode.add(bufferNode);
                buttonPaste.setEnabled(false);
                buttonMove.setEnabled(false);
                model.reload();
                drawPanel.repaint();
            }
        });
        /**
         * Вставка копируемого файла/каталога
         */
        buttonPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                //Проверка на "Файл в файл"
                if (!selectedNode.getAllowsChildren()) {
                    JOptionPane.showMessageDialog(drawPanel, "Нельзя!!!");
                    return;
                }
                //Проверка на возможность копирования
                Enumeration<TreeNode> check = bufferNode.depthFirstEnumeration();
                int sizeFile = 0;
                while (check.hasMoreElements()) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) check.asIterator().next();
                    File tmp = (File) child.getUserObject();
                    sizeFile += tmp.list.size();
                }
                if (sizeFile > emptySectors.size()) {
                    JOptionPane.showMessageDialog(drawPanel, "Недостаточно места");
                    return;
                }
                DefaultMutableTreeNode newNode = cloneNode(bufferNode);
                selectedNode.add(newNode);
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                buttonMove.setEnabled(false);
                buttonPaste.setEnabled(false);
                model.reload();
                drawPanel.repaint();
            }

        });
        /**
         * Смена выбора файла/каталога
         */
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            DefaultMutableTreeNode before = null;

            public void valueChanged(TreeSelectionEvent e) {
                //Перекрашивание прошлой выборки с помощью обхода дерева
                if (before != null) {
                    Enumeration<TreeNode> childrenBefore = before.depthFirstEnumeration();
                    while (childrenBefore.hasMoreElements()) {
                        DefaultMutableTreeNode child = (DefaultMutableTreeNode) childrenBefore.asIterator().next();
                        for (File file : files) {
                            if (child.getUserObject() == file) {
                                for (int i : file.list) {
                                    drawPanel.sectors.set(i, 1);
                                }
                            }
                        }
                    }
                }
                //Перекрашивание выбор с помощью обхода дерева
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (node == null) return;
                before = node;
                Enumeration<TreeNode> children = node.depthFirstEnumeration();
                while (children.hasMoreElements()) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.asIterator().next();
                    for (File file : files) {
                        if (child.getUserObject() == file) {
                            for (int i : file.list) {
                                drawPanel.sectors.set(i, 2);
                            }
                        }
                    }
                }
                drawPanel.repaint();
            }

        });

    }

    /**
     * Возвращаем панель на форму
     *
     * @return панель
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * Метод добавления файла
     *
     * @param file добавляемый файл
     * @return бул, для определения возможности добавления
     */
    public boolean addToDisk(File file) {
        int sizeFile = 1 + (int) (Math.random() * 10);
        System.out.println("size = " + sizeFile);
        file.name = textField.getText();
        if (sizeFile > emptySectors.size()) {
            JOptionPane.showMessageDialog(drawPanel, "Недостаточно места");
            return false;
        }
        for (int i = 0; i < sizeFile; i++) {
            while (true) {
                int rnd = (int) (Math.random() * drawPanel.size);
                if (emptySectors.contains(rnd)) {
                    System.out.println(rnd);
                    drawPanel.sectors.set(rnd, 1);
                    Integer iOb = rnd;
                    emptySectors.remove(iOb);
                    file.list.add(rnd);
                    break;
                }
            }
        }
        System.out.println();
        return true;
    }

    /**
     * Метод добавления папки
     *
     * @param file файл-привязка
     * @return возможность добавления
     */
    public boolean addFolder(File file) {
        int sizeFile = 1;
        System.out.println("size = " + sizeFile);
        file.name = textField.getText();
        if (sizeFile > emptySectors.size()) {
            JOptionPane.showMessageDialog(drawPanel, "Недостаточно места");
            return false;
        }

        while (true) {
            int rnd = (int) (Math.random() * drawPanel.size);
            if (emptySectors.contains(rnd)) {
                System.out.println(rnd);
                drawPanel.sectors.set(rnd, 1);
                Integer iOb = rnd;
                emptySectors.remove(iOb);
                file.list.add(rnd);
                break;
            }
        }

        System.out.println();
        return true;
    }

    /**
     * Метод удаления файла/каталога
     *
     * @param selectedNode выбранный файл/каталог
     */
    public void delFromDisk(DefaultMutableTreeNode selectedNode) {
        if (selectedNode != tree.getModel().getRoot()) {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            for (File file : files) {
                if (selectedNode.getUserObject() == file) {
                    for (int i : file.list) {
                        drawPanel.sectors.set(i, 0);
                        emptySectors.add(i);
                    }
                    files.remove(file);
                    break;
                }
            }
            model.removeNodeFromParent(selectedNode);
            model.reload();
        }
    }

    /**
     * Правильная инициализация дерева
     */
    public void initTree() {
        //То, что сделало возможным отдельное создание каталогов
        tree.setCellRenderer(new MyTreeCellRenderer());
        //Выбор, только 1 обьекта
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("C:");
        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);
    }

    /**
     * Метод клонирования node(стандартный не работает как надо)
     *
     * @param node клонирумая ветвь
     * @return новая ветвь
     */
    public DefaultMutableTreeNode cloneNode(DefaultMutableTreeNode node) {
        File newFile = new File();
        int size = 0;
        newFile.name = node.getUserObject().toString();
        File oldFile = (File) node.getUserObject();
        size = oldFile.list.size();
        for (int i = 0; i < size; i++) {
            while (true) {
                int rnd = (int) (Math.random() * drawPanel.size);
                if (emptySectors.contains(rnd)) {
                    System.out.println(rnd);
                    drawPanel.sectors.set(rnd, 1);
                    Integer iOb = rnd;
                    emptySectors.remove(iOb);
                    newFile.list.add(rnd);
                    break;
                }
            }
            files.add(newFile);
        }
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newFile, node.getAllowsChildren());
        for (int iChildren = node.getChildCount(), i = 0; i < iChildren; i++) {
            newNode.add((DefaultMutableTreeNode) cloneNode((DefaultMutableTreeNode) node.getChildAt(i)));
        }
        return newNode;
    }
}