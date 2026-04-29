package btlJAVA;
//File encoding: UTF-8
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class WarehouseManagement {

 // =================== MÀU SẮC ===================
 static final Color C_WHITE    = Color.WHITE;
 static final Color C_BG       = new Color(245, 247, 250);
 static final Color C_SIDEBAR  = new Color(30,  41,  59);
 static final Color C_SIDEBAR_HL = new Color(51, 65, 85);
 static final Color C_BLUE     = new Color(37,  99, 235);
 static final Color C_BLUE_LT  = new Color(219, 234, 254);
 static final Color C_GREEN    = new Color(22,  163,  74);
 static final Color C_GREEN_LT = new Color(220, 252, 231);
 static final Color C_RED      = new Color(220,  38,  38);
 static final Color C_RED_LT   = new Color(254, 226, 226);
 static final Color C_AMBER    = new Color(202, 138,   4);
 static final Color C_AMBER_LT = new Color(254, 243, 199);
 static final Color C_BORDER   = new Color(226, 232, 240);
 static final Color C_TEXT     = new Color(15,  23,  42);
 static final Color C_TEXT2    = new Color(100, 116, 139);
 static final Color C_TEXT3    = new Color(148, 163, 184);
 static final Color C_ROW_ALT  = new Color(248, 250, 252);
 static final Color C_SEL      = new Color(219, 234, 254);

 // =================== FONT ===================
 static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  20);
 static final Font F_H2    = new Font("Segoe UI", Font.BOLD,  15);
 static final Font F_LABEL = new Font("Segoe UI", Font.BOLD,  13);
 static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
 static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

 // =================== DỮ LIỆU ===================
 static class Product {
     static int seq = 1;
     String id, name, category, brand, unit, description;
     double price;
     int quantity, minStock;

     Product(String name, String category, String brand,
             double price, int quantity, int minStock, String unit, String desc) {
         this.id = String.format("SP%04d", seq++);
         this.name = name; this.category = category; this.brand = brand;
         this.price = price; this.quantity = quantity; this.minStock = minStock;
         this.unit = unit; this.description = desc;
     }
     boolean isLow()   { return quantity > 0 && quantity <= minStock; }
     boolean isEmpty() { return quantity == 0; }
     String statusText() {
         if (isEmpty()) return "Hết hàng";
         if (isLow())   return "Sắp hết";
         return "Còn hàng";
     }
 }

 static class Transaction {
     enum Type { NHAP, XUAT }
     static int seq = 1;
     String id, productId, productName, date, note, user;
     Type type;
     int quantity;
     // Đã thêm tham số 'String user' vào đây:
     Transaction(String productId, String productName, Type type, int quantity, String note, String user) {
         this.id = String.format("GD%04d", seq++);
         this.productId = productId; this.productName = productName;
         this.type = type; this.quantity = quantity; this.note = note;
         this.user = user;
         this.date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
     }
 }

 static List<Product>     products     = new ArrayList<>();
 static List<Transaction> transactions = new ArrayList<>();

 static class Account {
    String username, password, role, fullName, phone, email;
    Account(String u, String p, String r, String fn, String ph, String em) {
        this.username = u; this.password = p; this.role = r;
        this.fullName = fn; this.phone = ph; this.email = em;
    }
}
static List<Account> accounts = new ArrayList<>();
static final String FILE_ACCOUNTS = "accounts.txt";

 // =================== ĐỌC / GHI FILE ===================
static final String FILE_PRODUCTS = "products.txt";
static final String FILE_TRANS = "transactions.txt";

static void saveData() {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PRODUCTS))) {
        for (Product p : products) bw.write(String.join("|", p.id, p.name, p.category, p.brand, String.valueOf(p.price), String.valueOf(p.quantity), String.valueOf(p.minStock), p.unit, p.description) + "\n");
    } catch (Exception e) { e.printStackTrace(); }
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_TRANS))) {
        for (Transaction t : transactions) bw.write(String.join("|", t.id, t.productId, t.productName, t.type.name(), String.valueOf(t.quantity), t.date, t.note, t.user) + "\n"); // Lưu thêm t.user
    } catch (Exception e) { e.printStackTrace(); }
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_ACCOUNTS))) {
        for (Account a : accounts) bw.write(String.join("|", a.username, a.password, a.role, a.fullName, a.phone, a.email) + "\n");
    } catch (Exception e) { e.printStackTrace(); }
}

static void loadData() {
    File fAcc = new File(FILE_ACCOUNTS); 
    File fProd = new File(FILE_PRODUCTS); 
    File fTrans = new File(FILE_TRANS);
    
    boolean isFirstRun = false;

    // 1. Kiểm tra Tài khoản (Nếu file không tồn tại HOẶC file bị xóa trắng 0 byte)
    if (!fAcc.exists() || fAcc.length() == 0) {
        accounts.add(new Account("admin", "admin123", "Quản lý nhân sự", "Quản trị viên Hệ thống", "0988123456", "admin@electro.com"));
        accounts.add(new Account("kho1", "kho123", "Quản lý kho", "Nhân viên Kho chính", "0912345678", "kho1@electro.com"));
        isFirstRun = true;
    } else {
        try (BufferedReader br = new BufferedReader(new FileReader(fAcc))) {
            accounts.clear(); String line;
            while ((line = br.readLine()) != null) {
                String[] a = line.split("\\|");
                if (a.length >= 3) accounts.add(new Account(a[0], a[1], a[2], a.length > 3 ? a[3] : "", a.length > 4 ? a[4] : "", a.length > 5 ? a[5] : ""));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 2. Kiểm tra Sản phẩm & Giao dịch mẫu
    if (!fProd.exists() || fProd.length() == 0) { 
        loadSampleData(); 
        isFirstRun = true; 
    } else {
        try (BufferedReader br = new BufferedReader(new FileReader(fProd))) {
            products.clear(); String line; int maxPId = 0;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length >= 9) {
                    Product prod = new Product(p[1], p[2], p[3], Double.parseDouble(p[4]), Integer.parseInt(p[5]), Integer.parseInt(p[6]), p[7], p[8]);
                    prod.id = p[0]; products.add(prod);
                    int idNum = Integer.parseInt(p[0].replace("SP", ""));
                    if (idNum > maxPId) maxPId = idNum;
                }
            }
            Product.seq = maxPId + 1;
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 3. Kiểm tra Lịch sử giao dịch (Chỉ đọc nếu file tồn tại và có dữ liệu)
    if (fTrans.exists() && fTrans.length() > 0) {
        try (BufferedReader br = new BufferedReader(new FileReader(fTrans))) {
            transactions.clear(); String line; int maxTId = 0;
            while ((line = br.readLine()) != null) {
                String[] t = line.split("\\|");
                if (t.length >= 7) {
                    String usr = t.length > 7 ? t[7] : "Hệ thống";
                    Transaction trans = new Transaction(t[1], t[2], Transaction.Type.valueOf(t[3]), Integer.parseInt(t[4]), t[6], usr);
                    trans.id = t[0]; trans.date = t[5]; transactions.add(trans);
                    int idNum = Integer.parseInt(t[0].replace("GD", ""));
                    if (idNum > maxTId) maxTId = idNum;
                }
            }
            Transaction.seq = maxTId + 1;
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 4. Lưu lại toàn bộ trạng thái chuẩn ra ổ cứng
    if (isFirstRun) {
        saveData();
    }
}

 static void loadSampleData() {
     products.add(new Product("iPhone 15 Pro Max",        "Điện thoại",    "Apple",    32990000, 25,  5,  "Chiếc", "256GB, Titan Black"));
     products.add(new Product("Samsung Galaxy S24 Ultra", "Điện thoại",    "Samsung",  28990000, 18,  5,  "Chiếc", "512GB, Titanium Gray"));
     products.add(new Product("MacBook Pro M3 14 inch",   "Laptop",        "Apple",    49990000,  8,  3,  "Chiếc", "M3 Pro, 18GB RAM"));
     products.add(new Product("Dell XPS 15 9530",         "Laptop",        "Dell",     38990000, 12,  3,  "Chiếc", "Intel i9, 32GB, RTX 4070"));
     products.add(new Product("iPad Pro M4 11 inch",      "Máy tính bảng", "Apple",    22990000, 15,  5,  "Chiếc", "256GB, WiFi+Cellular"));
     products.add(new Product("AirPods Pro 2",            "Tai nghe",      "Apple",     6990000, 42, 10,  "Chiếc", "Chống ồn ANC, USB-C"));
     products.add(new Product("Sony WH-1000XM5",          "Tai nghe",      "Sony",      8490000, 20,  8,  "Chiếc", "Chống ồn, 30h pin"));
     products.add(new Product("Apple Watch Series 9",     "Đồng hồ TM",    "Apple",    12490000,  3,  5,  "Chiếc", "45mm, GPS+Cellular"));
     products.add(new Product("Samsung 65 QLED 8K",       "Tivi",          "Samsung",  85990000,  0,  2,  "Chiếc", "65Q950C, HDR10+"));
     products.add(new Product("Logitech MX Master 3S",    "Phụ kiện",      "Logitech",  2290000, 55, 15,  "Chiếc", "Chuột không dây"));
     products.add(new Product("Anker 140W GaN Charger",   "Phụ kiện",      "Anker",      990000,  2, 10,  "Chiếc", "USB-C 3 cổng"));
     products.add(new Product("SSD Samsung 990 Pro 2TB",  "Lưu trữ",       "Samsung",   3490000, 30, 10,  "Chiếc", "NVMe M.2 PCIe 4.0"));
     // Đã thêm "kho1" vào cuối mỗi lệnh giao dịch mẫu:
     transactions.add(new Transaction("SP0001", "iPhone 15 Pro Max",      Transaction.Type.NHAP, 30, "Nhập hàng đầu kỳ", "kho1"));
     transactions.add(new Transaction("SP0001", "iPhone 15 Pro Max",      Transaction.Type.XUAT,  5, "Xuất bán khách lẻ", "kho1"));
     transactions.add(new Transaction("SP0003", "MacBook Pro M3 14 inch", Transaction.Type.NHAP, 10, "Nhập từ Apple VN", "kho1"));
     transactions.add(new Transaction("SP0006", "AirPods Pro 2",          Transaction.Type.XUAT,  8, "Xuất đại lý Hà Nội", "kho1"));
 }

 // =================== MAIN ===================
public static void main(String[] args) {
    try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
    catch (Exception ignored) {}
    UIManager.put("OptionPane.background",       C_WHITE);
    UIManager.put("Panel.background",            C_WHITE);
    UIManager.put("OptionPane.messageForeground", C_TEXT);
    
    loadData(); // <--- Đổi dòng này
    
    SwingUtilities.invokeLater(LoginFrame::new);
}

 // =========================================================
 //   MÀNN HÌNH ĐĂNG NHẬP
 // =========================================================
 static class LoginFrame extends JFrame {
     static final Map<String,String> ACCOUNTS = new LinkedHashMap<>();

     JTextField     fUser;
     JPasswordField fPass;
     JLabel         lblErr;

     LoginFrame() {
         setTitle("Đăng nhập - ElectroStock");
         setDefaultCloseOperation(EXIT_ON_CLOSE);
         setSize(420, 520);
         setResizable(false);
         setLocationRelativeTo(null);
         getContentPane().setBackground(C_BG);
         setLayout(new GridBagLayout());
         buildUI();
         setVisible(true);
     }

     void buildUI() {
         JPanel card = new JPanel() {
             @Override protected void paintComponent(Graphics g) {
                 Graphics2D g2 = (Graphics2D) g.create();
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                 g2.setColor(C_WHITE);
                 g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                 g2.setColor(C_BORDER);
                 g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                 g2.dispose();
                 super.paintComponent(g);
             }
         };
         card.setOpaque(false);
         card.setLayout(new GridBagLayout());
         card.setPreferredSize(new Dimension(360, 440));

         GridBagConstraints g = new GridBagConstraints();
         g.insets = new Insets(6, 24, 6, 24);
         g.fill   = GridBagConstraints.HORIZONTAL;
         g.gridx  = 0;

         // Logo
         JPanel logoBox = new JPanel() {
             @Override protected void paintComponent(Graphics gx) {
                 Graphics2D g2 = (Graphics2D) gx.create();
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                 g2.setColor(C_BLUE);
                 g2.fillRoundRect(0, 0, 56, 56, 12, 12);
                 g2.setColor(C_WHITE);
                 g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                 FontMetrics fm = g2.getFontMetrics();
                 g2.drawString("E", (56 - fm.stringWidth("E"))/2, 38);
                 g2.dispose();
             }
             @Override public Dimension getPreferredSize() { return new Dimension(56, 56); }
         };
         logoBox.setOpaque(false);
         JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
         logoRow.setOpaque(false);
         logoRow.add(logoBox);

         g.gridy = 0; g.insets = new Insets(28, 24, 4, 24);
         card.add(logoRow, g);

         JLabel lAppName = new JLabel("ElectroStock", SwingConstants.CENTER);
         lAppName.setFont(new Font("Segoe UI", Font.BOLD, 20));
         lAppName.setForeground(C_BLUE);
         g.gridy = 1; g.insets = new Insets(0, 24, 2, 24);
         card.add(lAppName, g);

         JLabel lSub = new JLabel("Hệ thống quản lý kho điện tử", SwingConstants.CENTER);
         lSub.setFont(F_SMALL); lSub.setForeground(C_TEXT2);
         g.gridy = 2; g.insets = new Insets(0, 24, 14, 24);
         card.add(lSub, g);

         JSeparator sep = new JSeparator();
         sep.setForeground(C_BORDER);
         g.gridy = 3; g.insets = new Insets(0, 16, 14, 16);
         card.add(sep, g);

         g.insets = new Insets(4, 24, 2, 24);

         JLabel lUser = new JLabel("Tên đăng nhập");
         lUser.setFont(F_LABEL); lUser.setForeground(C_TEXT);
         g.gridy = 4; card.add(lUser, g);

         fUser = new JTextField("admin");
         styleField(fUser);
         g.gridy = 5; g.insets = new Insets(2, 24, 10, 24);
         card.add(fUser, g);

         JLabel lPass = new JLabel("Mật khẩu");
         lPass.setFont(F_LABEL); lPass.setForeground(C_TEXT);
         g.gridy = 6; g.insets = new Insets(4, 24, 2, 24);
         card.add(lPass, g);

         fPass = new JPasswordField("admin123");
         fPass.setEchoChar('*');
         styleField(fPass);
         g.gridy = 7; g.insets = new Insets(2, 24, 4, 24);
         card.add(fPass, g);

         JCheckBox chkShow = new JCheckBox("Hiện mật khẩu");
         chkShow.setOpaque(false); chkShow.setFont(F_SMALL); chkShow.setForeground(C_TEXT2);
         chkShow.addActionListener(e -> fPass.setEchoChar(chkShow.isSelected() ? (char)0 : '*'));
         g.gridy = 8; g.insets = new Insets(0, 22, 4, 24);
         card.add(chkShow, g);

         lblErr = new JLabel(" ");
         lblErr.setFont(F_SMALL); lblErr.setForeground(C_RED);
         g.gridy = 9; g.insets = new Insets(0, 24, 2, 24);
         card.add(lblErr, g);

         JButton btnLogin = createBtn("ĐĂNG NHẬP", C_BLUE, C_WHITE);
         btnLogin.setPreferredSize(new Dimension(0, 42));
         g.gridy = 10; g.insets = new Insets(2, 24, 6, 24);
         card.add(btnLogin, g);

         JLabel lHint = new JLabel("Tài khoản demo: admin / admin123", SwingConstants.CENTER);
         lHint.setFont(F_SMALL); lHint.setForeground(C_TEXT3);
         g.gridy = 11; g.insets = new Insets(0, 24, 20, 24);
         card.add(lHint, g);

         ActionListener doLogin = ev -> tryLogin();
         btnLogin.addActionListener(doLogin);
         fPass.addActionListener(doLogin);
         fUser.addActionListener(e -> fPass.requestFocus());

         add(card);
     }

     void tryLogin() {
    String user = fUser.getText().trim();
    String pass = new String(fPass.getPassword());
    Account loggedIn = accounts.stream().filter(a -> a.username.equals(user) && a.password.equals(pass)).findFirst().orElse(null);
    if (loggedIn != null) {
        dispose();
        SwingUtilities.invokeLater(() -> new MainFrame(loggedIn));
    } else {
        lblErr.setText("Tài khoản hoặc mật khẩu sai!");
        fPass.setText("");
    }
}

     void styleField(JComponent c) {
         c.setFont(F_BODY);
         c.setBackground(C_WHITE);
         if (c instanceof JTextField) ((JTextField)c).setForeground(C_TEXT);
         c.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createLineBorder(C_BORDER, 1),
             new EmptyBorder(8, 10, 8, 10)
         ));
         c.setPreferredSize(new Dimension(0, 38));
     }
 }

 // =========================================================
 //   CỬA SỔ CHÍNH
 // =========================================================
 static class MainFrame extends JFrame {

     JPanel     contentArea;
     CardLayout cards;
     JLabel     lblStatus;
     Account     currentUser;

     JPanel accChartView; 
     DefaultTableModel tblAccStatsModel;
     JPanel chartView; // Thêm biến này
     DefaultTableModel tblProductModel;
     JTable            tblProduct;
     DefaultTableModel tblTransModel;
     JTable            tblTrans;
     JLabel            statTotal, statValue, statLow, statOut;
     DefaultTableModel tblDashModel;
     JTable            tblDash;
     DefaultTableModel tblAccModel;
     JTable tblAcc;

     JPanel importPanel, exportPanel;

     MainFrame(Account user) {
         this.currentUser = user;
    setTitle("ElectroStock - " + user.role + " | " + user.username);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1280, 760);
    setMinimumSize(new Dimension(1000, 640));
    setLocationRelativeTo(null);
    getContentPane().setBackground(C_WHITE);
    buildUI();
    setVisible(true);
         SwingUtilities.invokeLater(this::checkLowStockAlert);
     }

     void checkLowStockAlert() {
         List<Product> lowProducts = products.stream()
                 .filter(Product::isLow)
                 .collect(Collectors.toList());
         
         if (!lowProducts.isEmpty()) {
             StringBuilder sb = new StringBuilder("⚠️ CẢNH BÁO: CÁC SẢN PHẨM CẦN NHẬP THÊM HÀNG\n\n");
             for (Product p : lowProducts) {
                 sb.append("- ").append(p.name)
                   .append(" (Mã: ").append(p.id).append(")")
                   .append(" | Tồn hiện tại: ").append(p.quantity)
                   .append(" | Tối thiểu: ").append(p.minStock).append("\n");
             }
             sb.append("\nVui lòng kiểm tra và lên kế hoạch nhập kho!");
             JOptionPane.showMessageDialog(this, sb.toString(), "Cảnh báo Tồn kho", JOptionPane.WARNING_MESSAGE);
         }
     }
     void buildUI() {
    setLayout(new BorderLayout());
    cards = new CardLayout();
    contentArea = new JPanel(cards);
    contentArea.setBackground(C_BG);
    if (currentUser.role.equals("Quản lý kho")) {
        contentArea.add(buildDashboard(), "dashboard");
        contentArea.add(buildProductPanel(), "products");
        contentArea.add(buildImportPanel(), "import");
        contentArea.add(buildExportPanel(), "export");
        contentArea.add(buildTransPanel(), "trans");
        contentArea.add(buildStatsPanel(), "stats");
        cards.show(contentArea, "dashboard");
        refreshDashboard();
    } else {
        contentArea.add(buildAccountPanel(), "accounts");
        contentArea.add(buildAccStatsPanel(), "stats_acc");
        cards.show(contentArea, "accounts");
    }
    add(buildTopBar(), BorderLayout.NORTH);
    add(buildSidebar(), BorderLayout.WEST);
    add(buildStatusBar(), BorderLayout.SOUTH);
    add(contentArea, BorderLayout.CENTER);
}

JPanel buildAccountPanel() {
    JPanel outer = new JPanel(new BorderLayout(0,14));
    outer.setBackground(C_BG);
    outer.setBorder(new EmptyBorder(24,24,24,24));
    JPanel topRow = new JPanel(new BorderLayout(10,0));
    topRow.setBackground(C_BG);
    JLabel title = new JLabel("Quản lý Tài khoản & Phân quyền");
    title.setFont(F_TITLE); title.setForeground(C_TEXT);
    JPanel srch = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
    srch.setBackground(C_BG);
    JTextField fSearch = buildTextField("Tìm kiếm tên đăng nhập, sđt, email...", 260);
    srch.add(fSearch);
    topRow.add(title, BorderLayout.WEST);
    topRow.add(srch, BorderLayout.EAST);
    outer.add(topRow, BorderLayout.NORTH);
    JPanel tableCard = whiteCard("Danh sách người dùng hệ thống");
    tblAccModel = new DefaultTableModel(new String[]{"Tên đăng nhập", "Họ và tên", "Số điện thoại", "Email", "Vai trò"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    tblAcc = buildTable(tblAccModel);
    setColWidths(tblAcc, new int[]{120, 160, 110, 180, 120});
    tableCard.add(styledScroll(tblAcc), BorderLayout.CENTER);
    JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,10));
    toolbar.setBackground(C_WHITE);
    toolbar.setBorder(BorderFactory.createMatteBorder(1,0,0,0,C_BORDER));
    JButton btnAdd = createBtn("Thêm tài khoản", C_BLUE, C_WHITE);
    JButton btnEdit = createBtn("Đổi quyền/Mật khẩu", C_BG, C_TEXT);
    JButton btnDelete = createBtn("Xóa tài khoản", C_RED_LT, C_RED);
    toolbar.add(btnAdd); toolbar.add(btnEdit); toolbar.add(btnDelete);
    tableCard.add(toolbar, BorderLayout.SOUTH);
    outer.add(tableCard, BorderLayout.CENTER);
    refreshAccTable();
    fSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e)  { filterAccount(fSearch.getText()); }
        public void removeUpdate(javax.swing.event.DocumentEvent e)  { filterAccount(fSearch.getText()); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) {}
    });
    btnAdd.addActionListener(e -> showAccDialog(null));
    btnEdit.addActionListener(e -> {
        int r = tblAcc.getSelectedRow();
        if (r < 0) { warn("Chọn tài khoản cần sửa!"); return; }
        String u = (String) tblAccModel.getValueAt(tblAcc.convertRowIndexToModel(r), 0);
        accounts.stream().filter(a -> a.username.equals(u)).findFirst().ifPresent(this::showAccDialog);
    });
    btnDelete.addActionListener(e -> {
        int r = tblAcc.getSelectedRow();
        if (r < 0) { warn("Chọn tài khoản cần xóa!"); return; }
        String u = (String) tblAccModel.getValueAt(tblAcc.convertRowIndexToModel(r), 0);
        if (u.equals(currentUser.username)) { warn("Không thể xóa chính mình!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Xóa " + u + "?", "Xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            accounts.removeIf(a -> a.username.equals(u)); refreshAccTable(); saveData();
        }
    });
    return outer;
}
void filterAccount(String kw) {
    if (tblAccModel == null) return;
    tblAccModel.setRowCount(0);
    String k = kw.trim().toLowerCase();
    for (Account a : accounts) {
        if (k.isEmpty() || a.username.toLowerCase().contains(k) || a.fullName.toLowerCase().contains(k) 
            || a.phone.toLowerCase().contains(k) || a.email.toLowerCase().contains(k) || a.role.toLowerCase().contains(k)) {
            tblAccModel.addRow(new Object[]{a.username, a.fullName, a.phone, a.email, a.role});
        }
    }
}
void refreshAccTable() {
    tblAccModel.setRowCount(0);
    for (Account a : accounts) tblAccModel.addRow(new Object[]{a.username, a.fullName, a.phone, a.email, a.role});
    refreshAccStats();
}
void showAccDialog(Account ex) {
    JDialog d = new JDialog(this, ex==null?"Thêm tài khoản":"Sửa tài khoản", true);
    d.setSize(400, 420); d.setLocationRelativeTo(this); d.setLayout(new GridBagLayout());
    d.getContentPane().setBackground(C_WHITE);
    GridBagConstraints g = new GridBagConstraints(); g.fill=GridBagConstraints.HORIZONTAL; g.insets=new Insets(5,10,5,10);
    JTextField fUser = buildTextField(ex!=null?ex.username:"", 200);
    if (ex != null) fUser.setEditable(false);
    JTextField fPass = buildTextField(ex!=null?ex.password:"", 200);
    JTextField fName = buildTextField(ex!=null?ex.fullName:"", 200);
    JTextField fPhone = buildTextField(ex!=null?ex.phone:"", 200);
    JTextField fEmail = buildTextField(ex!=null?ex.email:"", 200);
    JComboBox<String> cbRole = buildCombo(new String[]{"Quản lý kho", "Quản lý nhân sự"});
    if (ex != null) cbRole.setSelectedItem(ex.role);
    g.gridx=0; g.gridy=0; d.add(new JLabel("Tên đăng nhập *"), g); g.gridx=1; d.add(fUser, g);
    g.gridx=0; g.gridy=1; d.add(new JLabel("Mật khẩu *"), g); g.gridx=1; d.add(fPass, g);
    g.gridx=0; g.gridy=2; d.add(new JLabel("Họ và tên"), g); g.gridx=1; d.add(fName, g);
    g.gridx=0; g.gridy=3; d.add(new JLabel("Số điện thoại"), g); g.gridx=1; d.add(fPhone, g);
    g.gridx=0; g.gridy=4; d.add(new JLabel("Email"), g); g.gridx=1; d.add(fEmail, g);
    g.gridx=0; g.gridy=5; d.add(new JLabel("Phân quyền"), g); g.gridx=1; d.add(cbRole, g);
    JButton btnSave = createBtn("Lưu", C_BLUE, C_WHITE);
    g.gridy=6; g.gridx=1; g.insets=new Insets(15,10,5,10); d.add(btnSave, g);
    
    btnSave.addActionListener(e -> {
        String u=fUser.getText().trim(), p=fPass.getText().trim(), r=(String)cbRole.getSelectedItem();
        String fn=fName.getText().trim(), ph=fPhone.getText().trim(), em=fEmail.getText().trim();
        
        if (u.isEmpty()||p.isEmpty()) { warn("Nhập đủ tên đăng nhập và mật khẩu!"); return; }
        if (ex == null && accounts.stream().anyMatch(a -> a.username.equals(u))) { warn("Trùng tên đăng nhập!"); return; }
        
        // Bắt đầu Check trùng SĐT và Email (Bỏ qua tài khoản đang sửa)
        if (!ph.isEmpty() && accounts.stream().anyMatch(a -> a != ex && ph.equals(a.phone))) { 
            warn("Số điện thoại này đã được sử dụng bởi tài khoản khác!"); return; 
        }
        if (!em.isEmpty() && accounts.stream().anyMatch(a -> a != ex && em.equalsIgnoreCase(a.email))) { 
            warn("Email này đã được sử dụng bởi tài khoản khác!"); return; 
        }
        
        if (ex == null) accounts.add(new Account(u, p, r, fn, ph, em));
        else { ex.password = p; ex.role = r; ex.fullName = fn; ex.phone = ph; ex.email = em; }
        saveData(); refreshAccTable(); d.dispose();
    });
    d.setVisible(true);
}
     // ------- THANH TRÊN -------
     JPanel buildTopBar() {
         JPanel bar = new JPanel(new BorderLayout());
         bar.setBackground(C_WHITE);
         bar.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
             new EmptyBorder(10, 20, 10, 20)
         ));

         JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
         left.setBackground(C_WHITE);
         JPanel logoMark = new JPanel() {
             @Override protected void paintComponent(Graphics g) {
                 Graphics2D g2 = (Graphics2D) g.create();
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                 g2.setColor(C_BLUE); g2.fillRoundRect(0, 0, 32, 32, 8, 8);
                 g2.setColor(C_WHITE); g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                 g2.drawString("E", 10, 22); g2.dispose();
             }
             @Override public Dimension getPreferredSize() { return new Dimension(32, 32); }
         };
         logoMark.setOpaque(false);
         JLabel lblLogo = new JLabel("ElectroStock");
         lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
         lblLogo.setForeground(C_BLUE);
         left.add(logoMark); left.add(lblLogo);

         JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
         right.setBackground(C_WHITE);
         JLabel lblUser = new JLabel(currentUser.role + ": " + currentUser.username);
         lblUser.setFont(F_BODY); lblUser.setForeground(C_TEXT2);
         JButton btnLogout = new JButton("Đăng xuất");
         btnLogout.setFont(F_SMALL); btnLogout.setForeground(C_RED);
         btnLogout.setBackground(C_RED_LT);
         btnLogout.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createLineBorder(new Color(252,165,165), 1),
             new EmptyBorder(4, 12, 4, 12)
         ));
         btnLogout.setFocusPainted(false);
         btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
         btnLogout.addActionListener(e -> {
             int r = JOptionPane.showConfirmDialog(this,
                 "Bạn có muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
             if (r == JOptionPane.YES_OPTION) {
                 dispose(); SwingUtilities.invokeLater(LoginFrame::new);
             }
         });
         right.add(lblUser); right.add(btnLogout);

         bar.add(left, BorderLayout.WEST);
         bar.add(right, BorderLayout.EAST);
         return bar;
     }

     // ------- SIDEBAR -------
     JPanel buildSidebar() {
    JPanel sb = new JPanel();
    sb.setPreferredSize(new Dimension(210, 0));
    sb.setBackground(C_SIDEBAR);
    sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
    sb.add(Box.createVerticalStrut(20));
    JLabel lbl = new JLabel("DANH MỤC");
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
    lbl.setForeground(new Color(100,116,139));
    lbl.setBorder(new EmptyBorder(0, 18, 8, 0));
    lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
    sb.add(lbl);
    String[][] itemsKho = {{"Tổng quan","dashboard"},{"Sản phẩm","products"},{"Nhập kho","import"},{"Xuất kho","export"},{"Giao dịch","trans"},{"Thống kê","stats"}};
    String[][] itemsNS = {{"Tài khoản","accounts"}, {"Thống kê", "stats_acc"}}; // <--- Sửa dòng này
    String[][] items = currentUser.role.equals("Quản lý kho") ? itemsKho : itemsNS;
    ButtonGroup grp = new ButtonGroup();
    boolean first = true;
    for (String[] item : items) {
        JToggleButton btn = buildNavBtn(item[0], item[1]);
        grp.add(btn); sb.add(btn);
        if (first) { btn.setSelected(true); first = false; }
    }
    sb.add(Box.createVerticalGlue());
    JLabel ver = new JLabel("v1.0  -  ElectroStock");
    ver.setFont(F_SMALL); ver.setForeground(new Color(100,116,139));
    ver.setBorder(new EmptyBorder(10,18,14,0));
    ver.setAlignmentX(Component.LEFT_ALIGNMENT);
    sb.add(ver);
    return sb;
}

JPanel buildAccStatsPanel() {
    JPanel outer = new JPanel(new BorderLayout(0,18));
    outer.setBackground(C_BG); outer.setBorder(new EmptyBorder(24,24,24,24));
    JLabel title = new JLabel("Thống kê & Biểu đồ Tài khoản");
    title.setFont(F_TITLE); title.setForeground(C_TEXT); outer.add(title, BorderLayout.NORTH);
    JPanel grid = new JPanel(new GridLayout(1,1,16,0));
    grid.setBackground(C_BG); grid.setPreferredSize(new Dimension(0, 200));
    JPanel cardCat = whiteCard("Thống kê theo phân quyền");
    tblAccStatsModel = new DefaultTableModel(new String[]{"Phân quyền", "Số lượng tài khoản"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    JTable tCat = buildTable(tblAccStatsModel);
    cardCat.add(styledScroll(tCat), BorderLayout.CENTER); grid.add(cardCat);
    JPanel chartCard = whiteCard("Biểu đồ Số lượng Tài khoản theo Phân quyền");
    accChartView = new JPanel() {
        @Override protected void paintComponent(Graphics gx) {
            super.paintComponent(gx);
            Graphics2D g2 = (Graphics2D) gx.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Map<String, Integer> vals = new HashMap<>();
            for (Account a : accounts) vals.put(a.role, vals.getOrDefault(a.role, 0) + 1);
            int max = 0; for (int v : vals.values()) if (v > max) max = v;
            if (max == 0 || vals.isEmpty()) {
                g2.setColor(C_TEXT3); g2.drawString("Chưa có dữ liệu", getWidth()/2 - 40, getHeight()/2); return;
            }
            int w = getWidth(), h = getHeight(), padding = 40;
            int graphW = w - 2*padding, graphH = h - 2*padding;
            g2.setColor(C_BORDER); g2.drawLine(padding, h-padding, w-padding, h-padding);
            int numBars = vals.size(), barWidth = Math.min(80, graphW / numBars / 2);
            int spacing = (graphW - (barWidth * numBars)) / (numBars + 1), x = padding + spacing;
            Color[] colors = {C_BLUE, C_GREEN, C_AMBER, new Color(139, 92, 246)};
            int cIdx = 0;
            for (Map.Entry<String, Integer> e : vals.entrySet()) {
                int barH = (int) ((double)e.getValue() / max * graphH), y = h - padding - barH;
                g2.setColor(colors[cIdx % colors.length]); g2.fillRoundRect(x, y, barWidth, barH, 6, 6);
                g2.setColor(C_TEXT); g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String valStr = String.valueOf(e.getValue());
                g2.drawString(valStr, x + (barWidth - g2.getFontMetrics().stringWidth(valStr))/2, y - 8);
                g2.setColor(C_TEXT2); g2.setFont(F_SMALL); String label = e.getKey();
                g2.drawString(label, x + (barWidth - g2.getFontMetrics().stringWidth(label))/2, h - padding + 15);
                x += barWidth + spacing; cIdx++;
            }
            g2.dispose();
        }
    };
    accChartView.setBackground(C_WHITE); chartCard.add(accChartView, BorderLayout.CENTER);
    JPanel centerSplit = new JPanel(new BorderLayout(0, 16));
    centerSplit.setBackground(C_BG);
    centerSplit.add(grid, BorderLayout.NORTH); centerSplit.add(chartCard, BorderLayout.CENTER);
    outer.add(centerSplit, BorderLayout.CENTER);
    refreshAccStats();
    return outer;
}
void refreshAccStats() {
    if (tblAccStatsModel != null) {
        tblAccStatsModel.setRowCount(0);
        Map<String, Integer> counts = new HashMap<>();
        for (Account a : accounts) counts.put(a.role, counts.getOrDefault(a.role, 0) + 1);
        counts.forEach((role, count) -> tblAccStatsModel.addRow(new Object[]{role, count}));
    }
    if (accChartView != null) accChartView.repaint();
}

     JToggleButton buildNavBtn(String label, String card) {
         JToggleButton btn = new JToggleButton() {
             @Override protected void paintComponent(Graphics g) {
                 Graphics2D g2 = (Graphics2D) g.create();
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                 boolean sel   = isSelected();
                 boolean hover = getModel().isRollover();
                 if (sel) {
                     g2.setColor(C_BLUE);
                     g2.fillRoundRect(8, 2, getWidth()-16, getHeight()-4, 8, 8);
                 } else if (hover) {
                     g2.setColor(C_SIDEBAR_HL);
                     g2.fillRoundRect(8, 2, getWidth()-16, getHeight()-4, 8, 8);
                 }
                 int cy = getHeight()/2;
                 g2.setColor(sel ? C_WHITE : new Color(100,116,139));
                 g2.fillOval(20, cy-4, 8, 8);
                 g2.setFont(sel ? new Font("Segoe UI", Font.BOLD, 13) : F_BODY);
                 g2.setColor(sel ? C_WHITE : new Color(203,213,225));
                 g2.drawString(label, 38, cy+5);
                 g2.dispose();
             }
         };
         btn.setPreferredSize(new Dimension(210, 42));
         btn.setMaximumSize(new Dimension(210, 42));
         btn.setOpaque(false); btn.setContentAreaFilled(false);
         btn.setBorderPainted(false); btn.setFocusPainted(false);
         btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
         btn.setAlignmentX(Component.LEFT_ALIGNMENT);
         btn.addItemListener(e -> {
             if (e.getStateChange() == ItemEvent.SELECTED) {
                 if ("stats".equals(card))     refreshStats();
                 if ("stats_acc".equals(card)) refreshAccStats();
                 if ("trans".equals(card))     refreshTransTable();
                 if ("dashboard".equals(card)) refreshDashboard();
                 if ("import".equals(card))    rebuildImport();
                 if ("export".equals(card))    rebuildExport();
                 cards.show(contentArea, card);
                 setStatus("Đang xem: " + label);
             }
         });
         return btn;
     }

     // ------- STATUS BAR -------
     JPanel buildStatusBar() {
         JPanel bar = new JPanel(new BorderLayout());
         bar.setBackground(new Color(248,250,252));
         bar.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createMatteBorder(1,0,0,0,C_BORDER),
             new EmptyBorder(5,20,5,20)
         ));
         lblStatus = new JLabel("Hệ thống sẵn sàng");
         lblStatus.setFont(F_SMALL); lblStatus.setForeground(C_GREEN);
         JLabel lblDate = new JLabel(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
         lblDate.setFont(F_SMALL); lblDate.setForeground(C_TEXT3);
         bar.add(lblStatus, BorderLayout.WEST);
         bar.add(lblDate,   BorderLayout.EAST);
         return bar;
     }
     void setStatus(String msg) { if (lblStatus != null) lblStatus.setText(msg); }

     // ------- TỔNG QUAN -------
     JPanel buildDashboard() {
         JPanel outer = new JPanel(new BorderLayout(0,16));
         outer.setBackground(C_BG);
         outer.setBorder(new EmptyBorder(24,24,24,24));

         JLabel title = new JLabel("Tổng quan hệ thống");
         title.setFont(F_TITLE); title.setForeground(C_TEXT);
         outer.add(title, BorderLayout.NORTH);

         JPanel center = new JPanel(new BorderLayout(0,16));
         center.setBackground(C_BG);

         // Stat cards
         JPanel cardRow = new JPanel(new GridLayout(1,4,14,0));
         cardRow.setBackground(C_BG);
         statTotal = new JLabel("0"); statValue = new JLabel("0");
         statLow   = new JLabel("0"); statOut   = new JLabel("0");
         cardRow.add(statCard("Tổng sản phẩm",   statTotal, C_BLUE,  C_BLUE_LT,  "sản phẩm"));
         cardRow.add(statCard("Giá trị tồn kho",  statValue, C_GREEN, C_GREEN_LT, "VNĐ"));
         cardRow.add(statCard("Sắp hết hàng",    statLow,  C_AMBER, C_AMBER_LT, "sản phẩm"));
         cardRow.add(statCard("Hết hàng",         statOut,   C_RED,   C_RED_LT,   "sản phẩm"));
         center.add(cardRow, BorderLayout.NORTH);

         // Bảng
         JPanel tableCard = whiteCard("Danh sách sản phẩm");
         tblDashModel = new DefaultTableModel(
             new String[]{"Mã SP","Tên sản phẩm","Danh mục","Thương hiệu","Đơn giá","Tồn kho","Trạng thái"}, 0) {
             @Override public boolean isCellEditable(int r, int c) { return false; }
         };
         tblDash = buildTable(tblDashModel);
         setColWidths(tblDash, new int[]{75,220,120,110,120,80,110});
         tableCard.add(styledScroll(tblDash), BorderLayout.CENTER);
         center.add(tableCard, BorderLayout.CENTER);

         outer.add(center, BorderLayout.CENTER);
         return outer;
     }

     void refreshDashboard() {
         if (statTotal == null) return;
         long low = products.stream().filter(Product::isLow).count();
         long out = products.stream().filter(Product::isEmpty).count();
         double val = products.stream().mapToDouble(p -> p.price * p.quantity).sum();
         statTotal.setText(String.valueOf(products.size()));
         statValue.setText(String.format("%,.0f", val));
         statLow.setText(String.valueOf(low));
         statOut.setText(String.valueOf(out));
         if (tblDashModel != null) {
             tblDashModel.setRowCount(0);
             for (Product p : products) {
                 tblDashModel.addRow(new Object[]{
                     p.id, p.name, p.category, p.brand,
                     String.format("%,.0f đ", p.price),
                     p.quantity, p.statusText()
                 });
             }
         }
     }

     JPanel statCard(String label, JLabel valLbl, Color accent, Color bg, String unit) {
         JPanel c = new JPanel() {
             @Override protected void paintComponent(Graphics g) {
                 Graphics2D g2 = (Graphics2D) g.create();
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                 g2.setColor(C_WHITE); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                 g2.setColor(C_BORDER); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                 g2.setColor(accent);  g2.fillRoundRect(0,16,4,getHeight()-32,4,4);
                 g2.dispose(); super.paintComponent(g);
             }
         };
         c.setOpaque(false);
         c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
         c.setBorder(new EmptyBorder(18,20,18,14));

         JLabel lbl = new JLabel(label);
         lbl.setFont(F_SMALL); lbl.setForeground(C_TEXT2);
         lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
         valLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
         valLbl.setForeground(accent);
         valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
         JLabel uLbl = new JLabel(unit);
         uLbl.setFont(F_SMALL); uLbl.setForeground(C_TEXT3);
         uLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

         c.add(lbl); c.add(Box.createVerticalStrut(6));
         c.add(valLbl); c.add(uLbl);
         return c;
     }

     // ------- SẢN PHẨM -------
     JPanel buildProductPanel() {
         JPanel outer = new JPanel(new BorderLayout(0,14));
         outer.setBackground(C_BG);
         outer.setBorder(new EmptyBorder(24,24,24,24));

         JPanel topRow = new JPanel(new BorderLayout(10,0));
         topRow.setBackground(C_BG);
         JLabel title = new JLabel("Quản lý sản phẩm");
         title.setFont(F_TITLE); title.setForeground(C_TEXT);

         JPanel srch = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
         srch.setBackground(C_BG);
         JTextField fSearch = buildTextField("Tìm kiếm theo tên, mã, thương hiệu...", 240);
         JComboBox<String> cbCat = buildCombo(new String[]{
             "Tất cả danh mục","Điện thoại","Laptop","Máy tính bảng",
             "Tai nghe","Đồng hồ TM","Tivi","Phụ kiện","Lưu trữ"
         });
         cbCat.setPreferredSize(new Dimension(175,34));
         JLabel lCat = new JLabel("Danh mục:");
         lCat.setFont(F_BODY); lCat.setForeground(C_TEXT2);
         srch.add(lCat); srch.add(cbCat); srch.add(fSearch);
         topRow.add(title, BorderLayout.WEST);
         topRow.add(srch,  BorderLayout.EAST);
         outer.add(topRow, BorderLayout.NORTH);

         JPanel tableCard = whiteCard("Danh sách sản phẩm");
         String[] cols = {"Mã SP","Tên sản phẩm","Danh mục","Thương hiệu","Đơn giá (đ)","Tồn kho","Đơn vị","Tối thiểu","Trạng thái"};
         tblProductModel = new DefaultTableModel(cols, 0) {
             @Override public boolean isCellEditable(int r, int c) { return false; }
         };
         tblProduct = buildTable(tblProductModel);
         setColWidths(tblProduct, new int[]{75,200,120,110,120,75,65,80,110});
         tableCard.add(styledScroll(tblProduct), BorderLayout.CENTER);

         JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,10));
         toolbar.setBackground(C_WHITE);
         toolbar.setBorder(BorderFactory.createMatteBorder(1,0,0,0,C_BORDER));
         JButton btnAdd    = createBtn("Thêm sản phẩm", C_BLUE,   C_WHITE);
         JButton btnEdit   = createBtn("Sửa",            C_BG,     C_TEXT);
         JButton btnDelete = createBtn("Xóa",            C_RED_LT, C_RED);
         JButton btnReset  = createBtn("Làm mới",        C_BG,     C_TEXT2);
         toolbar.add(btnAdd); toolbar.add(btnEdit);
         toolbar.add(btnDelete); toolbar.add(btnReset);
         tableCard.add(toolbar, BorderLayout.SOUTH);
         outer.add(tableCard, BorderLayout.CENTER);

         refreshProductTable();

         btnAdd.addActionListener(e -> showProductDialog(null));
         btnEdit.addActionListener(e -> {
             int row = tblProduct.getSelectedRow();
             if (row < 0) { warn("Vui lòng chọn sản phẩm cần sửa!"); return; }
             String id = (String) tblProductModel.getValueAt(tblProduct.convertRowIndexToModel(row), 0);
             products.stream().filter(p -> p.id.equals(id)).findFirst().ifPresent(p -> showProductDialog(p));
         });
         btnDelete.addActionListener(e -> deleteSelectedProduct());
         btnReset.addActionListener(e -> {
             fSearch.setText(""); cbCat.setSelectedIndex(0); refreshProductTable();
         });

         fSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
             public void insertUpdate(javax.swing.event.DocumentEvent e)  { filterProduct(fSearch.getText(), (String)cbCat.getSelectedItem()); }
             public void removeUpdate(javax.swing.event.DocumentEvent e)  { filterProduct(fSearch.getText(), (String)cbCat.getSelectedItem()); }
             public void changedUpdate(javax.swing.event.DocumentEvent e) {}
         });
         cbCat.addActionListener(e -> filterProduct(fSearch.getText(), (String)cbCat.getSelectedItem()));
         return outer;
     }

     void refreshProductTable() {
         if (tblProductModel == null) return;
         tblProductModel.setRowCount(0);
         for (Product p : products) {
             tblProductModel.addRow(new Object[]{
                 p.id, p.name, p.category, p.brand,
                 String.format("%,.0f", p.price),
                 p.quantity, p.unit, p.minStock, p.statusText()
             });
         }
         refreshDashboard();
     }

     void filterProduct(String kw, String cat) {
         if (tblProductModel == null) return;
         tblProductModel.setRowCount(0);
         String k = kw.trim().toLowerCase();
         for (Product p : products) {
             boolean okKw  = k.isEmpty() || p.name.toLowerCase().contains(k)
                 || p.id.toLowerCase().contains(k) || p.brand.toLowerCase().contains(k);
             boolean okCat = "Tất cả danh mục".equals(cat) || p.category.equals(cat);
             if (okKw && okCat) {
                 tblProductModel.addRow(new Object[]{
                     p.id, p.name, p.category, p.brand,
                     String.format("%,.0f", p.price),
                     p.quantity, p.unit, p.minStock, p.statusText()
                 });
             }
         }
     }

     void showProductDialog(Product existing) {
         boolean isEdit = (existing != null);
         JDialog dlg = new JDialog(this, isEdit ? "Sửa sản phẩm" : "Thêm sản phẩm mới", true);
         dlg.setSize(500, 540);
         dlg.setLocationRelativeTo(this);
         dlg.getContentPane().setBackground(C_WHITE);

         JPanel panel = new JPanel(new GridBagLayout());
         panel.setBackground(C_WHITE);
         panel.setBorder(new EmptyBorder(24,28,20,28));
         GridBagConstraints g = new GridBagConstraints();
         g.fill = GridBagConstraints.HORIZONTAL;
         g.insets = new Insets(5,4,5,4);

         JTextField fName  = buildTextField("", 0);
         JTextField fBrand = buildTextField("", 0);
         JTextField fPrice = buildTextField("", 0);
         JTextField fQty   = buildTextField("", 0);
         JTextField fMin   = buildTextField("", 0);
         JTextField fDesc  = buildTextField("", 0);
         JComboBox<String> cbCat  = buildCombo(new String[]{"Điện thoại","Laptop","Máy tính bảng","Tai nghe","Đồng hồ TM","Tivi","Phụ kiện","Lưu trữ"});
         JComboBox<String> cbUnit = buildCombo(new String[]{"Chiếc","Bộ","Hộp","Cái"});

         if (isEdit) {
             fName.setText(existing.name); fBrand.setText(existing.brand);
             fPrice.setText(String.valueOf((long) existing.price));
             fQty.setText(String.valueOf(existing.quantity));
             fMin.setText(String.valueOf(existing.minStock));
             fDesc.setText(existing.description);
             cbCat.setSelectedItem(existing.category);
             cbUnit.setSelectedItem(existing.unit);
         }

         g.gridx = 0; g.gridy = 0; g.gridwidth = 2; g.insets = new Insets(0,4,16,4);
         JLabel dlgTitle = new JLabel(isEdit ? "Chỉnh sửa thông tin sản phẩm" : "Thêm sản phẩm mới vào kho");
         dlgTitle.setFont(F_H2); dlgTitle.setForeground(C_TEXT);
         panel.add(dlgTitle, g);
         g.gridwidth = 1; g.insets = new Insets(5,4,5,4);

         Object[][] fields = {
             {"Tên sản phẩm *",  fName},  {"Danh mục *",     cbCat},
             {"Thương hiệu *",   fBrand}, {"Đơn giá (đ) *",  fPrice},
             {"Số lượng *",      fQty},   {"Tồn tối thiểu",  fMin},
             {"Đơn vị",          cbUnit}, {"Mô tả",          fDesc},
         };
         for (int i = 0; i < fields.length; i++) {
             g.gridx = 0; g.gridy = i+1; g.weightx = 0.36;
             JLabel lbl = new JLabel((String) fields[i][0]);
             lbl.setFont(F_LABEL); lbl.setForeground(C_TEXT);
             panel.add(lbl, g);
             g.gridx = 1; g.weightx = 0.64;
             panel.add((Component) fields[i][1], g);
         }

         JLabel lblErr = new JLabel(" ");
         lblErr.setFont(F_SMALL); lblErr.setForeground(C_RED);
         g.gridx = 0; g.gridy = fields.length+1; g.gridwidth = 2; g.insets = new Insets(8,4,0,4);
         panel.add(lblErr, g);

         JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
         btnRow.setBackground(C_WHITE);
         JButton btnCancel = createBtn("Hủy",    C_BG,   C_TEXT2);
         JButton btnSave   = createBtn(isEdit ? "Lưu thay đổi" : "Thêm sản phẩm", C_BLUE, C_WHITE);
         btnRow.add(btnCancel); btnRow.add(btnSave);
         g.gridy = fields.length+2; g.insets = new Insets(12,4,0,4);
         panel.add(btnRow, g);

         btnCancel.addActionListener(e -> dlg.dispose());
         btnSave.addActionListener(e -> {
             try {
                 String name  = fName.getText().trim();
                 String brand = fBrand.getText().trim();
                 if (name.isEmpty() || brand.isEmpty()) { lblErr.setText("Vui lòng nhập đầy đủ thông tin!"); return; }
                 double price = Double.parseDouble(fPrice.getText().trim());
                 int qty = Integer.parseInt(fQty.getText().trim());
                 int min = fMin.getText().trim().isEmpty() ? 5 : Integer.parseInt(fMin.getText().trim());
                 if (price < 0 || qty < 0) { lblErr.setText("Giá và số lượng phải >= 0!"); return; }
                 if (isEdit) {
                     existing.name = name; existing.brand = brand;
                     existing.price = price; existing.quantity = qty; existing.minStock = min;
                     existing.category    = (String) cbCat.getSelectedItem();
                     existing.unit        = (String) cbUnit.getSelectedItem();
                     existing.description = fDesc.getText().trim();
                     setStatus("Đã cập nhật: " + name);
                 } else {
                     products.add(new Product(name,
                         (String) cbCat.getSelectedItem(), brand, price, qty, min,
                         (String) cbUnit.getSelectedItem(), fDesc.getText().trim()));
                     setStatus("Đã thêm: " + name);
                 }
                 refreshProductTable();
                 saveData();
                 dlg.dispose();
             } catch (NumberFormatException ex) {
                 lblErr.setText("Giá và số lượng phải là số hợp lệ!");
             }
         });

         dlg.setContentPane(panel);
         dlg.setVisible(true);
     }

     void deleteSelectedProduct() {
         int row = tblProduct.getSelectedRow();
         if (row < 0) { warn("Vui lòng chọn sản phẩm cần xóa!"); return; }
         String id = (String) tblProductModel.getValueAt(tblProduct.convertRowIndexToModel(row), 0);
         products.stream().filter(p -> p.id.equals(id)).findFirst().ifPresent(p -> {
             int c = JOptionPane.showConfirmDialog(this,
                 "Xóa sản phẩm: " + p.name + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
             if (c == JOptionPane.YES_OPTION) {
                 products.remove(p); refreshProductTable();
                 setStatus("Đã xóa: " + p.name); saveData();
             }
         });
     }

     // ------- NHẬP KHO -------
     JPanel buildImportPanel() {
         importPanel = buildStockPanel("Nhập kho", Transaction.Type.NHAP);
         return importPanel;
     }
     void rebuildImport() {
         contentArea.remove(importPanel);
         importPanel = buildStockPanel("Nhập kho", Transaction.Type.NHAP);
         contentArea.add(importPanel, "import");
     }

     // ------- XUẤT KHO -------
     JPanel buildExportPanel() {
         exportPanel = buildStockPanel("Xuất kho", Transaction.Type.XUAT);
         return exportPanel;
     }
     void rebuildExport() {
         contentArea.remove(exportPanel);
         exportPanel = buildStockPanel("Xuất kho", Transaction.Type.XUAT);
         contentArea.add(exportPanel, "export");
     }

     // ------- PANEL NHẬP/XUẤT CHUNG -------
     JPanel buildStockPanel(String titleText, Transaction.Type type) {
         JPanel outer = new JPanel(new BorderLayout(0,16));
         outer.setBackground(C_BG);
         outer.setBorder(new EmptyBorder(24,24,24,24));

         JLabel title = new JLabel(titleText);
         title.setFont(F_TITLE); title.setForeground(C_TEXT);
         JLabel sub = new JLabel(type == Transaction.Type.NHAP
             ? "Nhập hàng vào kho — số lượng tồn kho sẽ tăng lên"
             : "Xuất hàng khỏi kho — số lượng tồn kho sẽ giảm đi");
         sub.setFont(F_BODY); sub.setForeground(C_TEXT2);
         JPanel hdr = new JPanel();
         hdr.setBackground(C_BG);
         hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
         hdr.add(title); hdr.add(Box.createVerticalStrut(3)); hdr.add(sub);
         outer.add(hdr, BorderLayout.NORTH);

         JPanel card = new JPanel(new GridBagLayout()) {
             @Override protected void paintComponent(Graphics g) {
                 Graphics2D g2 = (Graphics2D) g.create();
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                 g2.setColor(C_WHITE); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                 g2.setColor(C_BORDER); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                 g2.dispose(); super.paintComponent(g);
             }
         };
         card.setOpaque(false);
         card.setBorder(new EmptyBorder(28,36,28,36));

         GridBagConstraints g = new GridBagConstraints();
         g.fill = GridBagConstraints.HORIZONTAL;
         g.insets = new Insets(9,4,9,4);

         // Combo chọn sản phẩm: "MA - TEN"
         String[] pLabels = products.stream()
             .map(p -> p.id + "  —  " + p.name)
             .toArray(String[]::new);
         JComboBox<String> cbProd = buildCombo(
             pLabels.length > 0 ? pLabels : new String[]{"Chưa có sản phẩm"});

         // Dòng hiển thị mã + tên sau khi chọn
         JLabel lblSelName = new JLabel("Chưa chọn sản phẩm");
         lblSelName.setFont(F_BODY); lblSelName.setForeground(C_TEXT2);

         // Dòng hiển thị tồn kho
         JLabel lblStock = new JLabel(" ");
         lblStock.setFont(F_BODY); lblStock.setForeground(C_BLUE);

         Runnable updateInfo = () -> {
             int idx = cbProd.getSelectedIndex();
             if (idx >= 0 && idx < products.size()) {
                 Product p = products.get(idx);
                 lblSelName.setForeground(C_TEXT);
                 lblSelName.setText("Mã: " + p.id + "    |    Tên: " + p.name + "    |    Thương hiệu: " + p.brand);
                 Color sc = p.isEmpty() ? C_RED : p.isLow() ? C_AMBER : C_GREEN;
                 lblStock.setForeground(sc);
                 lblStock.setText("Tồn kho hiện tại: " + p.quantity + " " + p.unit
                     + "    |    Tối thiểu: " + p.minStock
                     + "    |    " + p.statusText());
             }
         };
         cbProd.addActionListener(e -> updateInfo.run());
         if (!products.isEmpty()) updateInfo.run();

         JTextField fQty  = buildTextField("Nhập số lượng...", 0);
         JTextField fNote = buildTextField("Ghi chú (tùy chọn)...", 0);

         Object[][] rows = {
             {"Chọn sản phẩm",       cbProd},
             {"Thông tin sản phẩm",  lblSelName},
             {"Tồn kho",             lblStock},
             {"Số lượng *",          fQty},
             {"Ghi chú",             fNote},
         };
         for (int i = 0; i < rows.length; i++) {
             g.gridx = 0; g.gridy = i; g.weightx = 0.28;
             JLabel lbl = new JLabel((String) rows[i][0]);
             lbl.setFont(F_LABEL); lbl.setForeground(C_TEXT);
             card.add(lbl, g);
             g.gridx = 1; g.weightx = 0.72;
             card.add((Component) rows[i][1], g);
         }

         Color btnColor = (type == Transaction.Type.NHAP) ? C_GREEN : C_RED;
         String btnText = (type == Transaction.Type.NHAP) ? "Xác nhận nhập kho" : "Xác nhận xuất kho";
         JButton btnOk = createBtn(btnText, btnColor, C_WHITE);
         btnOk.setPreferredSize(new Dimension(220,40));

         JLabel lblErr = new JLabel(" ");
         lblErr.setFont(F_SMALL); lblErr.setForeground(C_RED);

         g.gridx = 1; g.gridy = rows.length; g.insets = new Insets(20,4,4,4);
         card.add(btnOk, g);
         g.gridy = rows.length+1; g.insets = new Insets(2,4,0,4);
         card.add(lblErr, g);

         btnOk.addActionListener(e -> {
             int idx = cbProd.getSelectedIndex();
             if (idx < 0 || products.isEmpty()) { lblErr.setText("Vui lòng chọn sản phẩm!"); return; }
             try {
                 int qty = Integer.parseInt(fQty.getText().trim());
                 if (qty <= 0) { lblErr.setText("Số lượng phải > 0!"); return; }
                 Product p = products.get(idx);
                 if (type == Transaction.Type.XUAT && qty > p.quantity) {
                     lblErr.setText("Số lượng xuất vượt tồn kho hiện tại (" + p.quantity + ")!"); return;
                 }
                 p.quantity += (type == Transaction.Type.NHAP ? qty : -qty);
                 transactions.add(new Transaction(p.id, p.name, type, qty, fNote.getText().trim(), currentUser.username));
                 refreshProductTable();
                 updateInfo.run();
                 saveData();
                 fQty.setText(""); fNote.setText(""); lblErr.setText(" ");
                 String act = (type == Transaction.Type.NHAP) ? "NHẬP" : "XUẤT";
                 JOptionPane.showMessageDialog(this,
                     act + " KHO THÀNH CÔNG\n"
                     + "Sản phẩm : " + p.name + "\n"
                     + "Số lượng  : " + qty + " " + p.unit + "\n"
                     + "Tồn kho mới: " + p.quantity + " " + p.unit,
                     "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                 setStatus((type == Transaction.Type.NHAP ? "Nhập" : "Xuất") + " kho: " + p.name + " x " + qty);
             } catch (NumberFormatException ex) {
                 lblErr.setText("Số lượng phải là số nguyên hợp lệ!");
             }
         });

         JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
         wrap.setBackground(C_BG);
         card.setPreferredSize(new Dimension(720, 390));
         wrap.add(card);
         outer.add(wrap, BorderLayout.CENTER);
         return outer;
     }

     // ------- GIAO DỊCH -------
     JPanel buildTransPanel() {
         JPanel outer = new JPanel(new BorderLayout(0,14));
         outer.setBackground(C_BG); outer.setBorder(new EmptyBorder(24,24,24,24));
         
         JPanel topRow = new JPanel(new BorderLayout(10,0));
         topRow.setBackground(C_BG);
         JLabel title = new JLabel("Lịch sử giao dịch");
         title.setFont(F_TITLE); title.setForeground(C_TEXT);
         
         JPanel srch = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
         srch.setBackground(C_BG);
         JComboBox<String> cbType = buildCombo(new String[]{"Tất cả giao dịch", "Chỉ Nhập kho", "Chỉ Xuất kho"});
         cbType.setPreferredSize(new Dimension(150, 34));
         JTextField fSearch = buildTextField("Tìm mã GD, tên SP, người, ngày...", 250);
         JButton btnExport = createBtn("Xuất Excel (CSV)", C_GREEN, C_WHITE);
         
         srch.add(cbType); srch.add(fSearch); srch.add(btnExport);
         topRow.add(title, BorderLayout.WEST); topRow.add(srch, BorderLayout.EAST);
         outer.add(topRow, BorderLayout.NORTH);

         JPanel card = whiteCard("Tất cả giao dịch nhập / xuất kho");
         String[] cols = {"Mã GD","Mã SP","Tên sản phẩm","Loại","Số lượng","Người thực hiện","Ngày giờ","Ghi chú"};
         tblTransModel = new DefaultTableModel(cols, 0) {
             @Override public boolean isCellEditable(int r, int c) { return false; }
         };
         tblTrans = buildTable(tblTransModel);
         setColWidths(tblTrans, new int[]{65,65,190,90,75,110,140,165});

         tblTrans.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
             @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                 super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                 setBorder(new EmptyBorder(0,10,0,10));
                 String s = v == null ? "" : v.toString();
                 setForeground(s.contains("Nhập") ? C_GREEN : C_RED);
                 setBackground(sel ? C_SEL : (r%2==0 ? C_WHITE : C_ROW_ALT));
                 setFont(new Font("Segoe UI", Font.BOLD, 12));
                 return this;
             }
         });

         card.add(styledScroll(tblTrans), BorderLayout.CENTER);
         outer.add(card, BorderLayout.CENTER);
         refreshTransTable();

         // Xử lý sự kiện tìm kiếm và lọc real-time
         Runnable applyFilter = () -> {
             String kw = fSearch.getText().trim().toLowerCase();
             if (kw.equals("tìm mã gd, tên sp, người, ngày...")) kw = "";
             String type = (String) cbType.getSelectedItem();
             
             tblTransModel.setRowCount(0);
             for (int i = transactions.size()-1; i >= 0; i--) {
                 Transaction tx = transactions.get(i);
                 boolean matchType = type.equals("Tất cả giao dịch") || 
                                    (type.equals("Chỉ Nhập kho") && tx.type == Transaction.Type.NHAP) ||
                                    (type.equals("Chỉ Xuất kho") && tx.type == Transaction.Type.XUAT);
                 boolean matchKw = kw.isEmpty() || tx.id.toLowerCase().contains(kw) || 
                                   tx.productName.toLowerCase().contains(kw) || 
                                   tx.user.toLowerCase().contains(kw) || tx.date.contains(kw);
                 
                 if (matchType && matchKw) {
                     tblTransModel.addRow(new Object[]{
                         tx.id, tx.productId, tx.productName,
                         tx.type == Transaction.Type.NHAP ? "Nhập kho" : "Xuất kho",
                         tx.quantity, tx.user, tx.date, tx.note
                     });
                 }
             }
         };

         fSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
             public void insertUpdate(javax.swing.event.DocumentEvent e)  { applyFilter.run(); }
             public void removeUpdate(javax.swing.event.DocumentEvent e)  { applyFilter.run(); }
             public void changedUpdate(javax.swing.event.DocumentEvent e) {}
         });
         cbType.addActionListener(e -> applyFilter.run());

         // Xử lý sự kiện xuất file
         btnExport.addActionListener(e -> exportTransToCSV());

         return outer;
     }

     // Thêm hàm này ngay dưới buildTransPanel
     void exportTransToCSV() {
         if (tblTransModel.getRowCount() == 0) { warn("Không có dữ liệu để xuất!"); return; }
         JFileChooser fileChooser = new JFileChooser();
         fileChooser.setDialogTitle("Chọn nơi lưu báo cáo giao dịch");
         fileChooser.setSelectedFile(new File("LichSuGiaoDich_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv"));
         
         if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
             try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileChooser.getSelectedFile()), "UTF-8"))) {
                 pw.write('\ufeff'); // Chèn BOM để Excel đọc tiếng Việt UTF-8 không bị lỗi font
                 pw.println("Mã GD,Mã SP,Tên sản phẩm,Loại,Số lượng,Người thực hiện,Ngày giờ,Ghi chú");
                 
                 for (int r = 0; r < tblTransModel.getRowCount(); r++) {
                     StringBuilder row = new StringBuilder();
                     for (int c = 0; c < tblTransModel.getColumnCount(); c++) {
                         String val = tblTransModel.getValueAt(r, c) != null ? tblTransModel.getValueAt(r, c).toString() : "";
                         row.append("\"").append(val.replace("\"", "\"\"")).append("\"");
                         if (c < tblTransModel.getColumnCount() - 1) row.append(",");
                     }
                     pw.println(row.toString());
                 }
                 JOptionPane.showMessageDialog(this, "Xuất báo cáo thành công!", "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
             } catch (Exception ex) {
                 warn("Lỗi khi xuất file: " + ex.getMessage());
             }
         }
     }
     void refreshTransTable() {
    if (tblTransModel == null) return;
    tblTransModel.setRowCount(0);
    for (int i = transactions.size()-1; i >= 0; i--) {
        Transaction tx = transactions.get(i);
        tblTransModel.addRow(new Object[]{
            tx.id, tx.productId, tx.productName,
            tx.type == Transaction.Type.NHAP ? "Nhập kho" : "Xuất kho",
            tx.quantity, tx.user, tx.date, tx.note // Hiển thị tx.user và tx.date
        });
    }
}

     // ------- THỐNG KÊ -------
     JPanel buildStatsPanel() {
         JPanel outer = new JPanel(new BorderLayout(0,18));
         outer.setBackground(C_BG);
         outer.setBorder(new EmptyBorder(24,24,24,24));
         JLabel title = new JLabel("Thống kê & Biểu đồ kho hàng");
         title.setFont(F_TITLE); title.setForeground(C_TEXT);
         outer.add(title, BorderLayout.NORTH);

         // Phần Bảng (Bên trên)
         JPanel grid = new JPanel(new GridLayout(1,2,16,0));
         grid.setBackground(C_BG);
         grid.setPreferredSize(new Dimension(0, 250));

         JPanel cardCat = whiteCard("Thống kê theo danh mục");
         DefaultTableModel mCat = new DefaultTableModel(new String[]{"Danh mục","Số SP","Tổng tồn","Giá trị kho (đ)"}, 0) {
             @Override public boolean isCellEditable(int r, int c) { return false; }
         };
         JTable tCat = buildTable(mCat);
         setColWidths(tCat, new int[]{150,65,80,170});
         cardCat.add(styledScroll(tCat), BorderLayout.CENTER);

         JPanel cardLow = whiteCard("Sản phẩm cần bổ sung hàng");
         DefaultTableModel mLow = new DefaultTableModel(new String[]{"Mã SP","Tên sản phẩm","Tồn kho","Tối thiểu"}, 0) {
             @Override public boolean isCellEditable(int r, int c) { return false; }
         };
         JTable tLow = buildTable(mLow);
         setColWidths(tLow, new int[]{75,210,80,80});
         cardLow.add(styledScroll(tLow), BorderLayout.CENTER);

         grid.add(cardCat); grid.add(cardLow);
         
         // Phần Biểu đồ (Bên dưới)
         JPanel chartCard = whiteCard("Biểu đồ Giá trị tồn kho theo Danh mục (VNĐ)");
         chartView = new JPanel() {
             @Override protected void paintComponent(Graphics gx) {
                 super.paintComponent(gx);
                 Graphics2D g2 = (Graphics2D) gx.create();
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                 // Lấy dữ liệu
                 Map<String, Double> vals = new HashMap<>();
                 double max = 0;
                 for (Product p : products) {
                     double v = p.price * p.quantity;
                     vals.put(p.category, vals.getOrDefault(p.category, 0.0) + v);
                 }
                 for (double v : vals.values()) if (v > max) max = v;

                 if (max == 0 || vals.isEmpty()) {
                     g2.setColor(C_TEXT3);
                     g2.drawString("Chưa có dữ liệu thống kê", getWidth()/2 - 60, getHeight()/2);
                     return;
                 }

                 int w = getWidth(), h = getHeight();
                 int padding = 40;
                 int graphW = w - 2*padding, graphH = h - 2*padding;

                 // Vẽ đường nền
                 g2.setColor(C_BORDER);
                 g2.drawLine(padding, h-padding, w-padding, h-padding);

                 // Vẽ cột
                 int numBars = vals.size();
                 int barWidth = Math.min(60, graphW / numBars / 2);
                 int spacing = (graphW - (barWidth * numBars)) / (numBars + 1);
                 int x = padding + spacing;
                 Color[] colors = {C_BLUE, C_GREEN, C_AMBER, new Color(139, 92, 246), new Color(236, 72, 153)};
                 int cIdx = 0;

                 for (Map.Entry<String, Double> e : vals.entrySet()) {
                     int barH = (int) ((e.getValue() / max) * graphH);
                     int y = h - padding - barH;

                     g2.setColor(colors[cIdx % colors.length]);
                     g2.fillRoundRect(x, y, barWidth, barH, 6, 6);

                     // Chữ ở dưới cột
                     g2.setColor(C_TEXT2); g2.setFont(F_SMALL);
                     String label = e.getKey();
                     int lw = g2.getFontMetrics().stringWidth(label);
                     g2.drawString(label, x + (barWidth - lw)/2, h - padding + 15);
                     
                     x += barWidth + spacing;
                     cIdx++;
                 }
                 g2.dispose();
             }
         };
         chartView.setBackground(C_WHITE);
         chartCard.add(chartView, BorderLayout.CENTER);

         JPanel centerSplit = new JPanel(new BorderLayout(0, 16));
         centerSplit.setBackground(C_BG);
         centerSplit.add(grid, BorderLayout.NORTH);
         centerSplit.add(chartCard, BorderLayout.CENTER);

         outer.putClientProperty("mCat", mCat);
         outer.putClientProperty("mLow", mLow);
         outer.add(centerSplit, BorderLayout.CENTER);

         refreshStatsPanel(outer);
         return outer;
     }

     void refreshStats() {
         for (Component c : contentArea.getComponents()) {
             if (c instanceof JPanel) {
                 JPanel p = (JPanel) c;
                 if (p.getClientProperty("mCat") instanceof DefaultTableModel) {
                     refreshStatsPanel(p);
                 }
             }
         }
     }

     void refreshStatsPanel(JPanel panel) {
         DefaultTableModel mCat = (DefaultTableModel) panel.getClientProperty("mCat");
         DefaultTableModel mLow = (DefaultTableModel) panel.getClientProperty("mLow");
         if (mCat == null || mLow == null) return;
         
         mCat.setRowCount(0);
         products.stream().collect(Collectors.groupingBy(p -> p.category))
             .forEach((cat, list) -> {
                 int tot    = list.stream().mapToInt(p -> p.quantity).sum();
                 double val = list.stream().mapToDouble(p -> p.price * p.quantity).sum();
                 mCat.addRow(new Object[]{cat, list.size(), tot, String.format("%,.0f", val)});
             });
             
         mLow.setRowCount(0);
         products.stream().filter(Product::isLow)
             .forEach(p -> mLow.addRow(new Object[]{p.id, p.name, p.quantity, p.minStock}));
             
         // Lệnh vẽ lại biểu đồ mỗi khi data cập nhật
         if (chartView != null) chartView.repaint(); 
     }

     // ------- HÀM TIỆN ÍCH -------
     JPanel whiteCard(String title) {
         JPanel card = new JPanel(new BorderLayout()) {
             @Override protected void paintComponent(Graphics g) {
                 Graphics2D g2 = (Graphics2D) g.create();
                 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                 g2.setColor(C_WHITE); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                 g2.setColor(C_BORDER); g2.setStroke(new BasicStroke(1f));
                 g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                 g2.dispose(); super.paintComponent(g);
             }
         };
         card.setOpaque(false);
         JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.LEFT,16,10));
         titleBar.setBackground(C_WHITE);
         titleBar.setBorder(BorderFactory.createMatteBorder(0,0,1,0,C_BORDER));
         JLabel lbl = new JLabel(title);
         lbl.setFont(F_H2); lbl.setForeground(C_TEXT);
         titleBar.add(lbl);
         card.add(titleBar, BorderLayout.NORTH);
         return card;
     }

     JTable buildTable(DefaultTableModel model) {
         JTable t = new JTable(model);
         t.setBackground(C_WHITE); t.setForeground(C_TEXT);
         t.setFont(F_BODY); t.setRowHeight(36);
         t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0,0));
         t.setSelectionBackground(C_SEL); t.setSelectionForeground(C_TEXT);
         t.setFillsViewportHeight(true);
         t.getTableHeader().setBackground(new Color(248,250,252));
         t.getTableHeader().setForeground(C_TEXT2);
         t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
         t.getTableHeader().setReorderingAllowed(false);
         t.getTableHeader().setPreferredSize(new Dimension(0,38));
         t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0,C_BORDER));
         t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
             @Override public Component getTableCellRendererComponent(
                     JTable tbl, Object v, boolean sel, boolean foc, int r, int c) {
                 super.getTableCellRendererComponent(tbl, v, sel, foc, r, c);
                 setBorder(new EmptyBorder(0,12,0,12));
                 setFont(F_BODY);
                 setBackground(sel ? C_SEL : (r%2==0 ? C_WHITE : C_ROW_ALT));
                 setForeground(C_TEXT);
                 if (v != null) {
                     switch (v.toString()) {
                         case "Hết hàng": setForeground(C_RED);   setFont(F_LABEL); break;
                         case "Sắp hết":  setForeground(C_AMBER); setFont(F_LABEL); break;
                         case "Còn hàng": setForeground(C_GREEN); setFont(F_LABEL); break;
                     }
                 }
                 return this;
             }
         });
         t.setRowSorter(new TableRowSorter<>(model));
         return t;
     }

     JScrollPane styledScroll(JTable t) {
         JScrollPane sp = new JScrollPane(t);
         sp.setBackground(C_WHITE); sp.getViewport().setBackground(C_WHITE);
         sp.setBorder(BorderFactory.createEmptyBorder());
         return sp;
     }

     JTextField buildTextField(String placeholder, int w) {
         JTextField f = new JTextField();
         f.setFont(F_BODY); f.setBackground(C_WHITE);
         f.setForeground(C_TEXT); f.setCaretColor(C_BLUE);
         f.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createLineBorder(C_BORDER, 1),
             new EmptyBorder(6,10,6,10)
         ));
         f.setPreferredSize(new Dimension(w > 0 ? w : 300, 34));
         if (!placeholder.isEmpty()) {
             f.setText(placeholder); f.setForeground(C_TEXT3);
             f.addFocusListener(new FocusAdapter() {
                 @Override public void focusGained(FocusEvent e) {
                     if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(C_TEXT); }
                 }
                 @Override public void focusLost(FocusEvent e) {
                     if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(C_TEXT3); }
                 }
             });
         }
         return f;
     }

     JComboBox<String> buildCombo(String[] items) {
         JComboBox<String> cb = new JComboBox<>(items);
         cb.setFont(F_BODY); cb.setBackground(C_WHITE);
         cb.setForeground(C_TEXT);
         cb.setPreferredSize(new Dimension(300, 34));
         return cb;
     }

     void setColWidths(JTable t, int[] ws) {
         for (int i = 0; i < ws.length && i < t.getColumnCount(); i++)
             t.getColumnModel().getColumn(i).setPreferredWidth(ws[i]);
     }

     void warn(String msg) {
         JOptionPane.showMessageDialog(this, msg, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
     }
 }

 // ------- NÚT BUTTON DÙNG CHUNG -------
 static JButton createBtn(String text, Color bg, Color fg) {
     JButton btn = new JButton(text) {
         @Override protected void paintComponent(Graphics g) {
             Graphics2D g2 = (Graphics2D) g.create();
             g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             Color c = getModel().isPressed()  ? bg.darker()
                     : getModel().isRollover() ? bg.brighter() : bg;
             g2.setColor(c); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
             g2.setColor(C_BORDER); g2.setStroke(new BasicStroke(1f));
             g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
             g2.dispose(); super.paintComponent(g);
         }
     };
     btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
     btn.setForeground(fg);
     btn.setOpaque(false); btn.setContentAreaFilled(false);
     btn.setBorderPainted(false); btn.setFocusPainted(false);
     btn.setBorder(new EmptyBorder(7,16,7,16));
     btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
     return btn;
 }
}
