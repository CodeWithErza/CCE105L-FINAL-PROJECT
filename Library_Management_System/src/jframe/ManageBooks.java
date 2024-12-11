/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package jframe;

import com.mysql.cj.jdbc.PreparedStatementWrapper;
import com.sun.jdi.connect.spi.Connection;
import java.awt.Color;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import static jframe.DBConnection.con;

/**
 *
 * @author Admin
 */
public class ManageBooks extends javax.swing.JFrame {

    /**
     * Creates new form ManageBooks
     */
    
    String bookName, author, category, isbn;
    int quantity;
    DefaultTableModel model;
    
    public ManageBooks() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setBookDetailsToTable();

    }
    // TO SET BOOK DETAILS TO TABLE
    public void setBookDetailsToTable(){
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
            java.sql.Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM book_details");

            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String bookName = rs.getString("book_name");
                String author = rs.getString("author");
                String category = rs.getString("category");
                String quantity = rs.getString("quantity");

                Object[] obj = {isbn, bookName, author, category, quantity};
                model = (DefaultTableModel) tbl_bookDetails.getModel();
                model.addRow(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// TO ADD BOOKS TO book_details table
public boolean addBooks() {
    boolean isAdded = false;
    isbn = txt_ISBNId.getText(); 
    bookName = txt_bookName.getText();
    author = txt_authorName.getText();
    category = combo_category.getSelectedItem().toString(); 
    quantity = Integer.parseInt(txt_quantity.getText()); 

    try {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
        String sql = "INSERT INTO book_details (isbn, book_name, author, category, quantity) VALUES(?,?,?,?,?)";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, isbn);
        pst.setString(2, bookName);
        pst.setString(3, author);
        pst.setString(4, category);
        pst.setInt(5, quantity);

        int rowCount = pst.executeUpdate();
        isAdded = rowCount > 0; // Set to true if the insert was successful

    } catch (java.sql.SQLIntegrityConstraintViolationException e) {
        // Log the error and show a user-friendly message
        System.err.println("Duplicate entry error: " + e.getMessage());
        JOptionPane.showMessageDialog(null, "Duplicate entry detected for ISBN. Please use a unique ISBN.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
    } catch (ClassNotFoundException e) {
        // Log the error and show a user-friendly message
        System.err.println("Database driver error: " + e.getMessage());
        JOptionPane.showMessageDialog(null, "Database driver not found. Please contact support.", "Driver Error", JOptionPane.ERROR_MESSAGE);
    }catch (Exception e) {
        // Log the error and show a generic message
        System.err.println("Unexpected error: " + e.getMessage());
        JOptionPane.showMessageDialog(null, "An unexpected error occurred. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (con != null) {
                con.close(); // Close the connection to free resources
            }
        } catch (SQLException e) {
            // Log connection close error silently
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    return isAdded;
}

        
        // TO UPDATE BOOK DETAILS
        public boolean updateBook() {
            boolean isUpdated = false;
            isbn = txt_ISBNId.getText(); 
            bookName = txt_bookName.getText();
            author = txt_authorName.getText();
            category = combo_category.getSelectedItem().toString(); 
            quantity = Integer.parseInt(txt_quantity.getText());
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_ms", "root", "");
                String sql = "UPDATE book_details SET book_name = ?, author = ?, category = ?, quantity = ? WHERE isbn = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, bookName);
                pst.setString(2, author);
                pst.setString(3, category);
                pst.setInt(4, quantity);
                pst.setString(5, isbn);

                int rowCount = pst.executeUpdate();
                if (rowCount > 0) {
                    isUpdated = true;
                } else {
                    isUpdated = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return isUpdated;
        }


    // TO DELETE BOOK DETAILS
    public boolean deleteBook() {
        boolean isDeleted = false;
        isbn = txt_ISBNId.getText(); // Use txt_ISBNId for ISBN input

        try {
            java.sql.Connection con = DBConnection.getConnection();
            String sql = "DELETE FROM book_details WHERE isbn = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, isbn);

            int rowCount = pst.executeUpdate();
            if (rowCount > 0) {
                isDeleted = true;
            } else {
                isDeleted = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDeleted;
    }
    
    // METHOD TO CLEAR TABLE  
    public void clearTable(){
        DefaultTableModel model = (DefaultTableModel) tbl_bookDetails.getModel();
        model.setRowCount(0);
    }
    
    // METHOD TO SORT BOOK DETAILS
    public void sortBookDetails(String column, String order) {
        int columnIndex = 0;
        switch (column) {
            case "ISBN":
                columnIndex = 0;
                break;
            case "Name":
                columnIndex = 1;
                break;
            case "Author":
                columnIndex = 2;
                break;
            case "Category":
                columnIndex = 3;
                break;
            case "Quantity":
                columnIndex = 4;
                break;
        }

        Object[][] data = new Object[model.getRowCount()][model.getColumnCount()];
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                data[i][j] = model.getValueAt(i, j);
            }
        }
        
    // Initialize timing variables
    long startTime = System.nanoTime(); // Start the timer with nanoTime for better precision

    // Insertion Sort
    for (int i = 1; i < data.length; i++) {
        Object[] key = data[i];
        int j = i - 1;

        while (j >= 0 && ((order.equals("Ascending") && ((Comparable) key[columnIndex]).compareTo(data[j][columnIndex]) < 0) ||
                          (order.equals("Descending") && ((Comparable) key[columnIndex]).compareTo(data[j][columnIndex]) > 0))) {
            data[j + 1] = data[j];
            j--;
        }
        data[j + 1] = key;
    }

    long endTime = System.nanoTime(); // End the timer with nanoTime

    // Calculate and format elapsed time in milliseconds (convert nanoseconds to milliseconds)
    double elapsedTimeInMillis = (endTime - startTime) / 1_000_000.0;

    clearTable();
    for (Object[] row : data) {
        model.addRow(row);
    }
    CPUTimeField.setText(String.format("%.3fms", elapsedTimeInMillis)); // Display with 3 decimal places
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        rSMaterialButtonCircle1 = new rojerusan.RSMaterialButtonCircle();
        rSMaterialButtonCircle2 = new rojerusan.RSMaterialButtonCircle();
        rSMaterialButtonCircle3 = new rojerusan.RSMaterialButtonCircle();
        jLabel11 = new javax.swing.JLabel();
        txt_authorName = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txt_quantity = new javax.swing.JTextField();
        txt_ISBNId = new javax.swing.JTextField();
        txt_bookName = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        combo_category = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_bookDetails = new rojerusan.RSTableMetro();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        CPUTimeField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(184, 0, 31));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 51, 51));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(255, 51, 51));
        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Verdana", 0, 17)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Rewind_48px.png"))); // NOI18N
        jLabel8.setText("Back");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        jPanel4.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 120, 40));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 140, 40));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 140, 40));

        jLabel9.setFont(new java.awt.Font("Roboto", 0, 17)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("ISBN:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 170, 150, 30));

        jLabel4.setFont(new java.awt.Font("Roboto", 0, 10)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Contact_26px.png"))); // NOI18N
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 200, 50, 40));

        jLabel5.setFont(new java.awt.Font("Roboto", 0, 10)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Moleskine_26px.png"))); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 300, 50, 40));

        jLabel10.setFont(new java.awt.Font("Roboto", 0, 17)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Book Name:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 270, 170, 30));

        jLabel6.setFont(new java.awt.Font("Roboto", 0, 10)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/icons8_Collaborator_Male_26px.png"))); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 400, 50, 40));

        jLabel7.setFont(new java.awt.Font("Roboto", 0, 10)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/quantity.png"))); // NOI18N
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 600, 50, 40));

        rSMaterialButtonCircle1.setBackground(new java.awt.Color(255, 102, 0));
        rSMaterialButtonCircle1.setText("DELETE");
        rSMaterialButtonCircle1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonCircle1ActionPerformed(evt);
            }
        });
        jPanel1.add(rSMaterialButtonCircle1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 700, 110, 60));

        rSMaterialButtonCircle2.setBackground(new java.awt.Color(255, 102, 0));
        rSMaterialButtonCircle2.setText("ADD");
        rSMaterialButtonCircle2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonCircle2ActionPerformed(evt);
            }
        });
        jPanel1.add(rSMaterialButtonCircle2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 700, 110, 60));

        rSMaterialButtonCircle3.setBackground(new java.awt.Color(255, 102, 0));
        rSMaterialButtonCircle3.setText("UPDATE");
        rSMaterialButtonCircle3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rSMaterialButtonCircle3ActionPerformed(evt);
            }
        });
        jPanel1.add(rSMaterialButtonCircle3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 700, 110, 60));

        jLabel11.setFont(new java.awt.Font("Roboto", 0, 17)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Author Name:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 370, 150, 30));

        txt_authorName.setBackground(new java.awt.Color(184, 0, 31));
        txt_authorName.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        txt_authorName.setForeground(new java.awt.Color(153, 153, 153));
        txt_authorName.setText("Enter Author name");
        txt_authorName.setToolTipText("");
        txt_authorName.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txt_authorName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_authorNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_authorNameFocusLost(evt);
            }
        });
        txt_authorName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_authorNameActionPerformed(evt);
            }
        });
        jPanel1.add(txt_authorName, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 410, 260, 40));

        jLabel12.setFont(new java.awt.Font("Roboto", 0, 17)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Quantity:");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 570, 90, 30));

        txt_quantity.setBackground(new java.awt.Color(184, 0, 31));
        txt_quantity.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        txt_quantity.setForeground(new java.awt.Color(153, 153, 153));
        txt_quantity.setText("Enter Quantity");
        txt_quantity.setToolTipText("");
        txt_quantity.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txt_quantity.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_quantityFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_quantityFocusLost(evt);
            }
        });
        txt_quantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_quantityActionPerformed(evt);
            }
        });
        jPanel1.add(txt_quantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 600, 260, 40));

        txt_ISBNId.setBackground(new java.awt.Color(184, 0, 31));
        txt_ISBNId.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        txt_ISBNId.setForeground(new java.awt.Color(153, 153, 153));
        txt_ISBNId.setText("Enter ISBN");
        txt_ISBNId.setToolTipText("");
        txt_ISBNId.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txt_ISBNId.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_ISBNIdFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_ISBNIdFocusLost(evt);
            }
        });
        txt_ISBNId.addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                txt_ISBNIdAncestorResized(evt);
            }
        });
        txt_ISBNId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_ISBNIdActionPerformed(evt);
            }
        });
        jPanel1.add(txt_ISBNId, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 200, 260, 40));

        txt_bookName.setBackground(new java.awt.Color(184, 0, 31));
        txt_bookName.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        txt_bookName.setForeground(new java.awt.Color(153, 153, 153));
        txt_bookName.setText("Enter Book name");
        txt_bookName.setToolTipText("");
        txt_bookName.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(255, 255, 255)));
        txt_bookName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_bookNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_bookNameFocusLost(evt);
            }
        });
        txt_bookName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_bookNameActionPerformed(evt);
            }
        });
        jPanel1.add(txt_bookName, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 300, 260, 40));

        jLabel14.setFont(new java.awt.Font("Roboto", 0, 17)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Book Category:");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 470, 120, 30));

        jLabel18.setFont(new java.awt.Font("Roboto", 0, 10)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/book.png"))); // NOI18N
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 500, 50, 40));

        combo_category.setFont(new java.awt.Font("Segoe UI", 0, 17)); // NOI18N
        combo_category.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Scholarly", "Non-Scholarly" }));
        combo_category.setBorder(null);
        combo_category.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_categoryActionPerformed(evt);
            }
        });
        jPanel1.add(combo_category, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 500, 260, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 470, 870));

        jPanel3.setBackground(new java.awt.Color(255, 248, 232));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(255, 51, 51));
        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Verdana", 0, 35)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("x");
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 30, 30));

        jPanel3.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 0, 103, 42));

        jScrollPane2.setBackground(new java.awt.Color(255, 248, 232));

        tbl_bookDetails.setBackground(new java.awt.Color(204, 204, 204));
        tbl_bookDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ISBN", "Book Name", "Author", "Category", "Quantity"
            }
        ));
        tbl_bookDetails.setColorBackgoundHead(new java.awt.Color(153, 0, 0));
        tbl_bookDetails.setColorBordeFilas(new java.awt.Color(153, 0, 0));
        tbl_bookDetails.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        tbl_bookDetails.setColorSelBackgound(new java.awt.Color(255, 51, 51));
        tbl_bookDetails.setRowHeight(25);
        tbl_bookDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_bookDetailsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbl_bookDetails);

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 220, 910, 490));

        jLabel1.setBackground(new java.awt.Color(0, 112, 192));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 0));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/AddNewBookIcons/books (1).png"))); // NOI18N
        jLabel1.setText("      MANAGE BOOKS");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, 450, 120));

        jPanel5.setBackground(new java.awt.Color(204, 0, 0));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 140, 330, 5));

        jButton1.setBackground(new java.awt.Color(204, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(204, 204, 204));
        jButton1.setText("SORT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 180, 90, 30));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ISBN", "Category", "Quantity" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });
        jPanel3.add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 180, 150, 30));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ascending", "Descending" }));
        jPanel3.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 180, 150, 30));

        jLabel15.setFont(new java.awt.Font("Roboto", 2, 15)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(102, 102, 102));
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/sorting.png"))); // NOI18N
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 180, 30, 30));

        jLabel16.setFont(new java.awt.Font("Roboto", 2, 15)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(102, 102, 102));
        jLabel16.setText("Sort By:");
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 180, 60, 30));

        jLabel17.setFont(new java.awt.Font("Roboto", 2, 15)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(102, 102, 102));
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/setting.png"))); // NOI18N
        jPanel3.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 180, 30, 30));

        CPUTimeField.setEditable(false);
        CPUTimeField.setBackground(new java.awt.Color(255, 248, 232));
        CPUTimeField.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        CPUTimeField.setForeground(new java.awt.Color(153, 0, 0));
        CPUTimeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CPUTimeFieldActionPerformed(evt);
            }
        });
        jPanel3.add(CPUTimeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 180, 170, 30));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("CPU TIME:");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 180, 90, 20));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 0, 1260, 870));

        setSize(new java.awt.Dimension(1541, 861));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void rSMaterialButtonCircle1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonCircle1ActionPerformed
        if(deleteBook() == true){
            JOptionPane.showMessageDialog(this, "Book Deleted");
            clearTable();
            setBookDetailsToTable();
        }else{
            JOptionPane.showMessageDialog(this, "Book Deletion Failed");
        }        
    }//GEN-LAST:event_rSMaterialButtonCircle1ActionPerformed

    private void rSMaterialButtonCircle2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonCircle2ActionPerformed
        int quantity;
        try {
            quantity = Integer.parseInt(txt_quantity.getText());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity cannot be negative.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return; // Exit the method if the quantity is invalid.
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method if the input is not a valid number.
        }

        if(addBooks() == true){
            JOptionPane.showMessageDialog(this, "Book Added");
            clearTable();
            setBookDetailsToTable();
        }else{
            JOptionPane.showMessageDialog(this, "Book Addition Failed");
        }
    }//GEN-LAST:event_rSMaterialButtonCircle2ActionPerformed

    private void rSMaterialButtonCircle3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rSMaterialButtonCircle3ActionPerformed
        int quantity;
        try {
            quantity = Integer.parseInt(txt_quantity.getText());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity cannot be negative.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return; // Exit the method if the quantity is invalid.
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method if the input is not a valid number.
        }

        if(updateBook() == true){
            JOptionPane.showMessageDialog(this, "Book Updated");
            clearTable();
            setBookDetailsToTable();
        }else{
            JOptionPane.showMessageDialog(this, "Book Updation Failed");
        }
        }//GEN-LAST:event_rSMaterialButtonCircle3ActionPerformed

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        HomePage home = new HomePage();
        home.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel13MouseClicked

    private void tbl_bookDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_bookDetailsMouseClicked
        int rowNo = tbl_bookDetails.getSelectedRow();
        TableModel model = tbl_bookDetails.getModel();
        
        txt_ISBNId.setText(model.getValueAt(rowNo, 0).toString());
        txt_ISBNId.setForeground(Color.white);
        txt_bookName.setText(model.getValueAt(rowNo, 1).toString());
        txt_bookName.setForeground(Color.white);

        txt_authorName.setText(model.getValueAt(rowNo, 2).toString());
        txt_authorName.setForeground(Color.white);

        combo_category.setSelectedItem(model.getValueAt(rowNo, 3).toString());
        combo_category.setForeground(Color.white);
        
        txt_quantity.setText(model.getValueAt(rowNo, 4).toString());
        txt_quantity.setForeground(Color.white);
       
    }//GEN-LAST:event_tbl_bookDetailsMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String selectedColumn = jComboBox2.getSelectedItem().toString();
        String selectedOrder = jComboBox1.getSelectedItem().toString();
        sortBookDetails(selectedColumn, selectedOrder);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void txt_quantityFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_quantityFocusGained
        if (txt_quantity.getText().equals("Enter Quantity")) {
            txt_quantity.setText("");
            txt_quantity.setForeground(Color.white);
        }
    }//GEN-LAST:event_txt_quantityFocusGained

    private void txt_quantityFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_quantityFocusLost
        if (txt_quantity.getText().isEmpty()) {
            txt_quantity.setText("Enter Quantity");
            txt_quantity.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_txt_quantityFocusLost

    private void txt_quantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_quantityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_quantityActionPerformed

    private void txt_ISBNIdFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_ISBNIdFocusGained
        // TODO add your handling code here:
        if (txt_ISBNId.getText().equals("Enter ISBN")) {
            txt_ISBNId.setText(""); // Clear the placeholder
            txt_ISBNId.setForeground(Color.white); // Set the text color to black (or any visible color)
        }
    }//GEN-LAST:event_txt_ISBNIdFocusGained

    private void txt_ISBNIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_ISBNIdFocusLost
        if (txt_ISBNId.getText().isEmpty()) {
            txt_ISBNId.setText("Enter ISBN");
            txt_ISBNId.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_txt_ISBNIdFocusLost

    private void txt_ISBNIdAncestorResized(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_txt_ISBNIdAncestorResized
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_ISBNIdAncestorResized

    private void txt_ISBNIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_ISBNIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_ISBNIdActionPerformed

    private void txt_bookNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_bookNameFocusGained
        if (txt_bookName.getText().equals("Enter Book name")) {
            txt_bookName.setText(""); // Clear the placeholder
            txt_bookName.setForeground(Color.white); // Set the text color to black (or any visible color)
        }
    }//GEN-LAST:event_txt_bookNameFocusGained

    private void txt_bookNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_bookNameFocusLost
        if (txt_bookName.getText().isEmpty()) {
            txt_bookName.setText("Enter Book name");
            txt_bookName.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_txt_bookNameFocusLost

    private void txt_bookNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_bookNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_bookNameActionPerformed

    private void CPUTimeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CPUTimeFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CPUTimeFieldActionPerformed

    private void txt_authorNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_authorNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_authorNameActionPerformed

    private void txt_authorNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_authorNameFocusLost
        if (txt_authorName.getText().isEmpty()) {
            txt_authorName.setText("Enter Author name");
            txt_authorName.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_txt_authorNameFocusLost

    private void txt_authorNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_authorNameFocusGained
        if (txt_authorName.getText().equals("Enter Author name")) {
            txt_authorName.setText("");
            txt_authorName.setForeground(Color.white);
        }
    }//GEN-LAST:event_txt_authorNameFocusGained

    private void combo_categoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_categoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_categoryActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ManageBooks.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageBooks.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageBooks.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageBooks.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ManageBooks().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField CPUTimeField;
    private javax.swing.JComboBox<String> combo_category;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private rojerusan.RSMaterialButtonCircle rSMaterialButtonCircle1;
    private rojerusan.RSMaterialButtonCircle rSMaterialButtonCircle2;
    private rojerusan.RSMaterialButtonCircle rSMaterialButtonCircle3;
    private rojerusan.RSTableMetro tbl_bookDetails;
    private javax.swing.JTextField txt_ISBNId;
    private javax.swing.JTextField txt_authorName;
    private javax.swing.JTextField txt_bookName;
    private javax.swing.JTextField txt_quantity;
    // End of variables declaration//GEN-END:variables
}
