import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;

public class GUI extends JFrame{
    private JTextArea area = new JTextArea(20,120);
    private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
    private String currentFile = "Untitled";
    private boolean changed = false;

    private void saveFileAs()
    {
        if(dialog.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
            saveFile(dialog.getSelectedFile().getAbsolutePath());
    }
    private void saveOld()
    {
        if(dialog.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
            saveFile(dialog.getSelectedFile().getAbsolutePath());
    }
    private void readInFile(String fileName)
    {
        try{
            FileReader r = new FileReader(fileName);
            area.read(r,null);
            r.close();
            currentFile=fileName;
            setTitle(currentFile);
            changed = false;
        }
        catch(IOException e){
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this,"Editor can't find the file called "+fileName);

        }

    }
    private void saveFile(String fileName){
        try{
            FileWriter w = new FileWriter(fileName);
            area.write(w);
            w.close();
            currentFile = fileName;
            setTitle(currentFile);
            changed = false;
            Save.setEnabled(false);
        }
        catch(IOException e){

        }
    }

    Action Open = new AbstractAction("Open", new ImageIcon("open.gif")){

        public void actionPerformed(ActionEvent e){
            saveOld();
            if(dialog.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
                readInFile(dialog.getSelectedFile().getAbsolutePath());
            }
            SaveAs.setEnabled(true);
        }
    };

    Action Compile = new AbstractAction("Compile")
    {
      public void actionPerformed(ActionEvent e) {
          if (currentFile.equals("Untitled"))
              saveFileAs();

          SSLF compiled = new SSLF();
          //new Console();
          compiled.loadProgramFile(currentFile);
          compiled.execute();
      }
    };

    Action Save = new AbstractAction("Save"){
        public void actionPerformed(ActionEvent e){
            if(!currentFile.equals("Untitled"))
                saveFile(currentFile);
            else
                saveFileAs();
        }

    };
    Action SaveAs = new AbstractAction("Save as..."){
        public void actionPerformed(ActionEvent e){
            saveFileAs();
        }
    };
    Action Quit = new AbstractAction("Quit"){
        public void actionPerformed(ActionEvent e){
            saveOld();
            System.exit(0);
        }
    };
    Action New = new AbstractAction("New"){
        public void actionPerformed(ActionEvent e){
            saveOld();
            area.setText("");
            currentFile = "Untitled";
            setTitle(currentFile);
            changed = false;
            Save.setEnabled(false);
            SaveAs.setEnabled(false);
        }
    };

    private KeyListener k1 = new KeyAdapter(){
        public void keyPressed(KeyEvent e) {
            changed = true;
            Save.setEnabled(true);
            SaveAs.setEnabled(true);
        }
    };

    ActionMap m = area.getActionMap();
    Action Cut = m.get(DefaultEditorKit.cutAction);
    Action Copy = m.get(DefaultEditorKit.copyAction);
    Action Paste = m.get(DefaultEditorKit.pasteAction);

    public GUI(){
        area.setFont(new Font("Monospaced",Font.PLAIN,12));
        JScrollPane scroll = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scroll,BorderLayout.CENTER);

        JMenuBar JMB = new JMenuBar();
        setJMenuBar(JMB);
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu compile = new JMenu("Compile");
        JMB.add(file);
        JMB.add(edit);
        JMB.add(compile);

        file.add(New);
        file.add(Open);
        file.add(Save);
        file.add(Quit);
        file.add(SaveAs);

        edit.add(Cut);
        edit.add(Copy);
        edit.add(Paste);

        compile.add(Compile);

        edit.getItem(0).setText("Cut out");
        edit.getItem(1).setText("Copy");
        edit.getItem(2).setText("Paste");

        Save.setEnabled(false);
        SaveAs.setEnabled(false);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        area.addKeyListener(k1);
        setTitle(currentFile);
        setVisible(true);


    }

}

